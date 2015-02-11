/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 07.10.12.

#import "MainController.h"

#import "PluginController.h"

#import "ZUUIRevealController.h"

#import "PluginNavigationController.h"

#import "PluginSplitViewController.h"

#import "MainMenuViewController.h"

#import "MainMenuItem.h"

#import "PCSplashView.h"

#import "PCSplashViewController.h"

#import "PCGlobalSettingsViewController.h"

#import "PCURLSchemeHandler.h"

#import <objc/message.h>

#import <Crashlytics/Crashlytics.h>

@interface MainController ()<CrashlyticsDelegate, UIGestureRecognizerDelegate, ZUUIRevealControllerDelegate>

@property (nonatomic, weak) UIWindow* window;
@property (nonatomic, strong) PCURLSchemeHandler* urlSchemeHander;
@property (nonatomic, strong) MainMenuViewController* mainMenuViewController;
@property (nonatomic, weak) UINavigationController* settingsNavController;
@property (nonatomic, strong) ZUUIRevealController* revealController;
@property (nonatomic) BOOL ignoreRevealMainMenuGesture;
@property (nonatomic) CGFloat revealWidth;
@property (nonatomic, strong) NSDictionary* plistDicForPluginIdentifier;
@property (nonatomic, strong) NSArray* logicOnlyPluginsList; //plugin identifiers of plugins that have no UI (logicOnly is YES)
@property (nonatomic, strong) NSArray* pluginsList; //plugin identifiers of plugins that have a UI (logicOnly is NO)
@property (nonatomic, strong) PCSplashViewController* splashViewController;
@property (nonatomic, strong) PCSplashView* splashView;
@property (nonatomic, weak) PluginController<PluginControllerProtocol>* activePluginController;
@property (nonatomic, strong) NSString* initialActivePluginIdentifier;
@property (nonatomic, copy) NSURL* pcURLToHandle;
@property (nonatomic, strong) NSMutableDictionary* pluginControllerForIdentifierName; //key: plugin identifier name, value: PluginController subclass.
@property (nonatomic) BOOL initDone;

@property (nonatomic, strong) NSMutableSet* validatedPluginNamesCache;

@end

/* Corresponds to supportedIdioms possible values in config file Plugins.plist */
static NSString* const kSupportedIdiomPhone = @"phone";
static NSString* const kSupportedIdiomPad = @"pad";
static NSString* const kSupportedIdiomPhonePad = @"phone+pad";

static NSString* const kPluginsMainMenuItemsInfoKey = @"pluginsMainMenuItemsInfo";
static NSString* const kPluginsMainMenuItemsInfoOrderNumberKey = @"pluginsMainMenuItemsInfoOrderNumber";
static NSString* const kPluginsMainMenuItemsInfoHiddenKey = @"pluginsMainMenuItemsInfoHidden";

static int kGesturesViewTag = 50;

/* 
 * If YES, plugins are kept alive when switching among them.
 * Otherwise, only foreground is kept alive (allocated).
 * IMPORTANT: background management is dummy and not well tested for now (no smart memory management)
 */
static BOOL BACKGROUND_PLUGINS_ENABLED = NO;

static MainController<MainControllerPublic>* instance = nil;

@implementation MainController

#pragma mark - Init

- (id)initWithWindow:(UIWindow *)window
{
    self = [super init];
    if (self) {
        self.window = window;
        //self.pcURLToHandle = [NSURL URLWithString:@"pocketcampus://map.plugin.pocketcampus.org/search?q=BC"];
        self.urlSchemeHander = [[PCURLSchemeHandler alloc] initWithMainController:self];
        self.validatedPluginNamesCache = [NSMutableSet set];
        instance = self;
        [self globalInit];
    }
    return self;
}

+ (id<MainControllerPublic>)publicController {
    return instance;
}

#pragma mark - MainControllerPublic

- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName {
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifierName];
    
    /* If not BACKGROUND_PLUGINS_ENABLED, check that active plugin controller can be released */
    PluginController<PluginControllerProtocol>* pluginController = self.pluginControllerForIdentifierName[pluginIdentifierName];
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
    
    PluginController<PluginControllerProtocol>* pluginController = self.pluginControllerForIdentifierName[pluginIdentifierName];
    
    if (!pluginController) {
        return YES; //plugin is not allocated and not active => desired effect achieved
    }
    
    if ([pluginController respondsToSelector:@selector(canBeReleased)] && ![pluginController canBeReleased]) {
        return NO;
    }
    
    if (self.activePluginController && [[self identifierNameForPluginController:self.activePluginController] isEqualToString:pluginIdentifierName]) {
        [self setActivePluginWithIdentifier:nil];
    }
    
    [self.pluginControllerForIdentifierName removeObjectForKey:pluginIdentifierName]; //already been done by setActivePluginIdentifer if not BACKGROUND_PLUGINS_ENABLED
    
    return YES;
}

