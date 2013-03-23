//
//  MainController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//
//  ARC enabled
//

#import "MainController.h"

#import "PluginController.h"

#import "PluginNavigationController.h"

#import "PluginSplitViewController.h"

#import "MainMenuViewController.h"

#import "MainMenuItem.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "SplashViewController.h"

#import "GlobalSettingsViewController.h"

#import "ObjectArchiver.h"

#import "PCConfig.h"

#import "PCURLSchemeHandler.h"

#import <objc/message.h>

@interface MainController ()

@property (nonatomic, weak) UIWindow* window;
@property (nonatomic, strong) PCURLSchemeHandler* urlSchemeHander;
@property (nonatomic, strong) MainMenuViewController* mainMenuViewController;
@property (nonatomic, strong) ZUUIRevealController* revealController;
@property (nonatomic) CGFloat revealWidth;
@property (nonatomic, strong) NSDictionary* plistDicForPluginIdentifier;
@property (nonatomic, strong) NSArray* pluginsList;
@property (nonatomic, strong) SplashViewController* splashViewController;
@property (nonatomic, weak) PluginController<PluginControllerProtocol>* activePluginController;
@property (nonatomic, strong) NSString* initialActivePluginIdentifier;
@property (nonatomic, strong) NSURL* pcURLToHandle;
@property (nonatomic, strong) NSMutableDictionary* pluginsControllers; //key: plugin identifier name, value: PluginController subclass.
@property (nonatomic) BOOL initDone;

@end

/* Corresponds to supportedIdioms possible values in config file Plugins.plist */
static NSString* kSupportedIdiomPhone = @"phone";
static NSString* kSupportedIdiomPad = @"pad";
static NSString* kSupportedIdiomPhonePad = @"phone+pad";

static NSString* kPluginsMainMenuItemsInfoKey = @"pluginsMainMenuItemsInfo";
static NSString* kPluginsMainMenuItemsInfoOrderNumberKey = @"pluginsMainMenuItemsInfoOrderNumber";
static NSString* kPluginsMainMenuItemsInfoHiddenKey = @"pluginsMainMenuItemsInfoHidden";

static int kGesturesViewTag = 50;

/* 
 * If YES, plugins are kept alive when switching among them.
 * Otherwise, only foreground is kept alive (allocated).
 * IMPORTANT: background management is dummy and not well tested for now (no smart memory management)
 */
static BOOL BACKGROUND_PLUGINS_ENABLED = NO;

static MainController<MainControllerPublic>* instance = nil;

@implementation MainController

- (id)initWithWindow:(UIWindow *)window
{
    self = [super init];
    if (self) {
        self.window = window;
        self.urlSchemeHander = [[PCURLSchemeHandler alloc] initWithMainController:self];
        self.activePluginController = nil;
        [self initPluginsList];
        self.pluginsControllers = [NSMutableDictionary dictionaryWithCapacity:self.pluginsList.count];
        [self initPluginObservers];
        [self initMainMenu];
        if ([PCUtils isIdiomPad]) {
            self.revealWidth = 320.0;
        } else {
            self.revealWidth = 261.0;
        }
        [self initRevealController];
        self.revealController.maxRearViewRevealOverdraw = 0.0;
        if ([PCUtils isIdiomPad]) {
            self.revealController.toggleAnimationDuration = 0.65;
        } else {
            self.revealController.toggleAnimationDuration = 0.65;
        }
        self.revealController.delegate = self;
        self.window.rootViewController = self.revealController;
        instance = self;
        //[NSTimer scheduledTimerWithTimeInterval:1.5 target:self selector:@selector(test) userInfo:nil repeats:NO];
    }
    return self;
}

- (void)test {
    [self requestPluginToForeground:@"Directory"];
}

+ (id<MainControllerPublic>)publicController {
    return instance;
}

#pragma mark - MainControllerPublic

- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName {
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifierName];
    
    /* If not BACKGROUND_PLUGINS_ENABLED, check that active plugin controller can be released */
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:pluginIdentifierName];
    if (!BACKGROUND_PLUGINS_ENABLED && self.activePluginController == pluginController && [pluginController respondsToSelector:@selector(canBeReleased)] && ![pluginController canBeReleased]) {
        return NO;
    }
    
    if (self.activePluginController) { /* current showing a plugin */
        if (![[self identifierNameForPluginController:self.activePluginController] isEqualToString:pluginIdentifierName]) {
            [self setActivePluginWithIdentifier:pluginIdentifierName];
        }
    } else if (self.initDone) { /* current showing splash (no active plugin) */
        [self setActivePluginWithIdentifier:pluginIdentifierName];
    } else { /* app is starting */
        self.initialActivePluginIdentifier = pluginIdentifierName;
    }
    return YES;
}

- (BOOL)requestLeavePlugin:(NSString*)pluginIdentifierName {
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifierName];
    
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:pluginIdentifierName];
    
    if (!pluginController) {
        return YES; //plugin is not allocated and not active => desired effect achieved
    }
    
    if ([pluginController respondsToSelector:@selector(canBeReleased)] && ![pluginController canBeReleased]) {
        return NO;
    }
    
    if (self.activePluginController && [[self identifierNameForPluginController:self.activePluginController] isEqualToString:pluginIdentifierName]) {
        [self setActivePluginWithIdentifier:nil];
    }
    
    [self.pluginsControllers removeObjectForKey:pluginIdentifierName]; //already been done by setActivePluginIdentifer if not BACKGROUND_PLUGINS_ENABLED
    
    return YES;
}

- (void)addPluginStateObserver:(id)observer selector:(SEL)selector notification:(PluginStateNotification)notification pluginIdentifierName:(NSString*)pluginIdentifierName {
    if (!observer) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"observer cannot be nil" userInfo:nil];
    }
    if (![observer respondsToSelector:selector]) {
        NSLog(@"!! Warning: observer %@ does not respond to selector %@. Crash will occur when notification is posted.", observer, NSStringFromSelector(selector));
    }
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifierName];
    
    NSString* name = [self notificiationNameForPluginStateNotification:notification pluginIdentifierName:pluginIdentifierName];
    [[NSNotificationCenter defaultCenter] addObserver:observer selector:selector name:name object:self];
    NSLog(@"-> %@ ('%@') registered for PluginStateNotification %d", observer, pluginIdentifierName, notification);
}

- (void)removePluginStateObserver:(id)observer {
    if (!observer) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"observer cannot be nil" userInfo:nil];
    }
    [[NSNotificationCenter defaultCenter] removeObserver:observer name:nil object:self];
    NSLog(@"-> %@ unregistered of PluginStateNotifications", observer);
}

- (PCURLSchemeHandler*)urlSchemeHandlerSharedInstance {
    return self.urlSchemeHander;
}

- (void)handlePocketCampusURL:(NSURL*)url {
    self.pcURLToHandle = url;
    if (self.initDone) {
        //not supported yet
    }
}

#pragma mark Private utilities

- (NSString*)notificiationNameForPluginStateNotification:(PluginStateNotification)notification pluginIdentifierName:(NSString*)pluginIdentifierName {
    return [NSString stringWithFormat:@"%@-%d", pluginIdentifierName, notification];
}

- (void)postNotificationWithState:(PluginStateNotification)notification pluginIdentifier:(NSString*)pluginIdentifier {
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifier];
    NSString* name = [self notificiationNameForPluginStateNotification:notification pluginIdentifierName:pluginIdentifier];
    [[NSNotificationCenter defaultCenter] postNotificationName:name object:self];
}

- (NSString*)identifierNameForPluginController:(PluginController*)pluginController {
    if ([self.activePluginController conformsToProtocol:NSProtocolFromString(@"PluginControllerProtocol")]) {
        id<PluginControllerProtocol> pluginController = self.activePluginController;
        NSString* identifier = [[pluginController class] identifierName];
        return identifier;
    }
    return nil;
}

/*
 * Can be called only after self.pluginsList has been filled
 */
