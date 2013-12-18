//
//  MyEduModuleDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleDetailViewController.h"

#import "MyEduModuleVideoViewController.h"

#import "MyEduModuleTextViewController.h"

#import "MyEduModuleMaterialsViewController.h"

#import "PluginSplitViewController.h"

#import "PCValues.h"

@interface MyEduModuleDetailViewController ()

@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) NSMutableArray* showHideUIButtons;

@property (nonatomic, weak) MyEduModuleVideoViewController* videoViewController; //weak because self.tabBarController owns it

@end

@implementation MyEduModuleDetailViewController

- (id)initWithModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduModuleDetailView" bundle:nil];
    if (self) {
        _module = module;
        _section = section;
        _course = course;
        self.showHideUIButtons = [NSMutableArray arrayWithCapacity:3]; //at least video, text, materials
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    NSString* title = [NSString stringWithFormat:@"%@ ‣ %@", self.section.iTitle, self.module.iTitle];
    
    MyEduModuleVideoViewController* videoController = [[MyEduModuleVideoViewController alloc] initWithMyEduModule:self.module];
    //videoController.navigationItem.title = [NSString stringWithFormat:@"%@ ‣ %@", title, NSLocalizedStringFromTable(@"Video", @"MyEduPlugin", nil)];
    videoController.navigationItem.title = title;
    videoController.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
    UINavigationController* videoNavController = [[UINavigationController alloc] initWithRootViewController:videoController];
    videoNavController.title = NSLocalizedStringFromTable(@"Video", @"MyEduPlugin", nil);
    videoNavController.tabBarItem.image = [UIImage imageNamed:@"MyEduModuleVideo"];
    
    MyEduModuleTextViewController* textController = [[MyEduModuleTextViewController alloc] initWithMyEduModule:self.module];
    //textController.navigationItem.title = [NSString stringWithFormat:@"%@ ‣ %@", title, NSLocalizedStringFromTable(@"Text", @"MyEduPlugin", nil)];
    textController.navigationItem.title = title;
    textController.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
    UINavigationController* textNavController = [[UINavigationController alloc] initWithRootViewController:textController];
    textNavController.title = NSLocalizedStringFromTable(@"Text", @"MyEduPlugin", nil);
    textNavController.tabBarItem.image = [UIImage imageNamed:@"MyEduModuleText"];
    
    MyEduModuleMaterialsViewController* materialsController = [[MyEduModuleMaterialsViewController alloc] initWithMyEduModule:self.module section:self.section course:self.course];
    //materialsController.navigationItem.title = [NSString stringWithFormat:@"%@ ‣ %@", title, NSLocalizedStringFromTable(@"Material", @"MyEduPlugin", nil)];
    materialsController.navigationItem.title = title;
    materialsController.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
    UINavigationController* materialsNavController = [[UINavigationController alloc] initWithRootViewController:materialsController];
    materialsNavController.title = NSLocalizedStringFromTable(@"Material", @"MyEduPlugin", nil);
    materialsNavController.tabBarItem.image = [UIImage imageNamed:@"MyEduModuleMaterial"];

    self.tabBarController.viewControllers = @[videoNavController, textNavController, materialsNavController];
    
    self.videoViewController = videoController;
    
    [self addChildViewController:self.tabBarController];
    [self.view addSubview:self.tabBarController.view];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark UITabBarControllerDelegate

- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
    UIBarButtonItem* barButton = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
    UIViewController* topViewController = viewController;
    if ([viewController isKindOfClass:[UINavigationController class]]) {
        topViewController = [(UINavigationController*)(viewController) topViewController];
    }
    topViewController.navigationItem.leftBarButtonItem = barButton;
}

- (void)dealloc {
    [self.videoViewController destroyVideoPlayer]; //be sure that player does not prevent videoViewController from being deallocated
}

@end
