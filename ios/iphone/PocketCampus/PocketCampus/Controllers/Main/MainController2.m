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

#import <objc/message.h>

@interface MainController2 ()

@property (nonatomic, assign) UIWindow* window;
@property (nonatomic, retain) MainMenuViewController* mainMenuViewController;
@property (nonatomic, retain) ZUUIRevealController* revealController;
@property (nonatomic, retain) NSArray* pluginsList;
@property (nonatomic, retain) PluginController* activePluginController;

@end

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
        [self initRevealController];
        self.revealController.toggleAnimationDuration = 1.0;
        //self.revealController.frontViewShadowRadius = 0.0;
        self.window.rootViewController = self.revealController;
        [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(revealMenuAfterSplash) userInfo:nil repeats:NO];
    }
    return self;
}

- (void)revealMenuAfterSplash {
    [self.revealController hideFrontView];
    self.revealController.toggleAnimationDuration = 0.25;
    self.revealController.frontViewShadowRadius = 2.5f;
}

- (void)initPluginsList {
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Plugins" ofType:@"plist"]];
    NSArray* pluginsFromPlist = [plist objectForKey:@"Plugins"];
    NSMutableArray* pluginsTempArray = [NSMutableArray arrayWithCapacity:pluginsFromPlist.count];
    for (NSDictionary* pluginDic in pluginsFromPlist) {
        NSString* identifierName = [pluginDic objectForKey:@"identifierName"];
        if ([[pluginDic objectForKey:@"enabled"] boolValue]) {
            NSLog(@"-> Detected enabled plugin : %@", identifierName);
            [pluginsTempArray addObject:identifierName];
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
    //UIViewController* blankPluginViewController = [[UIViewController alloc] initWithNibName:@"BlankPluginView" bundle:nil];
    CGSize screenSize = [UIScreen mainScreen].bounds.size;
    UIView* splashView = [[UIView alloc] initWithFrame:CGRectMake(0.0, 0.0, screenSize.width, screenSize.height)];
    splashView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    UIImageView* splashViewImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Default"]];
    splashViewImage.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    | UIViewAutoresizingFlexibleTopMargin;
    //| UIViewAutoresizingFlexibleRightMargin
    //| UIViewAutoresizingFlexibleBottomMargin;
    splashView.center = splashView.center;
    splashView.autoresizesSubviews = YES;
    splashView.backgroundColor = [UIColor colorWithRed:0.961 green:0.957 blue:0.941 alpha:1];
    [splashView addSubview:splashViewImage];
    [splashViewImage release];
    UIViewController* splashViewController = [[UIViewController alloc] init];
    splashViewController.view = splashView;
    ZUUIRevealController* revealController = [[ZUUIRevealController alloc] initWithFrontViewController:splashViewController rearViewController:self.mainMenuViewController];
    revealController.rearViewRevealWidth = 287.0;
    [splashViewController release];
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
    UIBarButtonItem* menuButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"HomeNavbar"] style:UIBarButtonItemStylePlain target:self.revealController action:@selector(revealToggle:)];
    pluginController.mainViewController.navigationItem.leftBarButtonItem = menuButton;
    [menuButton release];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:pluginController.mainViewController];
    navController.navigationBar.tintColor = [PCValues pocketCampusRed];
    
    UIPanGestureRecognizer* navigationBarPanGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.revealController action:@selector(revealGesture:)];
    [navController.navigationBar addGestureRecognizer:navigationBarPanGestureRecognizer];
    [navigationBarPanGestureRecognizer release];
    
    [self.revealController setFrontViewController:navController];
    [navController release];
    //[self.revealController revealToggle:self];
    self.activePluginController = pluginController;
}

@end