- (void)throwExceptionIfPluginIdentifierNameIsNotValid:(NSString*)identifier {
    if (!identifier) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"pluginIdentifierName cannot be nil." userInfo:nil];
    }
    if (![identifier isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"pluginIdentifierName is not kind of class NSString." userInfo:nil];
    }
    if (![self.pluginsList containsObject:identifier]) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"pluginIdentifierName does not correspond to any existing identifier. Please use [PluginControllerProtocol identifierName] as argument." userInfo:nil];
    }
}

- (void)selectActivePluginInMainMenuIfNecessary {
    if (self.activePluginController) {
        NSString* activePluginIdentifier = [self.activePluginController.class identifierName];
        [self.mainMenuViewController setSelectedPluginWithIdentifier:activePluginIdentifier animated:YES];
    }
}

#pragma mark - Inititalizations

- (void)initPluginsList {
    
    //Loading plugins list from Plugins.plist
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Plugins" ofType:@"plist"]];
    NSArray* pluginsFromPlist = [plist objectForKey:@"Plugins"];
    NSMutableArray* pluginsTempArray = [NSMutableArray arrayWithCapacity:pluginsFromPlist.count];
    NSMutableDictionary* tempPlistDicForPluginIdentifier = [NSMutableDictionary dictionaryWithCapacity:pluginsFromPlist.count];
    
    //Plugins list gotten from server
    NSArray* pluginsFromServer = [[PCConfig defaults] objectForKey:PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY];
    
    
    BOOL isPadIdiom = [PCUtils isIdiomPad];
    
    
    for (NSDictionary* pluginDic in pluginsFromPlist) {
        NSString* identifierName = [pluginDic objectForKey:@"identifierName"];
        
        BOOL pluginEnabled = [[pluginDic objectForKey:@"enabled"] boolValue];
        if (pluginsFromServer) { //if server config available, it overrides one from Plugins.plist
            pluginEnabled = [pluginsFromServer containsObject:identifierName];
        }
        
        if (pluginEnabled) { //plugin is enabled
            NSString* idiom = [pluginDic objectForKey:@"supportedIdioms"];
            if (idiom &&
                (
                 [idiom isEqualToString:kSupportedIdiomPhonePad]
                 || (isPadIdiom && [idiom isEqualToString:kSupportedIdiomPad])
                 || (!isPadIdiom && [idiom isEqualToString:kSupportedIdiomPhone]))
                ) {
                NSLog(@"-> Detected enabled idiom-compatible plugin: '%@' (idiom '%@')", identifierName, idiom);
                [pluginsTempArray addObject:identifierName];
                tempPlistDicForPluginIdentifier[identifierName] = pluginDic;
            }
        }
    }
    
    /* sorting plugins alphabetically on plugin name. Should be on localized name, but cannot access it here */
    /*[pluginsTempArray sortWithOptions:0 usingComparator:^NSComparisonResult(id obj1, id obj2) {
     NSString* name1 = (NSString*)obj1;
     NSString* name2 = (NSString*)obj2;
     return [name1 compare:name2];
     }];*/
    
    self.pluginsList = [NSArray arrayWithArray:pluginsTempArray]; //creates a non-mutable copy of the array
    self.plistDicForPluginIdentifier = [tempPlistDicForPluginIdentifier copy]; //creates a non-mutable copy of the dictionary
}

- (void)initPluginObservers {
    for (int i = 0; i<self.pluginsList.count; i++) {
        Class pluginClass = NSClassFromString([self pluginControllerNameForIndex:i]);
        if (class_getClassMethod(pluginClass, @selector(initObservers))) {
            NSLog(@"-> Found PluginController with observers : %@", pluginClass);
            [pluginClass initObservers];
        }
    }
}

- (NSMutableArray*)defaultMainMenuItemsWithoutTopSection {
    if (!self.pluginsList) {
        return nil;
    }
    NSMutableArray* menuItems = [NSMutableArray array];
    for (NSString* pluginIdentifier in self.pluginsList) {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:pluginIdentifier]);
        NSString* localizedName = [pluginClass localizedName];
        MainMenuItem* item = [MainMenuItem menuItemButtonWithTitle:localizedName leftImage:[UIImage imageNamed:pluginIdentifier] identifier:pluginIdentifier];
        NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
        item.hidden = [hiddenByDefault boolValue];
        [menuItems addObject:item];
    }
    return menuItems;
}

