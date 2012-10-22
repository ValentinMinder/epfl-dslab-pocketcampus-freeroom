//
//  MyEduController.m
//  PocketCampus
//
//

#import "MyEduController.h"


@implementation MyEduController

- (id)init
{
    self = [super init];
    if (self) {
        /*
        * TODO: init mainViewController
        * Example :
        * NewsListViewController* newsListViewController = [[NewsListViewController alloc] init];
        * newsListViewController.title = [[self class] localizedName];
        * mainViewController = newsListViewController;
        */
    }
    return self;
}

- (id)initWithMainController:(MainController *)mainController_
{
    self = [self init];
    if (self) {
        mainController = mainController_;
        
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

- (void)dealloc
{
    [super dealloc];
}

@end
