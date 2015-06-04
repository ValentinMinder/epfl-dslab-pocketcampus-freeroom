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

//  Created by Loïc Gardiol on 03.06.15.

#import "IsAcademiaGradesViewController.h"

#import "IsAcademiaService.h"

#import "PCCenterMessageCell.h"

#import "IsAcademiaCourseGradeCell.h"

static const NSTimeInterval kRefreshValiditySeconds = 2.0 * 60.0; //2 min

@interface IsAcademiaGradesViewController ()<IsAcademiaServiceDelegate>

@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) IsAcademiaService* isaService;
@property (nonatomic, strong) NSArray* semesters; //array of SemesterGrades

@end

@implementation IsAcademiaGradesViewController

#pragma mark - Init

- (id)init
{
    self = [super init];
    if (self) {
        self.gaiScreenName = @"/isacademia/grades";
        self.isaService = [IsAcademiaService sharedInstanceToRetain];
        self.semesters = [self.isaService getGradesFromCache].semesters;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tabBarController.tabBar.frame = CGRectZero;
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] initWithFrame:self.tableView.frame style:UITableViewStyleGrouped];
    self.tableView = tableViewAdditions;
    self.tableView.separatorColor = [UIColor clearColor];
    self.tableView.backgroundColor = [UIColor whiteColor];//[UIColor colorWithRed:0.972549 green:0.972549 blue:0.972549 alpha:1.0];
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 25.0, 0, 0);
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"isacademia" dataName:@"grades"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.semesters || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Private

- (void)refresh {
    [self.isaService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingGrades", @"IsAcademiaPlugin", nil)];
    [self startGetGradesRequest];
}

- (void)startGetGradesRequest {
    [self.isaService getGradesWithDelegate:self];
}

#pragma mark - IsaServiceDelegate

- (void)getGradesDidReturn:(IsaGradesResponse *)gradesResponse {
    switch (gradesResponse.statusCode) {
        case IsaStatusCode_OK:
        {
            self.semesters = gradesResponse.semesters;
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            break;
        }
        case IsaStatusCode_INVALID_SESSION:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [welf startGetGradesRequest];
            } userCancelled:^{
                [welf.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"LoginRequired", @"PocketCampus", nil)];
            } failure:^(NSError *error) {
                [welf getGradesFailed];
            }];
            break;
        }
        case IsaStatusCode_NETWORK_ERROR:
        {
            [self.lgRefreshControl endRefreshing];
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"IsAcademiaServerUnreachableTryLater", @"IsAcademiaPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            break;
        }
        case IsaStatusCode_ISA_ERROR:
        {
            [self.lgRefreshControl endRefreshing];
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"IsAcademiaServerProblemTryLater", @"IsAcademiaPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            break;
        }
        default:
            [self getGradesFailed];
            break;
    }
}

- (void)getGradesFailed {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section {
    if (!self.semesters) {
        return;
    }
    SemesterGrades* semester = self.semesters[section];
    UITableViewHeaderFooterView* header = (UITableViewHeaderFooterView*)view;
    header.textLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:19.0];
    header.textLabel.text = semester.semesterName;
    header.textLabel.textColor = [UIColor blackColor];
}

- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section {
    if (!self.semesters) {
        return;
    }
    UITableViewHeaderFooterView* footer = (UITableViewHeaderFooterView*)view;
    if (section == 0 && self.semesters && self.semesters.count == 0) {
        footer.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        footer.textLabel.textColor = [UIColor darkGrayColor];
        footer.textLabel.textAlignment = NSTextAlignmentCenter;
    } else {
        footer.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleCaption1];
        footer.textLabel.textColor = [UIColor lightGrayColor];
        footer.textLabel.textAlignment = NSTextAlignmentLeft;
    }
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (self.semesters.count == 0) {
        return nil;
    }
    SemesterGrades* semester = self.semesters[section];
    return semester.semesterName;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (!self.semesters) {
        return nil;
    }
    if (section == 0 && self.semesters && self.semesters.count == 0) {
        return NSLocalizedStringFromTable(@"GradesNoContent", @"IsAcademiaPlugin", nil);
    }
    SemesterGrades* semester = self.semesters[section];
    if (semester.grades.count == 0) {
        return NSLocalizedStringFromTable(@"NoCourse", @"IsAcademiaPlugin", nil);
    }
    if (semester.existsCourseWithNoGrade) {
        return NSLocalizedStringFromTable(@"GrayMeansNoGradeAvailableExplanation", @"IsAcademiaPlugin", nil);
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    NSString* text = [self tableView:tableView titleForFooterInSection:section];
    return text.length > 0 ? UITableViewAutomaticDimension : 15.0;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"SemesterGradeCell"];
    SemesterGrades* semester = self.semesters[indexPath.section];
    NSString* courseName = semester.sortedGradesKeys[indexPath.row];
    IsAcademiaCourseGradeCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[IsAcademiaCourseGradeCell alloc] initWithReuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    [cell setCourseName:courseName andGrade:semester.grades[courseName]];
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.semesters && self.semesters.count == 0) {
        return 0;
    }
    SemesterGrades* semester = self.semesters[section];
    return semester.grades.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.semesters && self.semesters.count == 0) {
        return 1;
    }
    return self.semesters.count;
}

@end
