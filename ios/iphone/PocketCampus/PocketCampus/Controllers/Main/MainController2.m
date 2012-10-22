//
//  MainController2.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainController2.h"

#import "ZUUIRevealController.h"

#import "PluginController.h"

#import "MainMenuViewController.h"

#import "MainMenuItem.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "SplashViewController.h"

#import <objc/message.h>

@interface MainController2 ()

@property (nonatomic, assign) UIWindow* window;
@property (nonatomic, retain) MainMenuViewController* mainMenuViewController;
@property (nonatomic, retain) ZUUIRevealController* revealController;
@property (nonatomic) CGFloat revealWidth;
@property (nonatomic, retain) NSArray* pluginsList;
@property (nonatomic, retain) PluginController* activePluginController;

@end

static NSString* kSupportedIdiomPhone = @"phone";
static NSString* kSupportedIdiomPad = @"pad";
static NSString* kSupportedIdiomPhonePad = @"phone+pad";

@implementation MainController2

- (id)initWithWindow:(UIWindow *)window
{
    self = [super init];
    if (self) {
        self.window = window;
        self.activePluginController = nil;
        [self initPluginsList];
        [self initPluginObservers];
        [self initMainMenu];
        if ([PCUtils isIdiomPad]) {
            self.revealWidth = 320.0;
        } else {
            self.revealWidth = 261.0;
        }
        [self initRevealController];
        self.revealController.toggleAnimationDuration = 0.8;
        self.window.rootViewController = self.revealController;
    }
    return self;
}

- (void)mainMenuIsReady {
    if (![self.revealController.frontViewController isViewLoaded]) {
        [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(mainMenuIsReady) userInfo:nil repeats:NO];
    } else {
        [self revealMenuAfterSplash];
    }
}

- (void)revealMenuAfterSplash {
    [(SplashViewController*)(self.revealController.frontViewController) willMoveToRightWithDuration:self.revealController.toggleAnimationDuration];
    if ([PCUtils isIdiomPad]) {
        [self.revealController revealToggle:self];
    } else {
        [self.revealController hideFrontView];
    }
    self.revealController.toggleAnimationDuration = 0.25;
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
    
    MainMenuViewController* mainMenuViewController = [[MainMenuViewController alloc] initWithMenuItems:menuItems mainController:self];
    self.mainMenuViewController = mainMenuViewController;
    [mainMenuViewController release];
}

- (void)initRevealController {
    SplashViewController* splashViewController = [[SplashViewController alloc] initWithRightHiddenOffset:self.revealWidth];
    ZUUIRevealController* revealController = [[ZUUIRevealController alloc] initWithFrontViewController:splashViewController rearViewController:self.mainMenuViewController];
    revealController.rearViewRevealWidth = self.revealWidth;
    self.revealController = revealController;
    [revealController release];
}

- (NSString*)pluginControllerClassNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (void)refreshDisplayedPlugin {
    if ([self.activePluginController respondsToSelector:@selector(refresh)]) {
        [self.activePluginController performSelector:@selector(refresh)];
    }
}

- (void)setActivePluginWithIdentifier:(NSString*)identifier {
    Class pluginClass = NSClassFromString([self pluginControllerClassNameForIdentifier:identifier]);
    PluginController* pluginController = [[pluginClass alloc] initWithMainController:nil];
    UIBarButtonItem* menuButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MainMenuNavbar"] style:UIBarButtonItemStylePlain target:self.revealController action:@selector(revealToggle:)];
    pluginController.mainViewController.navigationItem.leftBarButtonItem = menuButton;
    [menuButton release];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:pluginController.mainViewController];
    navController.navigationBar.tintColor = [PCValues pocketCampusRed];
    
    UIPanGestureRecognizer* navigationBarPanGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    [navController.navigationBar addGestureRecognizer:navigationBarPanGestureRecognizer];
    [navigationBarPanGestureRecognizer release];
    
    /*UITapGestureRecognizer* tapGestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    [navController.view addGestureRecognizer:tapGestureRecognizer];
    [tapGestureRecognizer release];*/
    
    [self.revealController setFrontViewController:navController];
    [navController release];
    self.activePluginController = pluginController;
}

@end