- (void)addPluginStateObserver:(id)observer selector:(SEL)selector notification:(PluginStateNotification)notification pluginIdentifierName:(NSString*)pluginIdentifierName {
    if (!observer) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"observer cannot be nil" userInfo:nil];
    }
    if (![observer respondsToSelector:selector]) {
        CLSNSLog(@"!! Warning: observer %@ does not respond to selector %@. Crash will occur when notification is posted.", observer, NSStringFromSelector(selector));
    }
    [self throwExceptionIfPluginIdentifierNameIsNotValid:pluginIdentifierName];
    
    NSString* name = [self notificiationNameForPluginStateNotification:notification pluginIdentifierName:pluginIdentifierName];
    [[NSNotificationCenter defaultCenter] addObserver:observer selector:selector name:name object:self];
    CLSNSLog(@"-> %@ ('%@') registered for PluginStateNotification %d", observer, pluginIdentifierName, notification);
}

- (void)removePluginStateObserver:(id)observer {
    if (!observer) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"observer cannot be nil" userInfo:nil];
    }
    [[NSNotificationCenter defaultCenter] removeObserver:observer name:nil object:self];
    CLSNSLog(@"-> %@ unregistered of PluginStateNotifications", observer);
}

- (UIViewController*)currentTopMostViewController {
    if (!self.activePluginController) {
        return self.mainMenuViewController.presentedViewController ?: self.mainMenuViewController;
    }
    UIViewController* topViewController = [self rootViewControllerForPluginController:self.activePluginController];
    while (topViewController.presentedViewController) {
        if (topViewController.presentedViewController.isBeingDismissed) {
            break;
        }
        topViewController = topViewController.presentedViewController;
    }
    return topViewController;
}

- (void)beginIgnoringRevealMainMenuGesture {
    self.ignoreRevealMainMenuGesture = YES;
}

- (void)endIgnoringRevealMainMenuGesture {
    self.ignoreRevealMainMenuGesture = NO;
}

- (PCURLSchemeHandler*)urlSchemeHandlerSharedInstance {
    return self.urlSchemeHander;
}

- (BOOL)handlePocketCampusURL:(NSURL*)urlTmp {
    if (![urlTmp isKindOfClass:[NSURL class]]) {
        //do that instead of exception to prevent crashes
        [self showActionNotSupportedAlert];
        CLSNSLog(@"!! ERROR: tried to handlePocketCampusURL: with URL not kind of class NSURL. Returning NO.");
        return NO;
    }
    
    if (!self.initDone) {
        //setInitDoneYES will call handlePocketCampusURL: again when ready
        self.pcURLToHandle = urlTmp;
        return NO;
    }
    
    CLSNSLog(@"-> Handling URL: %@", urlTmp.absoluteString);
    
    NSURL* url = [urlTmp copy];
    self.pcURLToHandle = nil; //prevent handling same URL twice
    
    NSString* pluginIdentifier = [self.urlSchemeHander pluginIdentifierForPocketCampusURL:url];
    if (!pluginIdentifier) {
        [self showActionNotSupportedAlert];
        CLSNSLog(@"    !! ERROR: cannot parse plugin identifer. Returning NO.");
        return NO;
    }
    
    if ([pluginIdentifier isEqualToString:kPocketCampusURLNoPluginSpecified]) {
        //just to open PocketCampus, no plugin to open in particular
        return YES;
    }
    
    PluginController<PluginControllerProtocol>* pluginController = [self pluginControllerForPluginIdentifier:[self validPluginIdentifierForAnycasePluginIdentifier:pluginIdentifier]];
    if (!pluginController) {
        [self showActionNotSupportedAlert];
        CLSNSLog(@"    !! ERROR: could not find plugin controller for identifier '%@'. Returning NO.", pluginIdentifier);
        return NO;
    }
    
    BOOL currentWasLeft = (self.activePluginController && self.revealController.currentFrontViewPosition == FrontViewPositionLeft);
    
    if (currentWasLeft) {
        [self.revealController revealToggle:self];
    }
    
    CLSNSLog(@"    1. Opening plugin '%@'", pluginIdentifier);
    
    [self setActivePluginWithIdentifier:[[pluginController class] identifierName]];
    
    NSString* action = [self.urlSchemeHander actionForPocketCampusURL:url];
    NSDictionary* params = [self.urlSchemeHander parametersForPocketCampusURL:url];
    
    if ((action.length > 0 || params) && [pluginController respondsToSelector:@selector(handleURLQueryAction:parameters:)]) {
        if ([pluginController handleURLQueryAction:action parameters:params]) {
            CLSNSLog(@"    2. Plugin successfully handled action '%@' with parameters %@", action, params);
        } else {
            CLSNSLog(@"    !! ERROR: plugin failed to handle action '%@' with parameters %@", action, params);
            [self showActionNotSupportedAlert];
            return NO;
        }
    }
    
#ifndef TARGET_IS_EXTENSION
    [[PCGAITracker sharedTracker] trackAction:@"OpenPocketCampusURL" inScreenWithName:@"/" contentInfo:[url absoluteString]];
#endif
    return YES;
}

- (UIViewController*)viewControllerForWebURL:(NSURL*)url {
    if (![url isKindOfClass:[NSURL class]]) {
        return nil;
    }
    UIViewController* viewController = nil;
    for (NSString* identifierName in self.pluginsList) {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifierName]);
        if ([pluginClass respondsToSelector:@selector(viewControllerForWebURL:)]) {
            viewController = [pluginClass viewControllerForWebURL:url];
            if (viewController) {
                break;
            }
        }
    }
    return viewController;
}

- (BOOL)isPluginAnycaseIdentifierValid:(NSString*)anycaseIdentifier {
    return [self existsPluginWithIdentifier:[self validPluginIdentifierForAnycasePluginIdentifier:anycaseIdentifier]];

}

