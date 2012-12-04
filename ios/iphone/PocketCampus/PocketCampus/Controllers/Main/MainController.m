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

#import <objc/message.h>

@interface MainController ()

@property (nonatomic, weak) UIWindow* window;
@property (nonatomic, strong) MainMenuViewController* mainMenuViewController;
@property (nonatomic, strong) ZUUIRevealController* revealController;
@property (nonatomic) CGFloat revealWidth;
@property (nonatomic, strong) NSArray* pluginsList;
@property (nonatomic, strong) SplashViewController* splashViewController;
@property (nonatomic, weak) PluginController<PluginControllerProtocol>* activePluginController;
@property (nonatomic, strong) NSString* initialActivePluginIdentifier;
@property (nonatomic, strong) NSMutableDictionary* pluginsControllers; //key: plugin identifier, value: PluginController subclass.

@end

/* Corresponds to supportedIdioms possible values in config file Plugins.plist */
static NSString* kSupportedIdiomPhone = @"phone";
static NSString* kSupportedIdiomPad = @"pad";
static NSString* kSupportedIdiomPhonePad = @"phone+pad";

static int kGesturesViewTag = 50;

static BOOL BACKGROUND_PLUGINS_ENABLED = NO; //YES not supported yet

static MainController<MainControllerPublic>* instance = nil;

@implementation MainController

- (id)initWithWindow:(UIWindow *)window
{
    self = [super init];
    if (self) {
        self.window = window;
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
        if ([PCUtils isIdiomPad]) {
            self.revealController.toggleAnimationDuration = 0.65;
        } else {
            self.revealController.toggleAnimationDuration = 0.8;
        }
        self.revealController.delegate = self;
        self.window.rootViewController = self.revealController;
        //[NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(revealMenuAfterSplash) userInfo:nil repeats:NO];
        instance = self;
    }
    return self;
}

+ (id<MainControllerPublic>)publicController {
    return instance;
}

#pragma mark - MainControllerPublic

- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName {
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:pluginIdentifierName];
    if (BACKGROUND_PLUGINS_ENABLED && [pluginController respondsToSelector:@selector(canBeReleased)] && ![pluginController canBeReleased]) {
        return NO;
    }
    if ([[[self.activePluginController class] identifierName] isEqualToString:pluginIdentifierName]) { //already forground
        return YES;
    }
    if (self.activePluginController) { //means app already started
        [self setActivePluginWithIdentifier:pluginIdentifierName];
    } else { //app is starting
        self.initialActivePluginIdentifier = pluginIdentifierName;
    }
    return YES;
}

- (BOOL)requestLeavePlugin:(NSString*)pluginIdentifierName {
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:pluginIdentifierName];
    if (BACKGROUND_PLUGINS_ENABLED && [pluginController respondsToSelector:@selector(canBeReleased)] && ![pluginController canBeReleased]) {
        return NO;
    }
    [self setActivePluginWithIdentifier:nil];
    return YES;
}

#pragma mark -

- (void)mainMenuIsReady {
    if (![self.revealController.frontViewController isViewLoaded]) {
        [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(mainMenuIsReady) userInfo:nil repeats:NO];
    } else {
        [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(revealMenuAfterSplash) userInfo:nil repeats:NO];
    }
}

- (void)revealMenuAfterSplash {
    if (self.initialActivePluginIdentifier) {
        self.revealController.toggleAnimationDuration = 0.25;
        [self setActivePluginWithIdentifier:self.initialActivePluginIdentifier];
        [self.revealController revealToggle:self];
        [(SplashViewController*)(self.revealController.frontViewController) willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:YES];
    } else {
        [(SplashViewController*)(self.revealController.frontViewController) willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:NO];
        if ([PCUtils isIdiomPad]) {
            [self.revealController revealToggle:self];
        } else {
            [self.revealController hideFrontView];
        }
        self.revealController.toggleAnimationDuration = 0.25;
    }
}

