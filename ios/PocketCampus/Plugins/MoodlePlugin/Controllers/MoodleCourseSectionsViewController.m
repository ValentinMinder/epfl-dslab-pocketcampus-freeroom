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


#import "MoodleCourseSectionsViewController.h"

#import "MoodleController.h"

#import "PCTableViewSectionHeader.h"

#import "MoodleResourceViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "PCTableViewCellAdditions.h"

#import "PCCenterMessageCell.h"

#import "PluginSplitViewController.h"

#import "MoodleModelAdditions.h"


static const NSTimeInterval kRefreshValiditySeconds = 604800.0; //1 week

static const UISearchBarStyle kSearchBarDefaultStyle = UISearchBarStyleDefault;
static const UISearchBarStyle kSearchBarActiveStyle = UISearchBarStyleMinimal;

@interface MoodleCourseSectionsViewController ()<UISearchDisplayDelegate>

@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;
@property (nonatomic, strong) UISearchBar* searchBar;
@property (nonatomic, strong) UISearchDisplayController* searchController;
@property (nonatomic, strong) NSOperationQueue* searchQueue;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSRegularExpression* currentSearchRegex;

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSArray* sections;
@property (nonatomic, strong) NSArray* filteredSections; //for search
@property (nonatomic, strong) NSDictionary* cellForMoodleResource;
@property (nonatomic) int currentWeek;
@property (nonatomic, strong) MoodleCourse* course;
@property (nonatomic, strong) MoodleResource* selectedResource;

@end

@implementation MoodleCourseSectionsViewController

#pragma mark - Init

- (id)initWithCourse:(MoodleCourse*)course;
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/moodle/course";
        self.course = course;
        self.title = self.course.iTitle;
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.sections = [self.moodleService getFromCacheSectionsListReplyForCourse:self.course].iSections;
        self.searchQueue = [NSOperationQueue new];
        self.searchQueue.maxConcurrentOperationCount = 1;
        [self fillCellsFromSections];
        //[self.moodleService saveSession:[[MoodleSession alloc] initWithMoodleCookie:@"sdfgjskjdfhgjshdfg"]]; //TEST ONLY
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    
    RowHeightBlock rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle]*1.2);
    };
    tableViewAdditions.rowHeightBlock = rowHeightBlock;
    __weak __typeof(self) weakSelf = self;
    tableViewAdditions.contentSizeCategoryDidChangeBlock = ^(PCTableViewAdditions* tableView) {
        //need to do it manually because UISearchDisplayController does not support using a custom table view (PCTableViewAdditions in this case)
        weakSelf.searchDisplayController.searchResultsTableView.rowHeight = tableView.rowHeightBlock(tableView);
        [weakSelf fillCellsFromSections];
        [weakSelf.searchDisplayController.searchResultsTableView reloadData];
    };

    self.tableView.allowsMultipleSelection = NO;
    
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 0.0)];
    [self.searchBar sizeToFit];
    //self.searchBar.delegate = self;
    self.searchBar.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchNoun", @"PocketCampus", nil);
    self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    
    self.tableView.tableHeaderView = self.searchBar;
    
    self.searchController = [[UISearchDisplayController alloc] initWithSearchBar:self.searchBar contentsController:self];
    self.searchController.searchResultsDelegate = self;
    self.searchController.searchResultsDataSource = self;
    self.searchController.delegate = self;
    self.searchController.searchResultsTableView.rowHeight = rowHeightBlock(tableViewAdditions);
    self.searchController.searchResultsTableView.allowsMultipleSelection = NO;
    
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"moodle" dataName:[NSString stringWithFormat:@"courseSectionsList-%d", self.course.iId]]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    
    [self showToggleButtonIfPossible];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(favoriteMoodleResourcesUpdated:) name:kMoodleFavoritesMoodleResourcesUpdatedNotification object:self.moodleService];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    if (!self.sections || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - Notifications listening

- (void)favoriteMoodleResourcesUpdated:(NSNotification*)notif {
    MoodleResource* resource = notif.userInfo[kMoodleFavoriteStatusMoodleResourceUpdatedUserInfoKey];
    if (!resource) {
        return;
    }
    PCTableViewCellAdditions* cell = self.cellForMoodleResource[resource];
    cell.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleResource:resource];
}