- (NSString*)localizedPluginIdentifierForAnycaseIdentifier:(NSString*)anycaseIdentifier {
    if (![self isPluginAnycaseIdentifierValid:anycaseIdentifier]) {
        return nil;
    }
    Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:[self validPluginIdentifierForAnycasePluginIdentifier:anycaseIdentifier]]);
    return [pluginClass localizedName];
}

#pragma mark - Initialization

- (void)globalInit {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        [self preConfigInit];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(globalInit) name:kPCConfigDidFinishLoadingNotification object:nil];
    });
    if ([PCConfig isLoaded]) {
        [self postConfigInit];
    }
}

/*
 * Iinit code that does NOT need PCConfig to be loaded
 */
- (void)preConfigInit {
    self.window.tintColor = [PCValues pocketCampusRed];
    self.revealWidth = [PCUtils isIdiomPad] ? 320.0 : (0.84 * self.window.bounds.size.width);
    [self initAndShowSplashViewViewController];
}

/*
 * Iinit code that needs PCConfig to be loaded
 */
- (void)postConfigInit {
    [self initAnalytics];
    [self initPluginsList];
    self.pluginControllerForIdentifierName = [NSMutableDictionary dictionaryWithCapacity:self.pluginsList.count];
    [self initMainMenu];
    [self initRevealController];
    [self initPluginObservers];
    [self revealMenuAndFinalize];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidReceiveMemoryWarning) name:UIApplicationDidReceiveMemoryWarningNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(pcConfigUserDefaultsDidChange) name:NSUserDefaultsDidChangeNotification object:[PCConfig defaults]];
}

#pragma mark Pre-config phases

- (void)initAndShowSplashViewViewController {
#ifdef TARGET_IS_MAIN_APP
    self.splashViewController = [[PCSplashViewController alloc] initWithRightHiddenOffset:self.revealWidth];
    if (![PCUtils isIdiomPad]) {
        self.splashView = [[PCSplashView alloc] initWithSuperview:self.splashViewController.view];
    }
    self.window.rootViewController = self.splashViewController;
#endif
}

#pragma mark Post-config phases

- (void)initAnalytics {
    
    //Crashlytics
    BOOL clEnabledConfig = [[PCConfig defaults] boolForKey:PC_CONFIG_CRASHLYTICS_ENABLED_KEY];
    BOOL clEnabledUserConfig = [[PCConfig defaults] boolForKey:PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY];
    if (clEnabledConfig && clEnabledUserConfig) {
        NSString* crashlyticsAPIKey = [[PCConfig defaults] stringForKey:PC_CONFIG_CRASHLYTICS_APIKEY_KEY];
        if (crashlyticsAPIKey) {
            static dispatch_once_t onceToken;
            dispatch_once(&onceToken, ^{
                CLSNSLog(@"-> Starting Crashlytics");
                [Crashlytics startWithAPIKey:crashlyticsAPIKey delegate:self];
            });
        } else {
            CLSNSLog(@"!! WARNING: could not start Crashlytics, did not find APIKey in config.");
        }
    } else {
        CLSNSLog(@"-> Crashlytics disabled (config: %d, user: %d)", clEnabledConfig, clEnabledUserConfig);
    }
    
    //Google Analytics
#ifndef TARGET_IS_EXTENSION
    [[PCGAITracker sharedTracker] trackAppOnce];
#endif
}

