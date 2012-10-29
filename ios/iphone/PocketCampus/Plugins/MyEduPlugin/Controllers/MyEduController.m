//
//  MyEduController.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduController.h"

#import "MyEduCourseListViewController.h"

@implementation MyEduController

- (id)initWithMainController:(MainController2 *)mainController_
{
    self = [super init];
    if (self) {
        mainController = mainController_;
        MyEduCourseListViewController* courseListViewController = [[MyEduCourseListViewController alloc] init];
        courseListViewController.title = NSLocalizedStringFromTable(@"MyCourses", @"MyEduPlugin", nil);
        PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] init];
        
        UIViewController* rightViewController = [[UIViewController alloc] init];
        UIView* grayView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
        grayView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        grayView.backgroundColor = [UIColor scrollViewTexturedBackgroundColor];
        rightViewController.view = grayView;
        
        splitViewController.delegate = self;
        splitViewController.viewControllers = @[[[UINavigationController alloc] initWithRootViewController:courseListViewController], [[UINavigationController alloc] initWithRootViewController:rightViewController]];
        mainSplitViewController = splitViewController;
        mainSplitViewController.pluginIdentifier = [[self class] identifierName];
    }
    return self;
}

- (void)refresh {
    //TODO: refresh infos displayed by plugin if necessary
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MyEduPlugin", @"");
}

+ (NSString*)identifierName {
    return @"MyEdu";
}

- (void)pluginDidBecomePassive {
    //TODO
}

- (void)pluginWillLoseFocus {
    //TODO
}
- (void)pluginDidRegainActive {
    //TODO
}

#pragma mark UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    return NO;
}

@end