#pragma mark - Refresh

- (void)refresh {
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourse", @"MoodlePlugin", nil)];
    [self startGetCourseSectionsRequest];
}

- (void)startGetCourseSectionsRequest {
    VoidBlock successBlock = ^{
        [self.moodleService getCourseSections:[self.moodleService createMoodleRequestWithCourseId:self.course.iId] withDelegate:self];
    };
    if ([self.moodleService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MoodleController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self.lgRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - Utils and toggle week button

- (void)computeCurrentWeek {
    if(!self.sections) {
        return;
    }
    self.currentWeek = -1; //-1 means outside semester time, all weeks will be displayed and toggle button hidden
    for (NSInteger i = 0; i < self.sections.count; i++) {
        MoodleSection* section = self.sections[i];
        if(section.iResources.count != 0 && section.iCurrent) {
            self.currentWeek = i;
            break;
        }
    }
}

- (void)showToggleButtonIfPossible {
    int visibleCount = 0;
    for (int i = 1; i < self.sections.count; i++) {
        MoodleSection* secObj = self.sections[i];
        visibleCount += secObj.iResources.count;
    }
    if(visibleCount > 0) {
        [self computeCurrentWeek];
        [self showToggleButton];
    }
}

- (void)showToggleButton {
    UIBarButtonItem *anotherButton = nil;
    if (self.currentWeek > 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleAllWeeks", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    } else if (self.currentWeek == 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleCurrentWeek", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    }
    [self.navigationItem setRightBarButtonItem:anotherButton animated:NO];
}

- (void)toggleShowAll:(id)sender {
    [self trackAction:@"AllCurrentWeekTogglePressed"];
    if (self.currentWeek > 0) {
        self.currentWeek = 0;
    } else {
        [self computeCurrentWeek];
    }
    [self showToggleButton];
    NSIndexPath* selectedIndexPath = nil;
    if (self.selectedResource && self.cellForMoodleResource[self.selectedResource]) { //second condition should always be true if first is true
        selectedIndexPath = [self.tableView indexPathForCell:self.cellForMoodleResource[self.selectedResource]];;
    }
    [self.tableView reloadData];
    if (selectedIndexPath) {
        [self.tableView scrollToRowAtIndexPath:selectedIndexPath atScrollPosition:UITableViewScrollPositionMiddle animated:NO];
    }
}

- (void)showMasterViewController {
    [(PluginSplitViewController*)self.splitViewController setMasterViewControllerHidden:NO animated:YES];
}

- (void)fillCellsFromSections {
    if (!self.sections) {
        return;
    }
    
    NSMutableDictionary* cellsTemp = [NSMutableDictionary dictionaryWithCapacity:self.sections.count*5]; //just estimation for pre-memory allocation
    
    for (MoodleSection* section in self.sections) {
        for (MoodleResource* resource in section.iResources) {
            
            PCTableViewCellAdditions* cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
            
            cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
            cell.textLabel.adjustsFontSizeToFitWidth = YES;
            cell.textLabel.minimumScaleFactor = 0.9;
            cell.textLabel.text = resource.iName;
            
            cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
            cell.detailTextLabel.adjustsFontSizeToFitWidth = YES;
            cell.detailTextLabel.minimumScaleFactor = 0.9;
            cell.detailTextLabel.text = resource.filename;
            
            cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
            
            cell.downloadedIndicationVisible = [self.moodleService isMoodleResourceDownloaded:resource];
            cell.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleResource:resource];
            cell.durablySelected = [self.selectedResource isEqualToMoodleResource:resource];
            
            MoodleCourseSectionsViewController* weakSelf __weak = self;
            PCTableViewCellAdditions* cellWeak __weak = cell;
            
            [self.moodleService removeMoodleResourceObserver:self forResource:resource];
            [self.moodleService addMoodleResourceObserver:self forResource:resource eventBlock:^(MoodleResourceEvent event) {
                if (event == MoodleResourceEventDeleted) {
                    cellWeak.durablySelected = NO;
                    cellWeak.downloadedIndicationVisible  = NO;
                    if (weakSelf.splitViewController && [weakSelf.selectedResource isEqual:resource]) { //iPad //resource deleted => hide ResourceViewController
                        [weakSelf.tableView deselectRowAtIndexPath:[weakSelf.tableView indexPathForSelectedRow] animated:YES];
                        [weakSelf.searchController.searchResultsTableView deselectRowAtIndexPath:[weakSelf.searchController.searchResultsTableView indexPathForSelectedRow] animated:YES];
                        weakSelf.selectedResource = nil;
                        MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                        weakSelf.splitViewController.viewControllers = @[weakSelf.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:splashViewController]];
                        [NSTimer scheduledTimerWithTimeInterval:0.2 target:weakSelf selector:@selector(showMasterViewController) userInfo:nil repeats:NO];
                    }
                } else if (event == MoodleResourceEventDownloaded) {
                    cellWeak.downloadedIndicationVisible = YES;
                } else {
                    //not supported
                }
                [cell setNeedsLayout];
            }];

            cellsTemp[(id<NSCopying>)resource] = cell; //NSCopying is implemented in Comparison category
            
        }
    }
    
    self.cellForMoodleResource = cellsTemp;

}

- (NSArray*)filteredSectionsFromPattern:(NSString*)pattern {
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"SELF.iName contains[cd] %@ OR SELF.filename contains[cd] %@", pattern, pattern];
    NSMutableArray* filteredSections = [NSMutableArray arrayWithCapacity:self.sections.count];
    for (MoodleSection* moodleSection in self.sections) {
        MoodleSection* moodleSectionCopy = [moodleSection copy]; //conforms to NSCopying in Additions category
        moodleSectionCopy.iResources = [moodleSection.iResources filteredArrayUsingPredicate:predicate];
        [filteredSections addObject:moodleSectionCopy];
    }
    return filteredSections;
}