- (void)initPluginsList {
    
    //Loading plugins list from Plugins.plist
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Plugins" ofType:@"plist"]];
    NSArray* pluginsFromPlist = plist[@"Plugins"];
    NSMutableArray* logicOnlyPluginsListTmp = [NSMutableArray array];
    NSMutableArray* pluginsListTmp = [NSMutableArray arrayWithCapacity:pluginsFromPlist.count];
    NSMutableDictionary* tmpPlistDicForPluginIdentifier = [NSMutableDictionary dictionaryWithCapacity:pluginsFromPlist.count];
    
    //Plugins list gotten from server
    NSArray* pluginsFromServer = [[PCConfig defaults] objectForKey:PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY];
    
    
    BOOL isPadIdiom = [PCUtils isIdiomPad];
    
    
    for (NSDictionary* pluginDic in pluginsFromPlist) {
        NSString* identifierName = pluginDic[@"identifierName"];
        
        BOOL pluginEnabled = [pluginDic[@"enabled"] boolValue];
        if (pluginsFromServer) { //if server config available, it overrides one from Plugins.plist
            pluginEnabled = [pluginsFromServer containsObject:identifierName];
        }
        
        NSString* minOSVersion = pluginDic[@"minOSVersion"];
        if (minOSVersion) {
            float minVersion = [minOSVersion floatValue];
            if (minVersion == 0.0) {
                CLSNSLog(@"!! WARNING: minOSVersion '%@' is not valid. Disabling plugin '%@'.", minOSVersion, identifierName);
                pluginEnabled = NO;
            } else {
                if ([PCUtils isOSVersionSmallerThan:minVersion]) {
                    CLSNSLog(@"-> Plugin '%@' requires %.1f (%.1f running) and will be thus disabled.", identifierName, minVersion, [PCUtils OSVersion]);
                    pluginEnabled = NO;
                }
            }
        }
        
        if (pluginEnabled) { //plugin is enabled
            NSString* idiom = pluginDic[@"supportedIdioms"];
            if (idiom &&
                (
                 [idiom isEqualToString:kSupportedIdiomPhonePad]
                 || (isPadIdiom && [idiom isEqualToString:kSupportedIdiomPad])
                 || (!isPadIdiom && [idiom isEqualToString:kSupportedIdiomPhone]))
                ) {
                NSNumber* logicOnly = pluginDic[@"logicOnly"];
                BOOL logicOnlyBool = NO;
                if (logicOnly && ![logicOnly isKindOfClass:[NSNumber class]]) {
                    CLSNSLog(@"!! ERROR: logicOnly key must be of type BOOL (found %@). Assuming logicOnly = NO.", logicOnly);
                } else {
                    logicOnlyBool = [logicOnly boolValue];
                }
                if (logicOnlyBool) {
                    CLSNSLog(@"-> Detected enabled logic only plugin: '%@'", identifierName);
                    [logicOnlyPluginsListTmp addObject:identifierName];
                } else {
                    CLSNSLog(@"-> Detected enabled idiom-compatible plugin: '%@' (idiom '%@')", identifierName, idiom);
                    [pluginsListTmp addObject:identifierName];
                }
                tmpPlistDicForPluginIdentifier[identifierName] = pluginDic;
            }
        }
    }
    
    /* sorting plugins alphabetically on plugin name. Should be on localized name, but cannot access it here */
    /*[pluginsTempArray sortWithOptions:0 usingComparator:^NSComparisonResult(id obj1, id obj2) {
     NSString* name1 = (NSString*)obj1;
     NSString* name2 = (NSString*)obj2;
     return [name1 compare:name2];
     }];*/
    
    self.logicOnlyPluginsList = [logicOnlyPluginsListTmp copy]; //creates a non-mutable copy of the array
    self.pluginsList = [pluginsListTmp copy]; //creates a non-mutable copy of the array
    self.plistDicForPluginIdentifier = [tmpPlistDicForPluginIdentifier copy]; //creates a non-mutable copy of the dictionary
}

- (NSMutableArray*)defaultMainMenuItemsWithoutTopSection {
    if (!self.pluginsList) {
        return nil;
    }
    NSMutableArray* menuItems = [NSMutableArray array];
#ifdef TARGET_IS_MAIN_APP
    for (NSString* pluginIdentifier in self.pluginsList) {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:pluginIdentifier]);
        NSString* localizedName = [pluginClass localizedName];
        UIImage* image = [UIImage imageNamed:pluginIdentifier];
        UIImage* highlightedImage = [UIImage imageNamed:[pluginIdentifier stringByAppendingString:@"Highlighted"]];
        MainMenuItem* item = [MainMenuItem menuItemButtonWithTitle:localizedName leftImage:image highlightedLeftImage:highlightedImage identifier:pluginIdentifier];
        NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
        item.hidden = [hiddenByDefault boolValue];
        NSString* devTeam = self.plistDicForPluginIdentifier[item.identifier][@"devTeam"];
        if (devTeam) {
            item.subtitle = [NSString stringWithFormat:@"%@ %@",NSLocalizedStringFromTable(@"By", @"PocketCampus", nil), devTeam];
        }
        [menuItems addObject:item];
    }
#endif
    return menuItems;
}

- (void)initMainMenu {
    
    /* Generating menu items from pluginsList */
    
    NSMutableArray* menuItems = [self defaultMainMenuItemsWithoutTopSection];
    
    /* Restoring previous order / hidden of menu items, saved be used */
    
    NSDictionary* menuItemsInfo = (NSDictionary*)[PCPersistenceManager objectForKey:kPluginsMainMenuItemsInfoKey pluginName:@"pocketcampus"];
    
    NSMutableArray* menuItemsCopy = [menuItems mutableCopy];
    
    @try {
        for (MainMenuItem* item in menuItemsCopy) {

            NSDictionary* infos = menuItemsInfo[item.identifier];
            if (infos) {
                //user has customized order AND/OR visiblity for this item
                if (infos[kPluginsMainMenuItemsInfoOrderNumberKey]) {
                    NSUInteger index = [infos[kPluginsMainMenuItemsInfoOrderNumberKey] unsignedIntValue];
                    [menuItems removeObject:item];
                    [menuItems insertObject:item atIndex:index];
                } else {
                    [menuItems removeObject:item];
                    [menuItems insertObject:item atIndex:0];
                }
                if (infos[kPluginsMainMenuItemsInfoHiddenKey]) {
                    item.hidden = [infos[kPluginsMainMenuItemsInfoHiddenKey] boolValue];
                } else {
                    NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
                    item.hidden = [hiddenByDefault boolValue];
                }
            } else {
                //user did NOT customize order/visibility for this item
                NSNumber* hiddenByDefault = self.plistDicForPluginIdentifier[item.identifier][@"hiddenByDefault"];
                item.hidden = [hiddenByDefault boolValue];
            }
        }
    }
    @catch (NSException *exception) {
        menuItems = menuItemsCopy; //if anything bad happens during recovery, go back to standard order.
    }
#ifdef TARGET_IS_MAIN_APP
    self.mainMenuViewController = [[MainMenuViewController alloc] initWithMenuItems:menuItems mainController:self];
#endif
}