- (void)initMainMenu {
    
    /* Generating menu items from pluginsList */
    
    NSMutableArray* menuItems = [self defaultMainMenuItemsWithoutTopSection];
    
    /* Restoring previous order / hidden of menu items, saved be used */
    
    NSDictionary* menuItemsInfo = (NSDictionary*)[ObjectArchiver objectForKey:kPluginsMainMenuItemsInfoKey andPluginName:@"pocketcampus"];
    
    NSMutableArray* menuItemsCopy = [menuItems mutableCopy];
    
    @try {
        for (MainMenuItem* item in menuItemsCopy) {

            NSDictionary* infos = menuItemsInfo[item.identifier];
            if (infos) {
                if (infos[kPluginsMainMenuItemsInfoOrderNumberKey]) {
                    NSUInteger index = [infos[kPluginsMainMenuItemsInfoOrderNumberKey] unsignedIntValue];
                    [menuItems removeObject:item];
                    [menuItems insertObject:item atIndex:index];
                }
                if (infos[kPluginsMainMenuItemsInfoHiddenKey]) {
                    item.hidden = [infos[kPluginsMainMenuItemsInfoHiddenKey] boolValue];
                } else {
                    NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
                    item.hidden = [hiddenByDefault boolValue];
                }
            } else {
                NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
                item.hidden = [hiddenByDefault boolValue];
            }
        }
    }
    @catch (NSException *exception) {
        menuItems = menuItemsCopy; //if anything bad happens during recovery, go back to standard order.
    }
    
    /* Adding section at beginning */
    
    MainMenuItem* pluginSectionHeader = [MainMenuItem menuItemSectionHeaderWithTitle:@"Plugins"];
    pluginSectionHeader.hidden = YES;
    
    [menuItems insertObject:pluginSectionHeader atIndex:0];
    
    MainMenuViewController* mainMenuViewController = [[MainMenuViewController alloc] initWithMenuItems:menuItems mainController:self];
    self.mainMenuViewController = mainMenuViewController;
}

- (void)initRevealController {
    self.splashViewController = [[SplashViewController alloc] initWithRightHiddenOffset:self.revealWidth];
    self.revealController = [[ZUUIRevealController alloc] initWithFrontViewController:self.splashViewController rearViewController:[[UINavigationController alloc] initWithRootViewController:self.mainMenuViewController]];
    self.revealController.rearViewRevealWidth = self.revealWidth;
}

- (void)revealMenuAfterSplash {
    if (self.initialActivePluginIdentifier) {
        self.revealController.toggleAnimationDuration = 0.25;
        self.initDone = YES; //must do it before calling setActivePluginWithIdentifier otherwise no effect
        [self setActivePluginWithIdentifier:self.initialActivePluginIdentifier];
        self.initialActivePluginIdentifier = nil; //initial plugin has been treated, prevent future use
        [self.revealController revealToggle:self];
    } else {
        [(SplashViewController*)(self.revealController.frontViewController) willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:NO];
        if ([PCUtils isIdiomPad]) {
            [self.revealController revealToggle:self];
        } else {
            [self.revealController hideFrontView];
        }
        [NSTimer scheduledTimerWithTimeInterval:self.revealController.toggleAnimationDuration+0.1 target:self selector:@selector(setInitDoneYES) userInfo:nil repeats:NO];
        self.revealController.toggleAnimationDuration = 0.25;
    }
}

/*
 * Called by timer scheduled in revealMenuAfterSplash.
 */
- (void)setInitDoneYES {
    self.initDone = YES;
    if (self.initialActivePluginIdentifier) {
        [self setActivePluginWithIdentifier:self.initialActivePluginIdentifier];
        self.initialActivePluginIdentifier = nil; //initial plugin has been treated, prevent future use
        if (self.revealController.currentFrontViewPosition != FrontViewPositionLeft) {
            [self.revealController showFrontViewCompletely:YES];
        }
    }
    if (self.pcURLToHandle) {
        //not supported yet
    }
}