#pragma mark - UISearchBarDisplayDelegate

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    [self.typingTimer invalidate];
    [self.searchQueue cancelAllOperations];
    if (searchString.length == 0) {
        self.filteredSections = nil;
        self.currentSearchRegex = nil;
        return YES;
    } else {
        //perform search in background
        typeof(self) weakSelf __weak = self;
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:self.filteredSections.count ? 0.2 : 0.0 block:^{ //interval: so that first search is not delayed (would display "No results" otherwise)
            [self.searchQueue addOperationWithBlock:^{
                NSArray* filteredSections = [self filteredSectionsFromPattern:searchString];
                NSRegularExpression* currentSearchRegex = [NSRegularExpression regularExpressionWithPattern:searchString options:NSRegularExpressionCaseInsensitive error:NULL];
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    weakSelf.filteredSections = filteredSections;
                    weakSelf.currentSearchRegex = currentSearchRegex;
                    [weakSelf.searchController.searchResultsTableView reloadData];
                }];
            }];
        } repeats:NO];
        self.typingTimer.tolerance = 0.05;
        return NO;
    }
}

- (void)searchDisplayController:(UISearchDisplayController *)controller willHideSearchResultsTableView:(UITableView *)tableView {
    [self.typingTimer invalidate];
    [self.searchQueue cancelAllOperations];
    [self fillCellsFromSections];
    [self.tableView reloadData];
}