- (void)initRevealController {
#ifdef TARGET_IS_MAIN_APP
    self.splashViewController = [[PCSplashViewController alloc] initWithRightHiddenOffset:self.revealWidth];
    PCNavigationController* mainMenuNavController = [[PCNavigationController alloc] initWithRootViewController:self.mainMenuViewController];
    self.revealController = [[ZUUIRevealController alloc] initWithFrontViewController:self.splashViewController rearViewController:mainMenuNavController];
    self.revealController.delegate = self;
    self.revealController.rearViewRevealWidth = self.revealWidth;
    self.revealController.frontViewShadowRadius = 1.0;
    self.revealController.maxRearViewRevealOverdraw = 0.0;
    self.revealController.toggleAnimationDuration = 0.65;
    [self.splashView moveToSuperview:self.revealController.view];
    self.window.rootViewController = self.revealController;
#endif
}

- (void)initPluginObservers {
    NSArray* allPlugins = [self.pluginsList arrayByAddingObjectsFromArray:self.logicOnlyPluginsList];
    for (NSString* identifier in allPlugins) {
        Class pluginClass = NSClassFromString([self pluginControllerNameForIdentifier:identifier]);
        if (class_getClassMethod(pluginClass, @selector(initObservers))) {
            CLSNSLog(@"-> Found PluginController with observers : %@", pluginClass);
            [pluginClass initObservers];
        }
    }
}

- (void)revealMenuAndFinalize {
    static NSTimeInterval const kHideAnimationDelay = 0.4;
    static NSTimeInterval const kHideAnimationNormalDuration = 0.50;
    if (self.initialActivePluginIdentifier) {
        self.revealController.toggleAnimationDuration = 0.25;
        self.initDone = YES; //must do it before calling setActivePluginWithIdentifier otherwise no effect
        [self setActivePluginWithIdentifier:self.initialActivePluginIdentifier];
        self.initialActivePluginIdentifier = nil; //initial plugin has been treated, prevent future use
        [self.revealController revealToggle:self];
        [self.splashView hideWithAnimationDelay:kHideAnimationDelay duration:self.revealController.toggleAnimationDuration completion:^{
            [self.splashView removeFromSuperview];
        }];
    } else {
        [(PCSplashViewController*)(self.revealController.frontViewController) willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:NO];
        NSTimeInterval duration;
        if ([PCUtils isIdiomPad]) {
            [self.revealController revealToggle:self];
            duration = self.revealController.toggleAnimationDuration;
        } else {
            [self.revealController hideFrontViewAnimated:NO];
            duration = kHideAnimationNormalDuration;
            [self.splashView hideWithAnimationDelay:kHideAnimationDelay duration:duration completion:^{
                [self.splashView removeFromSuperview];
            }];
        }
        [NSTimer scheduledTimerWithTimeInterval:duration+0.1 block:^{
            self.initDone = YES;
            if (self.initialActivePluginIdentifier) {
                [self setActivePluginWithIdentifier:self.initialActivePluginIdentifier];
                self.initialActivePluginIdentifier = nil; //initial plugin has been treated, prevent future use
                if (self.revealController.currentFrontViewPosition != FrontViewPositionLeft) {
                    [self.revealController showFrontViewCompletely:YES];
                }
            }
            if (self.pcURLToHandle) {
                [self handlePocketCampusURL:self.pcURLToHandle];
            }
        } repeats:NO];
        self.revealController.toggleAnimationDuration = 0.25;
    }
    
/*#warning REMOVE
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self goCrazyWithMap];
    });*/
}

/*#warning REMOVE
- (void)goCrazyWithMap {
#define ARC4RANDOM_MAX      0x100000000
    double val = ((double)arc4random() / ARC4RANDOM_MAX);
    
    if (self.activePluginController) {
        [self setActivePluginWithIdentifier:nil];
        if (val < 0.5) {
#ifndef TARGET_IS_EXTENSION
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://pocketcampus.epfl.ch/redirect.php?time=1&url=pocketcampus://map.plugin.pocketcampus.org"]];
#endif
        }
    } else {
#ifndef TARGET_IS_EXTENSION
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"pocketcampus://map.plugin.pocketcampus.org"]];
#endif
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5+(val*3.0) * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self goCrazyWithMap];
    });
}*/

#pragma mark - Memory warning handler

- (void)appDidReceiveMemoryWarning {
    /* release backgrounded plugins */
    CLSNSLog(@"-> AppDidReceiveMemoryWarning: releasing backgrounded plugins if any...");
    [[self.pluginControllerForIdentifierName copy] enumerateKeysAndObjectsUsingBlock:^(NSString* pluginIdentifier, PluginController* pluginController, BOOL *stop) {
        if (pluginController != self.activePluginController) {
            [self.pluginControllerForIdentifierName removeObjectForKey:pluginIdentifier];
        }
    }];
}

#pragma mark - User defaults changes handler

- (void)pcConfigUserDefaultsDidChange {
    [self initAnalytics];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSUserDefaultsDidChangeNotification object:[PCConfig defaults]];
}