- (void)initPluginsList {
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Plugins" ofType:@"plist"]];
    NSArray* pluginsFromPlist = [plist objectForKey:@"Plugins"];
    NSMutableArray* pluginsTempArray = [NSMutableArray arrayWithCapacity:pluginsFromPlist.count];
    BOOL isPadIdiom = [PCUtils isIdiomPad];
    for (NSDictionary* pluginDic in pluginsFromPlist) {
        NSString* identifierName = [pluginDic objectForKey:@"identifierName"];
        if ([[pluginDic objectForKey:@"enabled"] boolValue]) {
            NSString* idiom = [pluginDic objectForKey:@"supportedIdioms"];
            if (idiom && ([idiom isEqualToString:kSupportedIdiomPhonePad] || (isPadIdiom && [idiom isEqualToString:kSupportedIdiomPad]) || (!isPadIdiom && [idiom isEqualToString:kSupportedIdiomPhone]))) {
                NSLog(@"-> Detected enabled plugin : '%@' with idiom '%@'", identifierName, idiom);
                [pluginsTempArray addObject:identifierName];
            }
        }
    }
    
    /* sorting plugins alphabetically on plugin name. Should be on localized name, but cannot access it here */
    /*[pluginsTempArray sortWithOptions:0 usingComparator:^NSComparisonResult(id obj1, id obj2) {
     NSString* name1 = (NSString*)obj1;
     NSString* name2 = (NSString*)obj2;
     return [name1 compare:name2];
     }];*/
    
    self.pluginsList = [NSArray arrayWithArray:pluginsTempArray]; //creates a non-mutable copy of the dictionary
    
}

- (NSString*)pluginControllerNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (NSString*)pluginControllerNameForIndex:(NSUInteger)index {
    return [self pluginControllerNameForIdentifier:self.pluginsList[index]];
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

- (void)initMainMenu {
    NSMutableArray* menuItems = [NSMutableArray array];
    
    MainMenuItem* pluginSectionHeader = [MainMenuItem menuItemSectionHeaderWithTitle:@"Plugins"];
    pluginSectionHeader.hidden = YES;
    
    [menuItems addObject:pluginSectionHeader];
    
    for (NSString* pluginIdentifier in self.pluginsList) {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:pluginIdentifier]);
        NSString* localizedName = [pluginClass localizedName];
        MainMenuItem* item = [MainMenuItem menuItemButtonWithTitle:localizedName leftImage:[UIImage imageNamed:pluginIdentifier] identifier:pluginIdentifier];
        [menuItems addObject:item];
        
    }
    
    /*[menuItems addObject:[MainMenuItem menuItemThinSeparator]];
    
    MainMenuItem* settingsButton = [MainMenuItem menuItemButtonWithTitle:NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil) leftImage:[UIImage imageNamed:@"Settings"] identifier:kSettingsIdentifier];
    
    [menuItems addObject:settingsButton];*/
    
    MainMenuViewController* mainMenuViewController = [[MainMenuViewController alloc] initWithMenuItems:menuItems mainController:self];
    self.mainMenuViewController = mainMenuViewController;
}

- (void)initRevealController {
    self.splashViewController = [[SplashViewController alloc] initWithRightHiddenOffset:self.revealWidth];
    self.revealController = [[ZUUIRevealController alloc] initWithFrontViewController:self.splashViewController rearViewController:self.mainMenuViewController];
    self.revealController.rearViewRevealWidth = self.revealWidth;
}

- (NSString*)pluginControllerClassNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (void)refreshDisplayedPlugin {
    if ([self.activePluginController respondsToSelector:@selector(refresh)]) {
        [self.activePluginController performSelector:@selector(refresh)];
    }
}

- (void)showGlobalSettings {
    GlobalSettingsViewController* settingsViewController = [[GlobalSettingsViewController alloc] init];
    UINavigationController* settingsNavController = [[UINavigationController alloc] initWithRootViewController:settingsViewController];
    settingsNavController.modalPresentationStyle = UIModalPresentationFormSheet;
    settingsNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
    [self.revealController presentViewController:settingsNavController animated:YES completion:NULL];
}