- (void)searchDisplayControllerWillBeginSearch:(UISearchDisplayController *)controller {
    [self trackAction:PCGAITrackerActionSearch];
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarActiveStyle;
    }
    [self.moodleService cancelOperationsForDelegate:self];
    [self.lgRefreshControl endRefreshing];
}

- (void)searchDisplayControllerWillEndSearch:(UISearchDisplayController *)controller {
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    }
}

#pragma mark - MoodleServiceDelegate

- (void)getCourseSections:(MoodleRequest *)aMoodleRequest didReturn:(SectionsListReply *)sectionsListReply {
    switch (sectionsListReply.iStatus) {
        case 200:
            self.sections = sectionsListReply.iSections;
            [self.moodleService saveToCacheSectionsListReply:sectionsListReply forCourse:self.course];
            [self showToggleButtonIfPossible];
            [self fillCellsFromSections];
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshing];
            [self.lgRefreshControl markRefreshSuccessful];
            break;
        case 407:
            [self.moodleService deleteSession];
            [self startGetCourseSectionsRequest];
            break;
        case 405:
            [self error];
            break;
        case 404:
        {
            [self.lgRefreshControl endRefreshing];
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
        }
        default:
            [self getCourseSectionsFailed:aMoodleRequest];
            break;
    }
}

- (void)getCourseSectionsFailed:(MoodleRequest *)aMoodleRequest {
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
    MoodleSection* section;
    if (tableView == self.tableView) {
        if (!self.sections.count) {
            return;
        }
        section = self.sections[indexPath.section];
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.filteredSections.count) {
            return;
        }
        section = self.filteredSections[indexPath.section];
        [self.searchController.searchBar resignFirstResponder];
    }
    
    MoodleResource* resource = section.iResources[indexPath.row];
    
    if (self.splitViewController && [resource isEqualToMoodleResource:self.selectedResource]) {
        return;
    }
    MoodleResourceViewController* detailViewController = [[MoodleResourceViewController alloc] initWithMoodleResource:resource];

    if (self.splitViewController) { // iPad
        if (self.selectedResource) {
            PCTableViewCellAdditions* prevCell = self.cellForMoodleResource[self.selectedResource];
            prevCell.durablySelected = NO;
        }
        self.selectedResource = resource;
        
        PCTableViewCellAdditions* newCell = self.cellForMoodleResource[resource];
        newCell.durablySelected = YES;
        
        PCNavigationController* detailNavController = [[PCNavigationController alloc] initWithRootViewController:detailViewController]; //to have nav bar
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], detailNavController];
    } else { // iPhone
        [self.navigationController pushViewController:detailViewController animated:YES];
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    PCTableViewCellAdditions* cell = (PCTableViewCellAdditions*)[tableView cellForRowAtIndexPath:indexPath];
    if (cell.isDownloadedIndicationVisible) {
        return UITableViewCellEditingStyleDelete;
    }
    return UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tableView willBeginEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    //nothing to do, just prevent table view to enter editing mode (would show delete control in other cells which we don't want)
    //see http://stackoverflow.com/questions/6437916/how-to-avoid-swipe-to-delete-calling-setediting-at-the-uitableviewcell
}