#pragma mark - Called by MainMenuViewController

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
        infos[kPluginsMainMenuItemsInfoOrderNumberKey] = [NSNumber numberWithUnsignedInt:(unsigned int)index];
        infos[kPluginsMainMenuItemsInfoHiddenKey] = [NSNumber numberWithBool:item.hidden];
        if ([item.identifier isEqualToString:activePluginIdentifier] && item.hidden) {
            shouldLeaveActivePluginController = YES;
        }
        menuItemsInfo[item.identifier] = infos;
    }];
    
    if (shouldLeaveActivePluginController) {
        [self requestLeavePlugin:activePluginIdentifier];
    }
    
    if (![PCPersistenceManager saveObject:menuItemsInfo forKey:kPluginsMainMenuItemsInfoKey pluginName:@"pocketcampus"]) {
#ifdef TARGET_IS_MAIN_APP
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Sorry, an error occured while saving the main menu state." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
#endif
    }
}

- (void)restoreDefaultMainMenu {
    if (![PCPersistenceManager saveObject:nil forKey:kPluginsMainMenuItemsInfoKey pluginName:@"pocketcampus"]) {
#ifdef TARGET_IS_MAIN_APP
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Sorry, an error occured while restoring default main menu." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        return;
#endif
    }
    NSMutableArray* menuItems = [self defaultMainMenuItemsWithoutTopSection];
    
    [self.mainMenuViewController reloadWithMenuItems:menuItems];
    
    if (![PCUtils isIdiomPad] && self.activePluginController) {
        [self.revealController showFrontViewCompletely:NO];
    }
    
    [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(selectActivePluginInMainMenuIfNecessary) userInfo:nil repeats:NO];
}

- (void)showGlobalSettings {
#ifdef TARGET_IS_MAIN_APP
    [[PCGAITracker sharedTracker] trackAction:@"OpenSettings" inScreenWithName:@"/dashboard"];
    PCGlobalSettingsViewController* settingsViewController = [[PCGlobalSettingsViewController alloc] initWithMainController:self];
    UINavigationController* settingsNavController = [[PCNavigationController alloc] initWithRootViewController:settingsViewController];
    settingsNavController.modalPresentationStyle = UIModalPresentationFormSheet;
    self.settingsNavController = settingsNavController;
    [self.revealController presentViewController:settingsNavController animated:YES completion:NULL];
#endif
}

#pragma mark setActivePluginWithIdentifier:

