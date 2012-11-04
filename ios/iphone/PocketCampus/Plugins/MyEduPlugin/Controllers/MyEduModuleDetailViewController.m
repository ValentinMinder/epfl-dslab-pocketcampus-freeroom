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

#import "PluginSplitViewController.h"

#import "PCValues.h"

@interface MyEduModuleDetailViewController ()

@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) MyEduCourse* course;

@end

@implementation MyEduModuleDetailViewController

- (id)initWithModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduModuleDetailView" bundle:nil];
    if (self) {
        _module = module;
        _section = section;
        _course = course;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    NSString* title = [NSString stringWithFormat:@"%@ ‣ %@", self.section.iTitle, self.module.iTitle];
    
    MyEduModuleVideoViewController* videoController = [[MyEduModuleVideoViewController alloc] initWithMyEduModule:self.module];
    videoController.navigationItem.title = [NSString stringWithFormat:@"%@ ‣ %@", title, NSLocalizedStringFromTable(@"Video", @"MyEduPlugin", nil)];
    videoController.navigationItem.leftBarButtonItem = [self toggleMasterViewBarButtonItem];
    UINavigationController* videoNavController = [[UINavigationController alloc] initWithRootViewController:videoController];
    videoNavController.title = NSLocalizedStringFromTable(@"Video", @"MyEduPlugin", nil);
    videoNavController.tabBarItem.image = [UIImage imageNamed:@"MyEduModuleVideo"];
    videoNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
    
    MyEduModuleTextViewController* textController = [[MyEduModuleTextViewController alloc] initWithMyEduModule:self.module];
    textController.navigationItem.title = [NSString stringWithFormat:@"%@ ‣ %@", title, NSLocalizedStringFromTable(@"Text", @"MyEduPlugin", nil)];
    textController.navigationItem.leftBarButtonItem = [self toggleMasterViewBarButtonItem];
    UINavigationController* textNavController = [[UINavigationController alloc] initWithRootViewController:textController];
    textNavController.title = NSLocalizedStringFromTable(@"Text", @"MyEduPlugin", nil);
    textNavController.tabBarItem.image = [UIImage imageNamed:@"MyEduModuleText"];
    textNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
    
    self.tabBarController.viewControllers = @[videoNavController, textNavController];
    
    [self addChildViewController:self.tabBarController];
    [self.view addSubview:self.tabBarController.view];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UIBarButtonItem*)toggleMasterViewBarButtonItem {
    UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 25.0, 25.0)];
    [button setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
    button.adjustsImageWhenHighlighted = NO;
    button.showsTouchWhenHighlighted = YES;
    [button addTarget:self action:@selector(toggleMasterVideoControllerHidden:) forControlEvents:UIControlEventTouchUpInside];

    return [[UIBarButtonItem alloc] initWithCustomView:button];
}

- (void)toggleMasterVideoControllerHidden:(UIButton*)sender {
    if ([self.splitViewController isKindOfClass:[PluginSplitViewController class]]) { //should always be the case
        PluginSplitViewController* pluginSplitViewController = (PluginSplitViewController*)self.splitViewController;
        if (pluginSplitViewController.masterViewControllerHidden) {
            [sender setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
            [pluginSplitViewController setMasterViewControllerHidden:NO animated:YES];
        } else {
            [sender setImage:[UIImage imageNamed:@"ArrowShow"] forState:UIControlStateNormal];
            [pluginSplitViewController setMasterViewControllerHidden:YES animated:YES];
        }
    }
}

@end