#pragma mark - Called by AppDelegate

- (void)appDidReceiveMemoryWarning {
    /* release backgrounded plugins */
    NSLog(@"-> AppDidReceiveMemoryWarning: releasing backgrounded plugins if any...");
    for (PluginController* pluginController in [self.pluginsControllers copy]) {
        if (pluginController != self.activePluginController) {
            [self.pluginsControllers removeObjectForKey:pluginController];
        }
    }
}

#pragma mark - Called by MainMenuViewController

- (void)mainMenuIsReady {
    if (![self.revealController.frontViewController isViewLoaded]) {
        [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(mainMenuIsReady) userInfo:nil repeats:NO];
    } else {
        [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(revealMenuAfterSplash) userInfo:nil repeats:NO];
    }
}

- (void)mainMenuStartedEditing {
    if (![PCUtils isIdiomPad] && self.activePluginController) {
        [self.revealController hideFrontView];
    }
}

- (void)mainMenuEndedEditing {
    
    NSString* activePluginIdentifier = [self.activePluginController.class identifierName];
    
    if (self.activePluginController) {
        if (![PCUtils isIdiomPad]) {
            [self.revealController showFrontViewCompletely:NO];
        }
        [self.mainMenuViewController setSelectedPluginWithIdentifier:activePluginIdentifier animated:YES];
    }
    
    BOOL shouldLeaveActivePluginController __block = NO;
    
    NSMutableDictionary* menuItemsInfo __block = [NSMutableDictionary dictionary];
    [self.mainMenuViewController.pluginsMenuItems enumerateObjectsUsingBlock:^(MainMenuItem* item, NSUInteger index, BOOL *stop) {
        NSMutableDictionary* infos = [NSMutableDictionary dictionaryWithCapacity:2];
        infos[kPluginsMainMenuItemsInfoOrderNumberKey] = [NSNumber numberWithUnsignedInt:index];
        infos[kPluginsMainMenuItemsInfoHiddenKey] = [NSNumber numberWithBool:item.hidden];
        if ([item.identifier isEqualToString:activePluginIdentifier] && item.hidden) {
            shouldLeaveActivePluginController = YES;
        }
        menuItemsInfo[item.identifier] = infos;
    }];
    
    if (shouldLeaveActivePluginController) {
        [self requestLeavePlugin:activePluginIdentifier];
    }
    
    if (![ObjectArchiver saveObject:menuItemsInfo forKey:kPluginsMainMenuItemsInfoKey andPluginName:@"pocketcampus"]) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Sorry, an error occured while saving the main menu state." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
    }
}

- (void)restoreDefaultMainMenu {
    if (![ObjectArchiver saveObject:nil forKey:kPluginsMainMenuItemsInfoKey andPluginName:@"pocketcampus"]) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Sorry, an error occured while restoring default main menu." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        return;
    }
    NSMutableArray* menuItems = [self defaultMainMenuItemsWithoutTopSection];
    MainMenuItem* pluginSectionHeader = [MainMenuItem menuItemSectionHeaderWithTitle:@"Plugins"];
    pluginSectionHeader.hidden = YES;
    [menuItems insertObject:pluginSectionHeader atIndex:0];
    
    [self.mainMenuViewController reloadWithMenuItems:menuItems];
    
    if (![PCUtils isIdiomPad] && self.activePluginController) {
        [self.revealController showFrontViewCompletely:NO];
    }
    
    [NSTimer scheduledTimerWithTimeInterval:0.0 target:self selector:@selector(selectActivePluginInMainMenuIfNecessary) userInfo:nil repeats:NO];
}

- (void)showGlobalSettings {
    GlobalSettingsViewController* settingsViewController = [[GlobalSettingsViewController alloc] initWithMainController:self];
    UINavigationController* settingsNavController = [[UINavigationController alloc] initWithRootViewController:settingsViewController];
    settingsNavController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self.revealController presentViewController:settingsNavController animated:YES completion:NULL];
}