- (void)setActivePluginWithIdentifier:(NSString*)identifier {
    
    if (!self.initDone) {
        self.initialActivePluginIdentifier = identifier;
        return;
    }
    
    if (!identifier) { //means switch to splash view controller
        [self endIgnoringRevealMainMenuGesture];
        if (self.activePluginController) {
            [self.pluginControllerForIdentifierName removeObjectForKey:[self.activePluginController.class identifierName]];
        }
        [self.mainMenuViewController setSelectedPluginWithIdentifier:nil animated:YES];
        if (self.revealController.presentedViewController && self.revealController.presentedViewController != self.settingsNavController) {
            [self.revealController.presentedViewController.presentingViewController dismissViewControllerAnimated:NO completion:NULL];
        }
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
    [self endIgnoringRevealMainMenuGesture];
    if (self.revealController.presentedViewController && self.revealController.presentedViewController != self.settingsNavController) {
        [self.revealController.presentedViewController.presentingViewController dismissViewControllerAnimated:NO completion:NULL];
    }
    PluginController<PluginControllerProtocol>* pluginController = self.pluginControllerForIdentifierName[identifier];
    if (pluginController) { // pluginController was backgrounded
        UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
        [self.revealController setFrontViewController:pluginRootViewController animated:NO]; //check on whether this is already the front one is done in the method implementation
    } else {
        Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifier]);
        if (!pluginClass) {
            @throw [NSException exceptionWithName:@"Bad plugin identifier" reason:[NSString stringWithFormat:@"Controller class does not exist for idenfier %@", identifier] userInfo:nil];
        }
        pluginController = [pluginClass sharedInstanceToRetain];
        [self adaptInitializedNavigationOrSplitViewControllerOfPluginController:pluginController];
        UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
        
        if (!pluginRootViewController) {
            CLSNSLog(@"!! ERROR: could not obtain pluginRootViewController for plugin identifier %@", identifier);
            return;
        }
        
        [self manageBackgroundPlugins];
        self.pluginControllerForIdentifierName[identifier] = pluginController;
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

- (NSString*)validPluginIdentifierForAnycasePluginIdentifier:(NSString*)anycaseIdentifier {
    NSString* lowercaseIdentifier = [anycaseIdentifier lowercaseString];
    for (NSString* validIdentifier in self.pluginsList){
        if ([[validIdentifier lowercaseString] isEqualToString:lowercaseIdentifier]) {
            return validIdentifier;
        }
    }
    for (NSString* validIdentifier in self.logicOnlyPluginsList){
        if ([[validIdentifier lowercaseString] isEqualToString:lowercaseIdentifier]) {
            return validIdentifier;
        }
    }
    return nil;
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
    
    for (NSString* originalIdentifier in self.logicOnlyPluginsList) {
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
    pluginController = [pluginClass sharedInstanceToRetain];
    return pluginController;
}

- (BOOL)existsPluginWithIdentifier:(NSString*)identifier {
    @synchronized (self) {
        if ([self.validatedPluginNamesCache containsObject:identifier]) {
            return YES;
        }
        for (NSString* originalIdentifier in self.pluginsList) {
            if ([[identifier lowercaseString] isEqualToString:[originalIdentifier lowercaseString]]) {
                [self.validatedPluginNamesCache addObject:identifier];
                return YES;
            }
        }
        for (NSString* originalIdentifier in self.logicOnlyPluginsList) {
            if ([[identifier lowercaseString] isEqualToString:[originalIdentifier lowercaseString]]) {
                [self.validatedPluginNamesCache addObject:identifier];
                return YES;
            }
        }
        return NO;
    }
}


#pragma mark - ViewControllers Utils

- (UIViewController*)rootViewControllerForPluginController:(PluginController*)pluginController {
    UIViewController* pluginRootViewController = nil;
    if (pluginController.mainNavigationController) {
        pluginRootViewController = pluginController.mainNavigationController;
    } else if (pluginController.mainTabBarController) {
        pluginRootViewController = pluginController.mainTabBarController;
    } else if (pluginController.mainSplitViewController) {
        pluginRootViewController = pluginController.mainSplitViewController;
    } else {
        CLSNSLog(@"!! ERROR : PluginController '%@' has no initialized view controller (mainViewController, mainNavigationController, mainSplitViewController are nil)", [(id<PluginControllerProtocol>)pluginController identifierName]);
    }
    return pluginRootViewController;
}

- (void)adaptInitializedNavigationOrSplitViewControllerOfPluginController:(PluginController*)pluginController {
    
    if (!pluginController) {
        [NSException raise:@"Illegal argument" format:@"pluginController cannot be nil"];
    }
    
    NSInteger nbControllersInstanciated = 0;
    nbControllersInstanciated += (pluginController.mainNavigationController ? 1 : 0);
    nbControllersInstanciated += (pluginController.mainTabBarController ? 1 : 0);
    nbControllersInstanciated += (pluginController.mainSplitViewController ? 1 : 0);
    
    if (nbControllersInstanciated > 1) {
        [NSException raise:@"Incorrect attributes" format:@"only one among mainNavigationController, mainTabBarController, or mainSplitViewController can be instanciated."];
    }
    
    if (nbControllersInstanciated == 0) {
        [NSException raise:@"Incorrect attributes" format:@"at least one among mainNavigationController, mainTabBarController, or mainSplitViewController can be instanciated."];
    }
    
    UIViewController* pluginRootViewController = [self rootViewControllerForPluginController:pluginController];
    
    if (!pluginRootViewController || ![pluginRootViewController respondsToSelector:@selector(pluginIdentifier)] || ![(id)pluginRootViewController pluginIdentifier]) {
        [NSException raise:@"Incorrect attribute pluginIdentifier" format:@"Root view controller of pluginController must have initialized pluginIdentifier property"];
    }
    
    /*
     * This gesture is added to the view of every plugin's navigation controller or split view controlle to allow user to pan from
     * left screen edge to reveal the main menu. See UIGestureRecognizerDelegate delegation (below) for reaction to the gesture.
     */
    UIPanGestureRecognizer* revealPanGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    revealPanGesture.delegate = self;
    
    /*
     * Creating a view entirely covered with pan and tap gestures.
     * This view is added to the view of every plugin's navigation controller or split view controller to allow user to pan or tap
     * anywhere on the plugin's view when it is shifted the right (i.e. main menu currently visible) to bring it back into foreground.
     */
    UIPanGestureRecognizer* bringToFrontPanGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    UITapGestureRecognizer* bringToFrontTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealToggle:)];
    UIView* bringToFrontGesturesView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1.0, 1.0)];
    bringToFrontGesturesView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    bringToFrontGesturesView.tag = kGesturesViewTag;
    bringToFrontGesturesView.gestureRecognizers = @[bringToFrontPanGesture, bringToFrontTapGesture];
    
    if ([pluginRootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController* navController = (UINavigationController*)pluginRootViewController;
        [navController.view addGestureRecognizer:revealPanGesture];
        [navController.view addSubview:bringToFrontGesturesView];

        [[(UIViewController*)(navController.viewControllers[0]) navigationItem] setLeftBarButtonItem:[self newMainMenuButton]];
        
    } else if ([pluginRootViewController isKindOfClass:[UITabBarController class]]) {
        UITabBarController* tabBarController = (UITabBarController*)pluginRootViewController;
        [tabBarController.view addGestureRecognizer:revealPanGesture];
        [tabBarController.view addSubview:bringToFrontGesturesView];
        
        for (UIViewController* level1ViewController in tabBarController.viewControllers) {
            UIViewController* viewControllerMenuButton = level1ViewController;
            if ([level1ViewController isKindOfClass:[UINavigationController class]]) {
                viewControllerMenuButton = [[(UINavigationController*)level1ViewController viewControllers] firstObject];
            }
            viewControllerMenuButton.navigationItem.leftBarButtonItem = [self newMainMenuButton];
        }
        
    } else if ([pluginRootViewController isKindOfClass:[UISplitViewController class]]) {
        UISplitViewController* splitController = (UISplitViewController*)pluginRootViewController;
        [splitController.view addGestureRecognizer:revealPanGesture];
        splitController.view.autoresizesSubviews = YES;
        [splitController.view addSubview:bringToFrontGesturesView];
        
        UIViewController* masterViewController = splitController.viewControllers[0];
        NSArray* viewControllersMenuButton = nil;
        if ([masterViewController isKindOfClass:[UINavigationController class]]) {
            viewControllersMenuButton = @[[[(UINavigationController*)masterViewController viewControllers] firstObject]];
        } else if ([masterViewController isKindOfClass:[UITabBarController class]]) {
            UITabBarController* tabBarController = (UITabBarController*)masterViewController;
            NSMutableArray* mViewControllersMenuButton = [NSMutableArray arrayWithCapacity:tabBarController.viewControllers.count];
            for (UIViewController* level1ViewController in tabBarController.viewControllers) {
                if ([level1ViewController isKindOfClass:[UINavigationController class]]) {
                    [mViewControllersMenuButton addObject:[[(UINavigationController*)level1ViewController viewControllers] firstObject]];
                } else {
                    [mViewControllersMenuButton addObject:level1ViewController];
                }
            }
            viewControllersMenuButton = mViewControllersMenuButton;
        } else {
            viewControllersMenuButton = @[masterViewController];
        }
    
        for (UIViewController* viewController in viewControllersMenuButton) {
            viewController.navigationItem.leftBarButtonItem = [self newMainMenuButton];
        }
    }
}

