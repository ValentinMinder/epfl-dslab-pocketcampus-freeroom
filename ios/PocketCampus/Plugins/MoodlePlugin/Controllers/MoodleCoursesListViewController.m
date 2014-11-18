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

//  Created by Loïc Gardiol on 04.12.12.

#import "MoodleCoursesListViewController.h"

#import "MoodleController.h"

#import "PCCenterMessageCell.h"

#import "MoodleCourseSectionsViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "MoodleService.h"

#import "PluginSplitViewController.h"

#import "PCTableViewCellAdditions.h"

#import "MoodleSettingsViewController.h"

#import "CloudPrintController.h"


static const NSTimeInterval kRefreshValiditySeconds = 259200.0; //3 days

@interface MoodleCoursesListViewController ()<PCMasterSplitDelegate, MoodleServiceDelegate>

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleCoursesResponse2* coursesResponse;
@property (nonatomic, readonly) NSArray* courses;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) UIPopoverController* settingsPopover;

@end

@implementation MoodleCoursesListViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/moodle";
        self.title = NSLocalizedStringFromTable(@"MyCourses", @"MoodlePlugin", nil);
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.coursesResponse = [self.moodleService getFromCacheCoursesWithRequest:[self newMoodleCoursesRequest]];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIBarButtonItem* settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsBarButton"] style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButtonPressed)];
    settingsButton.accessibilityLabel = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    
    self.navigationItem.rightBarButtonItem = settingsButton;
    
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault]*1.3);
    };
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"moodle" dataName:@"coursesList"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    if (!self.courses || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

/*#warning REMOVE

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    PrintDocumentRequest* request = [PrintDocumentRequest createDefaultRequest];
    request.documentId = 1;
    UIViewController* viewController = [[CloudPrintController sharedInstance] viewControllerForPrintWithDocumentName:@"Test.pdf" printDocumentRequest:request completion:^(CloudPrintCompletionStatusCode printStatusCode) {
        NSLog(@"completion: %d", printStatusCode);
        [self.navigationController dismissViewControllerAnimated:YES completion:NULL];
    }];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.navigationController presentViewController:viewController animated:YES completion:NULL];
    });
}

#warning END OF REMOVE*/

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - Properties

- (NSArray*)courses {
    return self.coursesResponse.courses;
}

#pragma mark - Refresh control

- (void)refresh {
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourseList", @"MoodlePlugin", nil)];
    [self startGetCoursesListRequest];
}

- (void)startGetCoursesListRequest {
    [self.moodleService getCoursesWithRequest:[self newMoodleCoursesRequest] delegate:self];
}

- (MoodleCoursesRequest2*)newMoodleCoursesRequest {
    return [[MoodleCoursesRequest2 alloc] initWithLanguage:[PCUtils userLanguageCode]];
}

#pragma mark - Buttons actions

- (void)settingsButtonPressed {
    [self trackAction:@"OpenSettings"];
    MoodleSettingsViewController* settingsViewController = [[MoodleSettingsViewController alloc] init];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:settingsViewController];
    if (self.splitViewController) {
        if (!self.settingsPopover) {
            self.settingsPopover = [[UIPopoverController alloc] initWithContentViewController:navController];
        }
        [self.settingsPopover togglePopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
    } else {
        [self presentViewController:navController animated:YES completion:NULL];
    }
}

#pragma mark - PCMasterSplitDelegate (used on iPad only)

- (UIViewController*)detailViewControllerThatShouldBeDisplayed {
    MoodleSplashDetailViewController* detailViewController = [[MoodleSplashDetailViewController alloc] init];
    return [[PCNavigationController alloc] initWithRootViewController:detailViewController];
}

#pragma mark - MoodleServiceDelegate

- (void)getCoursesForRequest:(MoodleCoursesRequest2 *)request didReturn:(MoodleCoursesResponse2 *)response {
    switch (response.statusCode) {
        case MoodleStatusCode2_OK:
            self.coursesResponse = response;
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            break;
        case MoodleStatusCode2_AUTHENTICATION_ERROR:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [welf startGetCoursesListRequest];
            } userCancelled:^{
                [welf.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"LoginRequired", @"PocketCampus", nil)];
            } failure:^(NSError *error) {
                [welf error];
            }];
            break;
        }
        case MoodleStatusCode2_NETWORK_ERROR:
        {
            [self.lgRefreshControl endRefreshing];
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            break;
        }
        default:
            [self error];
            break;
    }
}

- (void)getCoursesFailedForRequest:(MoodleCoursesRequest2 *)request {
    [self error];
}

- (void)error {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.courses.count == 0) {
        return;
    }
    MoodleCourse2* course = self.courses[indexPath.row];
    [self trackAction:@"ViewCourse" contentInfo:[NSString stringWithFormat:@"%ld-%@", course.courseId, course.name]];
    MoodleCourseSectionsViewController* viewController = [[MoodleCourseSectionsViewController alloc] initWithCourse:course];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.courses && [self.courses count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"MoodleNoCourse", @"MoodlePlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"CourseCell"];
    MoodleCourse2* course = self.courses[indexPath.row];
    PCTableViewCellAdditions *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        [cell setAccessibilityHintBlock:^NSString *{
            return NSLocalizedStringFromTable(@"ShowsDocumentsForThisCourse", @"MoodlePlugin", nil);
        }];
        [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
            return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
        }];
    }
    
    cell.textLabel.text = course.name;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if (self.courses && [self.courses count] == 0) {
        return 2; //first empty cell, second cell says no content
    }
    return [self.courses count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.courses) {
        return 0;
    }
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc {
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.moodleService cancelOperationsForDelegate:self];
}

@end
