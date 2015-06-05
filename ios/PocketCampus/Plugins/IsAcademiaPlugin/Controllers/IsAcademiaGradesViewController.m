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

@interface IsAcademiaGradesViewController ()<IsAcademiaServiceDelegate, UISearchBarDelegate>

@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) IsAcademiaService* isaService;
@property (nonatomic, strong) NSArray* semesters; //array of SemesterGrades
@property (nonatomic, strong) NSArray* filteredSemesters; //array of SemestersGrades, filters for search
@property (nonatomic, readonly) NSArray* sections;

@property (nonatomic, strong) UISearchBar* searchBar;

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
    
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 44.0)];
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchByCourseOrGrade", @"IsAcademiaPlugin", nil);
    self.searchBar.searchBarStyle = UISearchBarStyleMinimal;
    self.searchBar.delegate = self;

    self.tableView.tableHeaderView = self.searchBar;
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.semesters || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.searchBar resignFirstResponder];
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.isaService cancelOperationsForDelegate:self];
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

- (void)fillSearchFilteredSemestersForSearchString:(NSString*)searchString {
    if (searchString.length == 0) {
        self.filteredSemesters = nil;
        return;
    }
    
    static NSCharacterSet* decimalsSet = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSMutableCharacterSet* mDecimalsSet = [NSMutableCharacterSet decimalDigitCharacterSet];
        [mDecimalsSet addCharactersInString:@"."];
        decimalsSet = mDecimalsSet;
    });
    NSCharacterSet* searchStringSet = [NSCharacterSet characterSetWithCharactersInString:searchString];
    BOOL gradeSearchMode = [decimalsSet isSupersetOfSet:searchStringSet];
    float searchGradeFloat = [searchString floatValue];
    
    
    NSMutableArray* filteredSemesters = [NSMutableArray arrayWithCapacity:self.semesters.count];
    float searchGrade = [searchString floatValue];
    if (searchGrade == 0.0 && [searchString rangeOfString:@"0"].location == NSNotFound) {
        // means floatValue returned 0.0 because did not find a valid numerical value
        searchGrade = -1.0;
    }
    for (SemesterGrades* semester in self.semesters) {
        SemesterGrades* semesterCopy = [semester copy];
        NSMutableDictionary* filteredGrades = [NSMutableDictionary dictionary];
        for (NSString* courseName in semester.grades) {
            NSString* grade = semesterCopy.grades[courseName];
            BOOL includeCourse = NO;
            if (gradeSearchMode && [decimalsSet isSupersetOfSet:[NSCharacterSet characterSetWithCharactersInString:grade]]) {
                // user is typing a grade and the grade variable is a numerical grade => apply numbers comparison
                includeCourse = (grade.length > 0 && searchGradeFloat == [grade floatValue]);
            } else {
                includeCourse = ([courseName rangeOfString:searchString options:NSCaseInsensitiveSearch|NSDiacriticInsensitiveSearch|NSWidthInsensitiveSearch].location != NSNotFound
                                 || [grade rangeOfString:searchString options:NSCaseInsensitiveSearch|NSDiacriticInsensitiveSearch|NSWidthInsensitiveSearch].location != NSNotFound);
            }
            if (includeCourse) {
                if (!grade) {
                    grade = @"";
                }
                filteredGrades[courseName] = grade;
            }
        }
        
        semesterCopy.grades = filteredGrades;
        if (semesterCopy.grades.count > 0) {
            [filteredSemesters addObject:semesterCopy];
        }
    }
    self.filteredSemesters = filteredSemesters;
}

- (NSArray*)sections {
    return self.searchBar.text.length > 0 ? self.filteredSemesters : self.semesters;
}

#pragma mark - IsaServiceDelegate

- (void)getGradesDidReturn:(IsaGradesResponse *)gradesResponse {
/*#warning REMOVE
    for (SemesterGrades* semester in gradesResponse.semesters) {
        for (NSString* course in [semester.grades copy]) {
            NSInteger index = [semester.grades.allKeys indexOfObject:course];
            if (index % 4 == 0) {
                semester.grades[course] = @"Réussi";
            } else if (index % 2 == 0) {
                semester.grades[course] = [NSString stringWithFormat:@"%d", index];
            } else {
                semester.grades[course] = [NSString stringWithFormat:@"%d.5", index];
            }
            
        }
    }*/
    switch (gradesResponse.statusCode) {
        case IsaStatusCode_OK:
        {
            self.semesters = gradesResponse.semesters;
            [self fillSearchFilteredSemestersForSearchString:self.searchBar.text];
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

#pragma mark - UISearchBarDelegate

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    [self fillSearchFilteredSemestersForSearchString:searchText];
    [self.tableView reloadData];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self.searchBar resignFirstResponder];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section {
    if (!self.sections) {
        return;
    }
    SemesterGrades* semester = self.sections[section];
    UITableViewHeaderFooterView* header = (UITableViewHeaderFooterView*)view;
    header.textLabel.font = [UIFont fontWithName:@"HelveticaNeue-Medium" size:19.0];
    header.textLabel.text = semester.semesterName;
    header.textLabel.textColor = [UIColor colorWithWhite:0.25 alpha:1.0];
}

- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section {
    if (!self.sections) {
        return;
    }
    UITableViewHeaderFooterView* footer = (UITableViewHeaderFooterView*)view;
    if (section == 0 && self.sections && self.sections.count == 0) {
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
    if (self.sections.count == 0) {
        return nil;
    }
    SemesterGrades* semester = self.sections[section];
    return semester.semesterName;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (!self.sections) {
        return nil;
    }
    if (section == 0 && self.sections && self.sections.count == 0) {
        if (self.searchBar.text.length > 0) {
            return NSLocalizedStringFromTable(@"NoResult", @"IsAcademiaPlugin", nil);
        } else {
            return NSLocalizedStringFromTable(@"GradesNoContent", @"IsAcademiaPlugin", nil);
        }
    }
    SemesterGrades* semester = self.sections[section];
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
    SemesterGrades* semester = self.sections[indexPath.section];
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
    if (self.sections && self.sections.count == 0) {
        return 0;
    }
    SemesterGrades* semester = self.sections[section];
    return semester.grades.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.sections && self.sections.count == 0) {
        return 1;
    }
    return self.sections.count;
}

- (void)dealloc
{
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.isaService cancelOperationsForDelegate:self];
}

@end