#pragma mark setActivePluginWithIdentifier:

- (void)setActivePluginWithIdentifier:(NSString*)identifier {
    
    if (!self.initDone) {
        self.initialActivePluginIdentifier = identifier;
        return;
    }
    
    if (!identifier) { //means switch to splash view controller
        if (self.activePluginController) {
            [self.pluginsControllers removeObjectForKey:[self.activePluginController.class identifierName]];
        }
        [self.mainMenuViewController setSelectedPluginWithIdentifier:nil animated:YES];
        [self.revealController setFrontViewController:self.splashViewController animated:NO]; //do NOT put animated YES. If YES, executed call will start asynchronous animation and following lines will exectue before instead of after.
        if ([PCUtils isIdiomPad]) {
            if (self.revealController.currentFrontViewPosition != FrontViewPositionRight) {
                [self.revealController revealToggle:self];
            }
        } else {
            if (self.revealController.currentFrontViewPosition != FrontViewPositionRightMost) {
                [self.revealController hideFrontView];
            }
        }
        return;
    }
    
    [self throwExceptionIfPluginIdentifierNameIsNotValid:identifier];
    
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:identifier];
    if (pluginController) { // pluginController was backgrounded
        UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
        [self.revealController setFrontViewController:pluginRootViewController animated:NO]; //check on whether this is already the front one is done in the method implementation
    } else {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifier]);
        if (!pluginClass) {
            @throw [NSException exceptionWithName:@"Bad plugin identifier" reason:[NSString stringWithFormat:@"Controller class does not exist for idenfier %@", identifier] userInfo:nil];
        }
        pluginController = [[pluginClass alloc] init];
        [self adaptInitializedNavigationOrSplitViewControllerOfPluginController:pluginController];
        UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
        
        if (!pluginRootViewController) {
            NSLog(@"!! ERROR: could not obtain pluginRootViewController for plugin identifier %@", identifier);
            return;
        }
        
        [self manageBackgroundPlugins];
        [self.pluginsControllers setObject:pluginController forKey:identifier];
        [self.revealController setFrontViewController:pluginRootViewController animated:NO];
    }
    [self.mainMenuViewController setSelectedPluginWithIdentifier:identifier animated:YES];
    self.activePluginController = pluginController;
    [self rotateDeviceToPortraitIfNecessary];
}


#pragma mark - PluginControllers identification

- (NSString*)pluginControllerNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (NSString*)pluginControllerNameForIndex:(NSUInteger)index {
    return [self pluginControllerNameForIdentifier:self.pluginsList[index]];
}


- (NSString*)pluginControllerClassNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (PluginController<PluginControllerProtocol>*)pluginControllerForPluginIdentifier:(NSString*)identifier {
    NSString* lowerCaseIdentifier = [identifier lowercaseString];
    
    if (self.activePluginController) {
        NSString* activePluginControllerLowerCaseIdentifier = [[[self.activePluginController class] identifierName] lowercaseString];
        if ([lowerCaseIdentifier isEqualToString:activePluginControllerLowerCaseIdentifier]) {
            return self.activePluginController;
        }
    }
    
    
    NSString* identifierName = nil;
    for (NSString* originalIdentifier in self.pluginsList) {
        if ([lowerCaseIdentifier isEqualToString:[originalIdentifier lowercaseString]]) {
            identifierName = originalIdentifier;
        }
    }
    
    if (!identifierName) {
        return nil;
    }
    
    PluginController<PluginControllerProtocol>* pluginController = nil;
    Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifierName]);
    if (!pluginClass) {
        return nil;
    }
    pluginController = [[pluginClass alloc] init];
    return pluginController;
}

- (BOOL)existsPluginWithIdentifier:(NSString*)identifier {
    for (NSString* originalIdentifier in self.pluginsList) {
        if ([[identifier lowercaseString] isEqualToString:[originalIdentifier lowercaseString]]) {
            return YES;
        }
    }
    return NO;
}


#pragma mark - ViewControllers Utils

