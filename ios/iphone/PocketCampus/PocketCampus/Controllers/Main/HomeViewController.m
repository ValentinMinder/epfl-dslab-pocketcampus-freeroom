//
//  HomeViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "HomeViewController.h"
#import "HomeIcon.h"
#import "PluginController.h"

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
    self.title = NSLocalizedStringFromTable(@"HomeViewControllerTitle", @"PocketCampus", @"Text that will be displayed in nav bar of Home");
    
    UIBarButtonItem* backButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"HomeNavbar"] style:UIBarButtonItemStylePlain target:nil action:nil];
    self.navigationItem.backBarButtonItem = backButton;
    [backButton release];
    UIBarButtonItem* settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsToolbar"] style:UIBarButtonItemStylePlain target:nil action:nil];
    toolbar.items = [NSArray arrayWithObject:settingsButton];
    [settingsButton release];
    toolbar.tintColor = [UIColor colorWithRed:0.26 green:0.305882 blue:0.321568  alpha:1.0];
    toolbar.hidden = YES;

    [self initHomeIcons];
	
}

- (void)viewWillAppear:(BOOL)animated {
    }

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

/*- (void)viewDidAppear:(BOOL)animated {
    UIBarButtonItem* settingsButton = [[UIBarButtonItem alloc] initWithTitle:@"Test" style:UIBarButtonItemStylePlain target:nil action:nil];
    
    [self setToolbarItems:[NSArray arrayWithObject:settingsButton] animated:YES];
}*/

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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