- (void)tableView:(UITableView *)tableView didEndEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    //see tableView:willBeginEditingRowAtIndexPath:
    [[tableView cellForRowAtIndexPath:indexPath] setEditing:NO];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (tableView == self.tableView) {
        if (!self.sections.count) {
            return 0.0;
        }
        if (![self showSection:section inTableView:tableView]) {
            return 0.0;
        }
        MoodleSection* secObj = self.sections[section];
        if (secObj.iResources.count == 0) {
            return 0.0;
        }
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.filteredSections.count) {
            return 0.0;
        }
        if (![self showSection:section inTableView:tableView]) {
            return 0.0;
        }
        MoodleSection* secObj = self.filteredSections[section];
        if (secObj.iResources.count == 0) {
            return 0.0;
        }
    } else {
        //should not happen
    }
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    MoodleSection* moodleSection = nil;
    if (tableView == self.tableView) {
        if (!self.sections.count || ![self showSection:section inTableView:tableView]) {
            return nil;
        }
        moodleSection = self.sections[section];
        if (moodleSection.iResources.count == 0) {
            return nil;
        }
    }
    if (tableView == self.searchController.searchResultsTableView) {
        if (!self.filteredSections.count || ![self showSection:section inTableView:tableView]) {
            return nil;
        }
        moodleSection = self.filteredSections[section];
        if (moodleSection.iResources.count == 0) {
            return nil;
        }
    }
    /*NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
     [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
     [dateFormatter setLocale:[NSLocale systemLocale]];
     [dateFormatter setDateFormat:@"dd/MM"];
     //NSLog(@"%lld", secObj.iStartDate);
     NSString* startDate = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:secObj.iStartDate]];*/
    /* startDate and endDate are not filled by server yet */
    
    NSString* title = nil;
    
    if (moodleSection.iText) {
        title = moodleSection.iText;
    } else {
       title = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), (int)section];
    }
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
}

#pragma mark - UITableViewDataSource

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MoodleSection* section = tableView == self.tableView ? self.sections[indexPath.section] : self.filteredSections[indexPath.section];
        MoodleResource* resource = section.iResources[indexPath.row];
        [self trackAction:PCGAITrackerActionDelete];
        if ([self.moodleService deleteDownloadedMoodleResource:resource]) {
            [tableView setEditing:NO animated:YES];
        } else {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            return;
        }
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (tableView == self.tableView && self.sections && self.sections.count == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"MoodleEmptyCourse", @"MoodlePlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    if (tableView == self.searchController.searchResultsTableView) {
        //UISearchDisplayController takes care itself to show a "No result" message when
        //filteredSections is empty
    }
    
    MoodleSection* section = tableView == self.tableView ? self.sections[indexPath.section] : self.filteredSections[indexPath.section];
    MoodleResource* resource = section.iResources[indexPath.row];
    PCTableViewCellAdditions* cell = self.cellForMoodleResource[resource];
    
    if (tableView == self.tableView) {
        cell.textLabelHighlightedRegex = nil;
        cell.detailTextLabelHighlightedRegex = nil;
    } else if (tableView == self.searchController.searchResultsTableView) {
        //Results text highlighting
        cell.textLabelHighlightedRegex = self.currentSearchRegex;
        cell.detailTextLabelHighlightedRegex = self.currentSearchRegex;
    }
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == self.tableView) {
        if (!self.sections) {
            return 0;
        }
        if (self.sections.count == 0) {
            return 2; //first empty cell, second cell says no content
        }
        if(![self showSection:section inTableView:tableView]) {
            return 0;
        }
        MoodleSection* secObj = self.sections[section];
        return secObj.iResources.count;
    }
    if (tableView == self.searchController.searchResultsTableView) {
        if (!self.filteredSections) {
            return 0;
        }
        if(![self showSection:section inTableView:tableView]) {
            return 0;
        }
        MoodleSection* secObj = self.filteredSections[section];
        return secObj.iResources.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (tableView == self.tableView) {
        if (!self.sections) {
            return 0;
        }
        if (self.sections.count == 0) {
            return 1; //empty course message
        }
        return self.sections.count;
    }
    if (tableView == self.searchController.searchResultsTableView) {
        return self.filteredSections.count;
    }
    return 0;
}

#pragma mark - showSections

- (BOOL)showSection:(NSInteger)section inTableView:(UITableView*)tableView {
    if (self.currentWeek <= 0 || tableView == self.searchController.searchResultsTableView) {
        return YES;
    }
    return (self.currentWeek == section);
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.moodleService removeMoodleResourceObserver:self];
    [self.moodleService cancelOperationsForDelegate:self];
    [self.searchQueue cancelAllOperations];
    [self.typingTimer invalidate];
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}

@end