- (UIViewController*)rootViewControllerForPluginController:(PluginController*)pluginController {
    UIViewController* pluginRootViewController = nil;
    if (pluginController.mainNavigationController) {
        pluginRootViewController = pluginController.mainNavigationController;
    } else if (pluginController.mainSplitViewController) {
        pluginRootViewController = pluginController.mainSplitViewController;
    } else {
        NSLog(@"!! ERROR : PluginController '%@' has no initialized view controller (mainViewController, mainNavigationController, mainSplitViewController are nil)", [(id<PluginControllerProtocol>)pluginController identifierName]);
    }
    return pluginRootViewController;
}

- (void)adaptInitializedNavigationOrSplitViewControllerOfPluginController:(PluginController*)pluginController {
    
    if (!pluginController) {
        @throw [NSException exceptionWithName:@"bad pluginController argument" reason:@"cannot be nil" userInfo:nil];
    }
    
    if (pluginController.mainNavigationController && pluginController.mainSplitViewController) {
        @throw [NSException exceptionWithName:@"incorrect attributes" reason:@"pluginController properties mainNavigationController and mainSplitViewController cannot be both instancied" userInfo:nil];
    }
    
    if (!pluginController.mainNavigationController && !pluginController.mainSplitViewController) {
        @throw [NSException exceptionWithName:@"incorrect attributes" reason:@"pluginController properties mainNavigationController and mainSplitViewController cannot be both nil" userInfo:nil];
    }
    
    UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
    
    if (!pluginRootViewController || ![pluginRootViewController respondsToSelector:@selector(pluginIdentifier)] || ![(id)pluginRootViewController pluginIdentifier]) {
        @throw [NSException exceptionWithName:@"incorrect attribute pluginIdentifier" reason:@"root view controller of pluginController must have initialized pluginIdentifier property" userInfo:nil];
    }
    
    UIPanGestureRecognizer* panGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    UITapGestureRecognizer* tapGestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealToggle:)];
    UIView* gesturesView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1.0, 1.0)];
    gesturesView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    //gesturesView.hidden = YES;
    //gesturesView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.5];
    gesturesView.tag = kGesturesViewTag;
    gesturesView.gestureRecognizers = @[panGestureRecognizer, tapGestureRecognizer];
    
    
    if ([pluginRootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController* navController = (UINavigationController*)pluginRootViewController;
        
        UIPanGestureRecognizer* navigationBarPanGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
        [navController.navigationBar addGestureRecognizer:navigationBarPanGestureRecognizer];
        
        [navController.view addSubview:gesturesView];
        
        UIBarButtonItem* menuButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MainMenuNavbar"] style:UIBarButtonItemStylePlain target:self.revealController action:@selector(revealToggle:)];
        [[(UIViewController*)(navController.viewControllers[0]) navigationItem] setLeftBarButtonItem:menuButton];
    }
    
    if ([pluginRootViewController isKindOfClass:[UISplitViewController class]]) {
        UISplitViewController* splitController = (UISplitViewController*)pluginRootViewController;
        splitController.view.autoresizesSubviews = YES;
        [splitController.view addSubview:gesturesView];
        
        for (int i = 0; i<splitController.viewControllers.count; i++) {
            if([splitController.viewControllers[i] isKindOfClass:[UINavigationController class]]) {
                UINavigationController* navController = (UINavigationController*)splitController.viewControllers[i];
                if (i == 0) {
                    UIBarButtonItem* menuButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MainMenuNavbar"] style:UIBarButtonItemStylePlain target:self.revealController action:@selector(revealToggle:)];
                    [navController.viewControllers[0] navigationItem].leftBarButtonItem = menuButton;
                }
                UIPanGestureRecognizer* navigationBarPanGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
                [navController.navigationBar addGestureRecognizer:navigationBarPanGestureRecognizer];
            }
        }
    }
    
}