- (UIBarButtonItem*)newMainMenuButton {
    UIBarButtonItem* menuButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MainMenuNavbar"] style:UIBarButtonItemStylePlain target:self.revealController action:@selector(revealToggle:)];
    menuButton.accessibilityLabel = NSLocalizedStringFromTable(@"MainMenu", @"PocketCampus", nil);
    return menuButton;
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
        if ([device orientation] == UIDeviceOrientationUnknown || ([device orientation] != UIDeviceOrientationPortrait && [device orientation] != UIDeviceOrientationFaceUp)) {
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
            [viewController presentViewController:[UIViewController new] animated:NO completion:NULL];
            [viewController dismissViewControllerAnimated:NO completion:NULL];
        }
    }
}

#pragma mark - Other Utils

- (void)manageBackgroundPlugins {
    if (BACKGROUND_PLUGINS_ENABLED) {
        CLSNSLog(@"!! WARNING: background plugins management is not fully supported. Plugins will simply stay in memory until app receives memory warning.");
    } else {
        [self.pluginControllerForIdentifierName removeAllObjects];
    }
}

- (void)showActionNotSupportedAlert {
#ifdef TARGET_IS_MAIN_APP
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Sorry", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ActionNotSupportedYet", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
#endif
}

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
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"pluginIdentifierName cannot be nil." userInfo:nil];
    }
    if (![identifier isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"pluginIdentifierName is not kind of class NSString." userInfo:nil];
    }
    if (![self.pluginsList containsObject:identifier] && ![self.logicOnlyPluginsList containsObject:identifier]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"pluginIdentifierName does not correspond to any existing identifier. Please use [PluginControllerProtocol identifierName] as argument." userInfo:nil];
    }
}

- (void)selectActivePluginInMainMenuIfNecessary {
    if (self.activePluginController) {
        NSString* activePluginIdentifier = [self.activePluginController.class identifierName];
        [self.mainMenuViewController setSelectedPluginWithIdentifier:activePluginIdentifier animated:YES];
    }
}

#pragma mark - CrashlyticsDelegate

- (void)crashlytics:(Crashlytics *)crashlytics didDetectCrashDuringPreviousExecution:(id<CLSCrashReport>)crash {
#ifndef TARGET_IS_EXTENSION
    [[PCGAITracker sharedTracker] trackAppCrashedDuringPreviousExecution];
#endif
}

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    if (![gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]
        || self.ignoreRevealMainMenuGesture) {
        return NO;
    }
    /*
     * Assuming gesture is the reveal pan gesture
     * OK, because self is delegate for this gesture only
     */
    
    if ([touch.view isKindOfClass:[UINavigationBar class]]) {
        /*
         * Allow panning anywhere on the navigation bar
         */
        return YES;
    }
    /*
     * Accept gesture only if started from the left edge of the screen
     */
    CGPoint point = [touch locationInView:gestureRecognizer.view];
    static CGFloat threshold;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        threshold = 0.05 * [UIScreen mainScreen].bounds.size.width;
    });
    return point.x < threshold;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    if ([otherGestureRecognizer isKindOfClass:[UIScreenEdgePanGestureRecognizer class]]) {
        /*
         * UIScreenEdgePanGestureRecognizer is iOS 7 gesture to pop view controller in navigation controller.
         * We don't want to do anything when this gesture is in progress => stopping our reveal gesture.
         */
        return NO;
    }
    otherGestureRecognizer.enabled = NO;
    otherGestureRecognizer.enabled = YES;
    return YES;
}

#pragma mark - ZUUIRevealControllerDelegate

- (void)revealController:(ZUUIRevealController *)revealController willRevealRearViewController:(UIViewController *)rearViewController {
    self.mainMenuViewController.view.accessibilityElementsHidden = NO;
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
    self.mainMenuViewController.view.accessibilityElementsHidden = YES;
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

/*- (void)revealController:(ZUUIRevealController *)revealController willSwapToFrontViewController:(UIViewController *)frontViewController {
    //nothing
}*/

- (void)revealController:(ZUUIRevealController *)revealController didSwapToFrontViewController:(UIViewController *)frontViewController {
    if (frontViewController == self.splashViewController) {
        [self.splashViewController willMoveToRightWithDuration:self.revealController.toggleAnimationDuration hideDrawingOnIdiomPhone:YES];
    }
}

@end
