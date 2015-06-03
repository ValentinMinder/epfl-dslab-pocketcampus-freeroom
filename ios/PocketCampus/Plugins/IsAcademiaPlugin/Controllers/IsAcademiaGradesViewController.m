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
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForStyle:UITableViewCellStyleValue1 textLabelTextStyle:UIFontTextStyleSubheadline detailTextLabelTextStyle:UIFontTextStyleHeadline]);
    };
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

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (self.semesters && self.semesters.count == 0) {
        return nil;
    }
    SemesterGrades* semester = self.semesters[section];
    return semester.semesterName;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.semesters && self.semesters.count == 0) {
        return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"GradesNoContent", @"IsAcademiaPlugin", nil)];
    }
    
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"SemesterGradeCell"];
    SemesterGrades* semester = self.semesters[indexPath.section];
    NSString* courseName = semester.sortedGradesKeys[indexPath.row];
    
    PCTableViewCellAdditions *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleHeadline];
        cell.detailTextLabel.textColor = [UIColor colorWithRed:0.478431 green:0.564706 blue:1.000000 alpha:1.0];
    }
    cell.textLabel.text = courseName;
    cell.detailTextLabel.text = semester.grades[courseName];
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.semesters && self.semesters.count == 0) {
        return 1; //cell says no content
    }
    
    SemesterGrades* semester = self.semesters[section];
    return semester.grades.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.semesters.count;
}

@end