/* will rotate phone idiom device to portrait if presentedViewController or topviewcontroller only supports portait */
- (void)rotateDeviceToPortraitIfNecessary {

    UIViewController* viewController = nil;
    UIViewController* topViewController = nil;
    if (self.revealController.presentedViewController) {
        viewController = self.revealController.presentedViewController;
        if ([viewController isKindOfClass:[UINavigationController class]]) {
            topViewController = [(UINavigationController*)viewController topViewController];
        }
    } else if (self.activePluginController.mainNavigationController) {
        viewController = [self rootViewControllerForPluginController:self.activePluginController];
    } else {
        return; //no need to rotate if no active plugin nor presented view controller
    }
    
    
    if (([topViewController supportedInterfaceOrientations] | UIInterfaceOrientationMaskPortrait) == UIInterfaceOrientationMaskPortrait || ([viewController supportedInterfaceOrientations] | UIInterfaceOrientationMaskPortrait) == UIInterfaceOrientationMaskPortrait) {
        /* means only potrait mask is supported */
        UIDevice* device = [UIDevice currentDevice];
        if ([device orientation] != UIInterfaceOrientationPortrait) {
            /*
             * Best but forbidden method (private API)
             */
            /*NSInvocation* inv = [NSInvocation invocationWithMethodSignature:[device methodSignatureForSelector:@selector(setOrientation:)]];
            [inv setSelector:@selector(setOrientation:)];
            [inv setTarget:device];
            UIDeviceOrientation val = UIDeviceOrientationPortrait;
            [inv setArgument:&val atIndex:2];
            [inv invoke];*/
            
            /* 
             * Little hack found on http://ev3r.tumblr.com/post/3854315796/uinavigationcontroller-pushviewcontroller-from 
             * Will actually make view controller retest for supported operations and rotate if necessary.
             */
            [viewController presentModalViewController:[[UIViewController alloc] init] animated:NO];
            [viewController dismissModalViewControllerAnimated:NO];
        }
    }
}

- (void)manageBackgroundPlugins {
    if (BACKGROUND_PLUGINS_ENABLED) {
        NSLog(@"!! WARNING: background plugins management is not fully supported. Plugins will simply stay in memory until app receives memory warning.");
    } else {
        [self.pluginsControllers removeAllObjects];
    }
}

#pragma mark - ZUUIRevealControllerDelegate

- (void)revealController:(ZUUIRevealController *)revealController willRevealRearViewController:(UIViewController *)rearViewController {
    if (self.activePluginController) {
        [self postNotificationWithState:PluginWillLoseForegroundNotification pluginIdentifier:[self identifierNameForPluginController:self.activePluginController]];
    }
    if ([self.activePluginController respondsToSelector:@selector(pluginWillLoseForeground)]) {
        [self.activePluginController pluginWillLoseForeground];
    }
}

- (void)revealController:(ZUUIRevealController *)revealController didRevealRearViewController:(UIViewController *)rearViewController {
    if (!self.activePluginController) {
        return;
    }
    UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:self.activePluginController];
    UIView* gesturesView = [pluginRootViewController.view viewWithTag:kGesturesViewTag];
    gesturesView.frame = pluginRootViewController.view.frame;
    gesturesView.hidden = NO;
}

- (void)revealController:(ZUUIRevealController *)revealController willHideRearViewController:(UIViewController *)rearViewController {
    
}

- (void)revealController:(ZUUIRevealController *)revealController didHideRearViewController:(UIViewController *)rearViewController {
    if (!self.activePluginController) {
        return;
    }
    
    [self postNotificationWithState:PluginDidEnterForegroundNotification pluginIdentifier:[self identifierNameForPluginController:self.activePluginController]];
    if ([self.activePluginController respondsToSelector:@selector(pluginDidEnterForeground)]) {
        [self.activePluginController pluginDidEnterForeground];
    }
    UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:self.activePluginController];
    UIView* gesturesView = [pluginRootViewController.view viewWithTag:kGesturesViewTag];
    gesturesView.hidden = YES;
}

- (void)revealController:(ZUUIRevealController *)revealController willSwapToFrontViewController:(UIViewController *)frontViewController {
    
}

- (void)revealController:(ZUUIRevealController *)revealController didSwapToFrontViewController:(UIViewController *)frontViewController {
    if (frontViewController == self.splashViewController) {
        [self.splashViewController willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:YES];
    }
}

@end