- (void)setActivePluginWithIdentifier:(NSString*)identifier {
    
    if (!identifier) {
        [self.pluginsControllers removeObjectForKey:[self.activePluginController.class identifierName]];
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
    
    PluginController<PluginControllerProtocol>* pluginController = [self.pluginsControllers objectForKey:identifier];
    if (pluginController) {
        UIViewController* pluginRootViewController = [[self class] rootViewControllerForPluginController:pluginController];
        [self.revealController setFrontViewController:pluginRootViewController animated:NO]; //check on whether this is already the front one is done in the method implementation
    } else {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifier]);
        if (!pluginClass) {
            @throw [NSException exceptionWithName:@"Bad plugin identifier" reason:[NSString stringWithFormat:@"Controller class does not exist for idenfier %@", identifier] userInfo:nil];
        }
        pluginController = [[pluginClass alloc] init];
        [self adaptInitializedNavigationOrSplitViewControllerOfPluginController:pluginController];
        UIViewController* pluginRootViewController = [[self class] rootViewControllerForPluginController:pluginController];
        
        if (pluginRootViewController) {
            [self manageBackgroundPlugins];
            [self.pluginsControllers setObject:pluginController forKey:identifier];
            [self.revealController setFrontViewController:pluginRootViewController animated:NO];
        }
    }
    [self.mainMenuViewController setSelectedPluginWithIdentifier:identifier animated:YES];
    self.activePluginController = pluginController;
}

- (void)manageBackgroundPlugins {
    if (BACKGROUND_PLUGINS_ENABLED) {
        //TODO
    } else {
        [self.pluginsControllers removeAllObjects];
    }
}

+ (UIViewController*)rootViewControllerForPluginController:(PluginController*)pluginController {
    UIViewController* pluginRootViewController = nil;
    if (pluginController.mainViewController) {
        NSLog(@"!! Warning: legacy property mainViewController used. There won't be any navigation controller to support navigation. Consider using mainNavigationController and/or mainSplitViewController (iPad) instead.");
        pluginRootViewController = pluginController.mainViewController;
    } else if (pluginController.mainNavigationController) {
        pluginRootViewController = pluginController.mainNavigationController;
    } else if (pluginController.mainSplitViewController) {
        pluginRootViewController = pluginController.mainSplitViewController;
    } else {
        NSLog(@"-> ERROR : PluginController '%@' has no initialized view controller (mainViewController, mainNavigationController, mainSplitViewController are nil)", [(id<PluginControllerProtocol>)pluginController identifierName]);
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
    
    UIViewController* pluginRootViewController = [[self  class] rootViewControllerForPluginController:pluginController];
    
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
        
        navController.navigationBar.tintColor = [PCValues pocketCampusRed];
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
                navController.navigationBar.tintColor = [PCValues pocketCampusRed];
                UIPanGestureRecognizer* navigationBarPanGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
                [navController.navigationBar addGestureRecognizer:navigationBarPanGestureRecognizer];
            }
        }
    }

}

#pragma mark - ZUUIRevealControllerDelegate

- (void)revealController:(ZUUIRevealController *)revealController willRevealRearViewController:(UIViewController *)rearViewController {
    if ([self.activePluginController respondsToSelector:@selector(pluginWillLoseFocus)]) {
        [self.activePluginController pluginWillLoseFocus];
    }
}

- (void)revealController:(ZUUIRevealController *)revealController didRevealRearViewController:(UIViewController *)rearViewController {
    if (!self.activePluginController) {
        return;
    }
    UIViewController* pluginRootViewController = [[self class] rootViewControllerForPluginController:self.activePluginController];
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
    if ([self.activePluginController respondsToSelector:@selector(pluginDidRegainActive)]) {
        [self.activePluginController pluginDidRegainActive];
    }
    UIViewController* pluginRootViewController = [[self class] rootViewControllerForPluginController:self.activePluginController];
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
