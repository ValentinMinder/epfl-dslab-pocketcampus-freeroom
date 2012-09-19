//
//  HomeViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "HomeViewController.h"
#import "HomeIcon.h"
#import "PluginController.h"
#import "PCValues.h"

#import "GlobalSettingsViewController.h"

@implementation HomeViewController

- (id)initWithMainController:(MainController*)mainController_;
{
    self = [super initWithNibName:@"HomeView" bundle:nil];
    if (self) {
        mainController = mainController_;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/dashboard" withError:NULL];
#if DEBUG
    self.title = [NSString stringWithFormat:@"%@ Beta", NSLocalizedStringFromTable(@"HomeViewControllerTitle", @"PocketCampus", @"Text that will be displayed in nav bar of Home")];
#else
    self.title = NSLocalizedStringFromTable(@"HomeViewControllerTitle", @"PocketCampus", @"Text that will be displayed in nav bar of Home");
#endif
    UIBarButtonItem* backButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"HomeNavbar"] style:UIBarButtonItemStylePlain target:nil action:nil];
    self.navigationItem.backBarButtonItem = backButton;
    [backButton release];
    [self initHomeIcons];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)iconPressedWithIndex:(NSUInteger)index {
    Class pluginClass = NSClassFromString([mainController pluginControllerNameForIndex:index]);
    PluginController* pluginController = [[pluginClass alloc] initWithMainController:mainController];
    [self.navigationController pushViewController:pluginController.mainViewController animated:YES];
    mainController.activePluginController = pluginController;
    [pluginController release];
}

- (void)initHomeIcons {
    int nbPlugins = mainController.pluginsList.count;
    int index;
    for (index = 0; index < nbPlugins; index++) {
        Class pluginClass = NSClassFromString([mainController pluginControllerNameForIndex:index]);
        NSString* localizedName = [pluginClass localizedName];
        NSString* identifierName = [pluginClass identifierName];
        HomeIcon* icon = [[HomeIcon alloc] initWithController:self index:index title:localizedName normalStateImageName:identifierName andHighlightedStateImageName:identifierName];
        [self.view addSubview:icon];
        [icon release];
    }    
}

- (IBAction)settingsButtonPressed {
    GlobalSettingsViewController* settingsViewController = [[GlobalSettingsViewController alloc] init];
    UINavigationController* settingsNavController = [[UINavigationController alloc] initWithRootViewController:settingsViewController];
    settingsNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
    if ([self.navigationController respondsToSelector:@selector(presentViewController:animated:completion:)]) { // >= iOS 5.0
        [self presentViewController:settingsNavController animated:YES completion:NULL];
    } else {
        [self.navigationController presentModalViewController:settingsNavController animated:YES];
    }
    [settingsViewController release];
    [settingsNavController release];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation // <= iOS 5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
