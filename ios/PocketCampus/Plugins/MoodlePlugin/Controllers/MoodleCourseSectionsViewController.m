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

#import "MoodleService.h"

#import "MoodleController.h"

#import "PCTableViewSectionHeader.h"

#import "MoodleFileViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "PCTableViewCellAdditions.h"

#import "PCCenterMessageCell.h"

#import "PluginSplitViewController.h"

#import "MoodleModelAdditions.h"

#import "MoodleSettingsViewController.h"

#import "MoodleUrlViewController.h"

#import "MoodleResourceCell.h"

#import "MoodleFolderViewController.h"

static const NSTimeInterval kRefreshValiditySeconds = 86400; //1 day

static const UISearchBarStyle kSearchBarDefaultStyle = UISearchBarStyleDefault;
static const UISearchBarStyle kSearchBarActiveStyle = UISearchBarStyleMinimal;

static const NSInteger kSegmentIndexAll = 0;
static const NSInteger kSegmentIndexCurrentWeek = 1;
static const NSInteger kSegmentIndexFavorites = 2;

@interface MoodleCourseSectionsViewController ()<UISearchDisplayDelegate, MoodleServiceDelegate>

@property (nonatomic) BOOL firstViewWillAppearDone;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) UISearchBar* searchBar;
@property (nonatomic, strong) UISearchDisplayController* searchController;
@property (nonatomic, strong) NSOperationQueue* searchQueue;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSRegularExpression* currentSearchRegex;
@property (nonatomic, strong) UIPopoverController* settingsPopover;
@property (nonatomic, strong) UISegmentedControl* segmentedControl;
@property (nonatomic, strong) NSLayoutConstraint* segmentedControlWidthConstraint;
@property (nonatomic, strong) NSLayoutConstraint* segmentedControlHeightConstraint;
@property (nonatomic) NSInteger prevSelectedSegmentIndex;

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleCourseSectionsResponse2* sectionsResponse;
@property (nonatomic, strong) NSArray* sections;
@property (nonatomic, strong) NSArray* searchFilteredSections; //for search
@property (nonatomic, strong) NSMapTable* cellForMoodleResource; //Key: MoodleResource2, value: cell
@property (nonatomic) int currentWeek;
@property (nonatomic, strong) MoodleCourse2* course;
@property (nonatomic, strong) MoodleResource2* selectedResource;

@end

@implementation MoodleCourseSectionsViewController

#pragma mark - Init

- (id)initWithCourse:(MoodleCourse2*)course
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/moodle/course";
        self.course = course;
        self.title = self.course.name;
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.sectionsResponse = [self.moodleService getFromCacheSectionsWithRequest:[self newCourseSectionsRequest]];
        self.sections = self.sectionsResponse.sections;
        [self computeCurrentWeek];
        self.searchQueue = [NSOperationQueue new];
        self.searchQueue.maxConcurrentOperationCount = 1;
        [self fillCellForMoodleResource];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIBarButtonItem* settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsBarButton"] style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButtonPressed)];
    settingsButton.accessibilityLabel = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    
    self.navigationItem.rightBarButtonItem = settingsButton;
    
    NSArray* segmentedControlItems = @[NSLocalizedStringFromTable(@"All", @"PocketCampus", nil), NSLocalizedStringFromTable(@"MoodleCurrentWeek", @"MoodlePlugin", nil), NSLocalizedStringFromTable(@"Favorites", @"PocketCampus", nil)];
    self.segmentedControl = [[UISegmentedControl alloc] initWithItems:segmentedControlItems];
    self.segmentedControl.tintColor = [UIColor colorWithWhite:0.5 alpha:1.0];
    self.segmentedControl.selectedSegmentIndex = kSegmentIndexAll;
    self.prevSelectedSegmentIndex = self.segmentedControl.selectedSegmentIndex;
    [self.segmentedControl addTarget:self action:@selector(segmentedControlValueChanged) forControlEvents:UIControlEventValueChanged];
    [self showCurrentWeekSegmentConditionally];
    UIBarButtonItem* segmentedControlBarItem = [[UIBarButtonItem alloc] initWithCustomView:self.segmentedControl];
    
    [self.segmentedControl addObserver:self forKeyPath:NSStringFromSelector(@selector(frame)) options:0 context:NULL];
    
    UIBarButtonItem* flexibleSpaceLeft = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* flexibleSpaceRight = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    self.toolbarItems = @[flexibleSpaceLeft, segmentedControlBarItem, flexibleSpaceRight];
    
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
        [weakSelf fillCellForMoodleResource];
        [weakSelf.searchDisplayController.searchResultsTableView reloadData];
    };

    self.tableView.allowsMultipleSelection = NO;
    
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 1.0)];
    [self.searchBar sizeToFit];
    self.searchBar.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchCourse", @"MoodlePlugin", nil);
    self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    
    self.tableView.tableHeaderView = self.searchBar;
    
    self.searchController = [[UISearchDisplayController alloc] initWithSearchBar:self.searchBar contentsController:self];
    self.searchController.searchResultsDelegate = self;
    self.searchController.searchResultsDataSource = self;
    self.searchController.delegate = self;
    self.searchController.searchResultsTableView.rowHeight = rowHeightBlock(tableViewAdditions);
    self.searchController.searchResultsTableView.allowsMultipleSelection = NO;
    
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"moodle" dataName:[NSString stringWithFormat:@"courseSectionsList-%d", self.course.courseId]]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(favoriteMoodleResourcesUpdated:) name:kMoodleFavoritesMoodleItemsUpdatedNotification object:self.moodleService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillResignActive) name:UIApplicationWillResignActiveNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    if (!self.searchController.isActive) {
        [self.navigationController setToolbarHidden:NO animated:YES];
    }
    if (!self.sections || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
    if (!self.firstViewWillAppearDone) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self restoreUIState];
        });
        self.firstViewWillAppearDone = YES;
    }
//#warning REMOVE
    //[NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(test) userInfo:nil repeats:YES];
    
}

// STRESS TEST
// Also need to set animated:NO for iPhone in didSelectRowAtIndexPath
/*
static int i = 0;
- (void)test {
    NSUInteger randSection = arc4random() % (self.sections.count - 1);
    MoodleSection* section = self.sections[randSection];
    NSUInteger randRow = 0;
    if (section.iResources.count == 0) {
        return;
    }
    if (![PCUtils isIdiomPad] && (i % 2 == 1)) {
        [self.navigationController popViewControllerAnimated:NO];
    } else {
        if (section.iResources.count > 1) {
            randRow = arc4random() % (section.iResources.count - 1);
        }
        [self tableView:self.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:randRow inSection:randSection]];
    }
    i++;
}*/

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self saveUIState];
    [self.navigationController setToolbarHidden:YES animated:YES];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - Notifications listening

- (void)favoriteMoodleResourcesUpdated:(NSNotification*)notif {
    id item = notif.userInfo[kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey];
    if (!item) {
        return;
    }
    
    for (MoodleResource2* resource in self.cellForMoodleResource) {
        if ([item isEqual:resource.file] || [item isEqual:resource.url]) {
            MoodleResourceCell* cell = [self.cellForMoodleResource objectForKey:resource];
            cell.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleItem:resource.file] || [self.moodleService isFavoriteMoodleItem:resource.url];
            break;
        }
    }

    if (self.splitViewController && self.segmentedControl.selectedSegmentIndex == kSegmentIndexFavorites) {
        // Only on iPad, because fav list is visible when documents are open. Need to update live.
        // On iPhone, want to let the opportunity to go back to doc to re-add to fav is wanted (otherwise lose pointer)
        [self fillSectionsForSelectedSegment];
        [self.tableView reloadData];
    }
}

- (void)appWillResignActive {
    [self saveUIState];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.segmentedControl && [keyPath isEqualToString:NSStringFromSelector(@selector(frame))]) {
        if (!self.segmentedControl.superview) {
            return;
        }
        CGFloat width = self.segmentedControl.superview.frame.size.width-18.0;
        if (width > 370.0) {
            width = 370.0;
        }
        CGFloat height = self.segmentedControl.superview.frame.size.height-16.0;
        if (height < 22.0) {
            height = 22.0;
        }
        self.segmentedControl.bounds = CGRectMake(0, 0, width, height);
    }
}

#pragma mark - Refresh

- (void)refresh {
    CLSLog(@"-> Refresh course sections");
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourse", @"MoodlePlugin", nil)];
    [self startGetSectionsRequest];
}

- (void)startGetSectionsRequest {
    [self.moodleService getSectionsWithRequest:[self newCourseSectionsRequest] delegate:self];
}

- (MoodleCourseSectionsRequest2*)newCourseSectionsRequest {
    return [[MoodleCourseSectionsRequest2 alloc] initWithLanguage:[PCUtils userLanguageCode] courseId:self.course.courseId];
}

#pragma mark - Utils and data

- (void)computeCurrentWeek {
    if(!self.sectionsResponse.sections) {
        return;
    }
    self.currentWeek = -1; //-1 means outside semester time, all weeks will be displayed and toggle button hidden
    
    for (NSInteger i = 0; i < self.sectionsResponse.sections.count; i++) {
        MoodleCourseSection2* section = self.sectionsResponse.sections[i];
        if(section.resources.count > 0 && section.isCurrent) {
            self.currentWeek = (int)i;
            break;
        }
    }
}

- (void)showCurrentWeekSegmentConditionally {
    if (self.currentWeek > -1) {
        [self.segmentedControl setWidth:0.0 forSegmentAtIndex:kSegmentIndexCurrentWeek]; // 0.0 means auto => show (see doc)
    } else {
        [self.segmentedControl setWidth:0.1 forSegmentAtIndex:kSegmentIndexCurrentWeek]; // will be interepreted as 0 width (0.0 is auto)
    }
}

- (void)showMasterViewController {
    [(PluginSplitViewController*)self.splitViewController setMasterViewControllerHidden:NO animated:YES];
}

- (void)fillSectionsForSelectedSegment {
    if (!self.sectionsResponse.sections) {
        self.sections = nil;
        return;
    }
    switch (self.segmentedControl.selectedSegmentIndex) {
        case kSegmentIndexAll:
            self.sections = self.sectionsResponse.sections;
            break;
        case kSegmentIndexCurrentWeek:
        {
            NSMutableArray* filteredSections = [NSMutableArray arrayWithCapacity:1]; //assuming only 1 current section
            for (MoodleCourseSection2* section in self.sectionsResponse.sections) {
                if (section.isCurrent) {
                    [filteredSections addObject:section];
                }
            }
            self.sections = filteredSections;
            break;
        }
        case kSegmentIndexFavorites:
        {
            NSMutableArray* filteredSections = [NSMutableArray arrayWithCapacity:self.sectionsResponse.sections.count];
            for (MoodleCourseSection2* section in self.sectionsResponse.sections) {
                if (section.resources.count == 0) {
                    continue;
                }
                NSMutableArray* filteredResources = [NSMutableArray arrayWithCapacity:section.resources.count];
                for (MoodleResource2* resource in section.resources) {
                    if (resource.file || resource.url) {
                        if ([self.moodleService isFavoriteMoodleItem:resource.item]) {
                            [filteredResources addObject:resource]; //adding
                        }
                    } else if (resource.folder) {
                        // folders cannot be faved (doc of addFavoriteMoodleItem)
                        // let's see if nested files are.
                        for (MoodleFile2* file in resource.folder.files) {
                            if ([self.moodleService isFavoriteMoodleItem:file]) {
                                // We HAVE TO HAVE MoodleResource2 in filteredResources
                                // so we have to create file resource containers for each
                                // file nested in the folder. Displaying favs list will show
                                // files flattened, this is wanted behavior.
                                MoodleResource2* tmpResource = [[MoodleResource2 alloc] initWithFile:file folder:nil url:nil];
                                [filteredResources addObject:tmpResource];
                            }
                        }
                    }
                }
                if (filteredResources.count == 0) {
                    continue;
                }
                MoodleCourseSection2* sectionCopy = [section copy];
                sectionCopy.resources = filteredResources;
                [filteredSections addObject:sectionCopy];
            }
            self.sections = filteredSections;
            break;
        }
        default:
            self.sections = self.sectionsResponse.sections;
            break;
    }
}

- (void)fillCellForMoodleResource {
    if (!self.sectionsResponse.sections) {
        return;
    }
    
    NSMapTable* cellsTemp = [NSMapTable mapTableWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory];
    for (MoodleCourseSection2* section in self.sectionsResponse.sections) {
        for (MoodleResource2* resource in section.resources) {
            MoodleResourceCell* cell = [self newCellForMoodleResource:resource];
            [cellsTemp setObject:cell forKey:resource];
        }
    }
    self.cellForMoodleResource = cellsTemp;
}

- (MoodleResourceCell*)newCellForMoodleResource:(MoodleResource2*)resource {
    MoodleResourceCell* cell = [[MoodleResourceCell alloc] initWithMoodleResource:resource];
    cell.durablySelected = [resource isEqual:self.selectedResource];
    __weak typeof(cell) weakCell = cell;
    __weak typeof(self) welf = self;
    if (resource.file) {
        [self.moodleService removeMoodleFileObserver:self forFile:resource.file];
        [self.moodleService addMoodleFileObserver:self forFile:resource.file eventBlock:^(MoodleResourceEvent event) {
            if (!weakCell) {
                return;
            }
            if (event == MoodleResourceEventDeleted) {
                weakCell.durablySelected = NO;
                if (welf.splitViewController && [welf.selectedResource isEqual:resource]) { //iPad //resource deleted => hide ResourceViewController
                    [welf.tableView deselectRowAtIndexPath:[welf.tableView indexPathForSelectedRow] animated:YES];
                    [welf.searchController.searchResultsTableView deselectRowAtIndexPath:[welf.searchController.searchResultsTableView indexPathForSelectedRow] animated:YES];
                    welf.selectedResource = nil;
                    MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                    welf.splitViewController.viewControllers = @[welf.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:splashViewController]];
                    [NSTimer scheduledTimerWithTimeInterval:0.2 target:welf selector:@selector(showMasterViewController) userInfo:nil repeats:NO];
                }
            }
            [weakCell setNeedsLayout];
        }];
    }
    return cell;
}

- (NSArray*)filteredSectionsFromPattern:(NSString*)pattern {
    static NSUInteger const options = NSDiacriticInsensitiveSearch | NSCaseInsensitiveSearch;
    NSPredicate* predicate = [NSPredicate predicateWithBlock:^BOOL(MoodleResource2* resource, NSDictionary *bindings) {
        if ([resource.name rangeOfString:pattern options:options].location != NSNotFound) {
            return YES;
        }
        if (resource.file) {
            return [resource.file.filename rangeOfString:pattern options:options].location != NSNotFound;
        }
        if (resource.folder) {
            for (MoodleFile2* file in resource.folder.files) {
                if ([file.name rangeOfString:pattern options:options].location != NSNotFound) {
                    return YES;
                }
                if ([file.filename rangeOfString:pattern options:options].location != NSNotFound) {
                    return YES;
                }
            }
            return NO;
        }
        if (resource.url) {
            if ([resource.url.url rangeOfString:pattern options:options].location != NSNotFound) {
                return YES;
            }
            return NO;
        }
        return NO;
    }];
    
    NSMutableArray* filteredSections = [NSMutableArray arrayWithCapacity:self.sectionsResponse.sections.count];
    for (MoodleCourseSection2* moodleSection in self.sectionsResponse.sections) {
        MoodleCourseSection2* moodleSectionCopy = [moodleSection copy]; //conforms to NSCopying in Additions category
        moodleSectionCopy.resources = [[moodleSection.resources filteredArrayUsingPredicate:predicate] mutableCopy];
        [filteredSections addObject:moodleSectionCopy];
    }
    return filteredSections;
}

- (void)saveUIState {
    if (!self.sectionsResponse) {
        return;
    }
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"moodle"];
    [defaults setInteger:self.segmentedControl.selectedSegmentIndex forKey:[self selectedSegmentedIndexIntegerKey]];
    [defaults setObject:[(PCTableViewAdditions*)(self.tableView) saveContentOffsetForIdentifier:[self contentOffsetDictionaryKey]] forKey:[self contentOffsetDictionaryKey]];
}

- (void)restoreUIState {
    if (!self.sectionsResponse) {
        return;
    }
    @try {
        NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"moodle"];
        NSNumber* nsSelectedSegmentedIndex = [defaults objectForKey:[self selectedSegmentedIndexIntegerKey]];
        if (nsSelectedSegmentedIndex) {
            self.segmentedControl.selectedSegmentIndex = [nsSelectedSegmentedIndex integerValue];
            [self segmentedControlValueChanged];
        }
        NSDictionary* contentOffsetDictionary = [defaults objectForKey:[self contentOffsetDictionaryKey]];
        if (contentOffsetDictionary) {
            [(PCTableViewAdditions*)(self.tableView) restoreContentOffsetWithStateDictionary:contentOffsetDictionary];
        }
    }
    @catch (NSException *exception) {}
}

- (NSString*)selectedSegmentedIndexIntegerKey {
    return [NSString stringWithFormat:@"selectedSegmentIndex-moodleCourse-%d", (int)(self.course.courseId)];
}

- (NSString*)contentOffsetDictionaryKey {
    return [NSString stringWithFormat:@"contentOffset-moodleCourse-%d", (int)(self.course.courseId)];
}

#pragma mark - Button actions

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

- (void)segmentedControlValueChanged {
    switch (self.segmentedControl.selectedSegmentIndex) {
        case kSegmentIndexAll:
            [self trackAction:@"ShowAll"];
            break;
        case kSegmentIndexCurrentWeek:
            [self trackAction:@"ShowCurrentWeek"];
            break;
        case kSegmentIndexFavorites:
            [self trackAction:@"ShowFavorites"];
            break;
        default:
            break;
    }
    
    [(PCTableViewAdditions*)(self.tableView) saveContentOffsetForIdentifier:[NSString stringWithFormat:@"%ld", self.prevSelectedSegmentIndex]];
    [self fillSectionsForSelectedSegment];
    [self.tableView reloadData];
    [(PCTableViewAdditions*)(self.tableView) restoreContentOffsetForIdentifier:[NSString stringWithFormat:@"%ld", self.segmentedControl.selectedSegmentIndex]];
    self.prevSelectedSegmentIndex = self.segmentedControl.selectedSegmentIndex;
}

- (void)sectionDetailsDoneButtonTapped {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UISearchDisplayDelegate

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    [self.typingTimer invalidate];
    [self.searchQueue cancelAllOperations];
    if (searchString.length == 0) {
        self.searchFilteredSections = nil;
        self.currentSearchRegex = nil;
        return YES;
    } else {
        //perform search in background
        typeof(self) welf __weak = self;
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:self.searchFilteredSections.count ? 0.2 : 0.0 block:^{ //interval: so that first search is not delayed (would display "No results" otherwise)
            [welf.searchQueue addOperationWithBlock:^{
                if (!welf) {
                    return;
                }
                __strong __typeof(welf) strongSelf = welf;
                NSArray* filteredSections = [strongSelf filteredSectionsFromPattern:searchString]; //heavy-computation line
                if (!welf) {
                    return;
                }
                NSRegularExpression* currentSearchRegex = [NSRegularExpression regularExpressionWithPattern:searchString options:NSRegularExpressionCaseInsensitive error:NULL];
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    welf.searchFilteredSections = filteredSections;
                    welf.currentSearchRegex = currentSearchRegex;
                    [welf.searchController.searchResultsTableView reloadData];
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
    [self fillCellForMoodleResource];
    [self.tableView reloadData];
}

- (void)searchDisplayControllerWillBeginSearch:(UISearchDisplayController *)controller {
    [self trackAction:PCGAITrackerActionSearch];
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarActiveStyle;
    }
    [self.navigationController setToolbarHidden:YES animated:YES];
    [self.moodleService cancelOperationsForDelegate:self];
    [self.lgRefreshControl endRefreshing];
}

- (void)searchDisplayControllerWillEndSearch:(UISearchDisplayController *)controller {
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    }
    [self.navigationController setToolbarHidden:NO animated:YES];
}

#pragma mark - MoodleServiceDelegate

- (void)getSectionsForRequest:(MoodleCourseSectionsRequest2 *)request didReturn:(MoodleCourseSectionsResponse2 *)response {
    switch (response.statusCode) {
        case MoodleStatusCode2_OK:
            self.sectionsResponse = response;
            [self computeCurrentWeek];
            [self showCurrentWeekSegmentConditionally];
            [self fillCellForMoodleResource];
            [self fillSectionsForSelectedSegment];
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshing];
            [self.lgRefreshControl markRefreshSuccessful];
            break;
        case MoodleStatusCode2_AUTHENTICATION_ERROR:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [welf startGetSectionsRequest];
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

- (void)getSectionsFailedForRequest:(MoodleCourseSectionsRequest2 *)request {
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
    MoodleCourseSection2* section;
    if (tableView == self.tableView) {
        if (!self.sections.count) {
            return;
        }
        section = self.sections[indexPath.section];
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.searchFilteredSections.count) {
            return;
        }
        section = self.searchFilteredSections[indexPath.section];
        [self.searchController.searchBar resignFirstResponder];
    }
    
    MoodleResource2* resource = section.resources[indexPath.row];
    
    if (self.splitViewController && [resource isEqualToMoodleResource:self.selectedResource]) {
        return;
    }
    
    UIViewController* viewController = nil;
    
    if (resource.file) {
        viewController = [[MoodleFileViewController alloc] initWithMoodleFile:resource.file];
        [self trackAction:@"DownloadAndOpenFile" contentInfo:resource.file.name];
    } else if (resource.folder) {
        viewController = [[MoodleFolderViewController alloc] initWithFolder:resource.folder];
        [self trackAction:@"OpenFolder" contentInfo:resource.folder.name];
    } else if (resource.url) {
        viewController = [[MoodleUrlViewController alloc] initWithMoodleUrl:resource.url];
        [self trackAction:@"OpenLink" contentInfo:resource.url.name];
    }
    
    if (self.splitViewController && !resource.folder) { // iPad
        if (self.selectedResource) {
            MoodleResourceCell* prevCell = [self.cellForMoodleResource objectForKey:self.selectedResource];
            prevCell.durablySelected = NO;
        }
        self.selectedResource = resource;
        
        MoodleResourceCell* newCell = [self.cellForMoodleResource objectForKey:resource];
        newCell.durablySelected = YES;
        
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController]; //to have nav bar
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else { // iPhone or iPad folder
        [self.navigationController pushViewController:viewController animated:YES];
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleResourceCell* cell = (MoodleResourceCell*)[tableView cellForRowAtIndexPath:indexPath];
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
    MoodleCourseSection2* secObj = nil;
    if (tableView == self.tableView) {
        if (!self.sections.count) {
            return 0.0;
        }
        secObj = self.sections[section];
        if (secObj.resources.count == 0) {
            return 0.0;
        }
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.searchFilteredSections.count) {
            return 0.0;
        }
        secObj = self.searchFilteredSections[section];
        if (secObj.resources.count == 0) {
            return 0.0;
        }
    } else {
        //should not happen
    }
    return [PCTableViewSectionHeader preferredHeightWithInfoButton:YES]; //we want all section headers to be same height
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    MoodleCourseSection2* moodleSection = nil;
    if (tableView == self.tableView) {
        if (self.sections.count == 0) {
            return nil;
        }
        moodleSection = self.sections[section];
        if (moodleSection.resources.count == 0) {
            return nil;
        }
    }
    if (tableView == self.searchController.searchResultsTableView) {
        if (self.searchFilteredSections.count == 0) {
            return nil;
        }
        moodleSection = self.searchFilteredSections[section];
        if (moodleSection.resources.count == 0) {
            return nil;
        }
    }
    
    NSString* title = moodleSection.titleOrDateRangeString;

    PCTableViewSectionHeader* header = [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView showInfoButton:(moodleSection.details.length > 0)];
    header.highlighted = moodleSection.isCurrent;
    __weak __typeof(self) welf = self;
    if (moodleSection.details.length > 0) {
        [header setInfoButtonTappedBlock:^{
            PCWebViewController* webViewController = [[PCWebViewController alloc] initWithHTMLString:moodleSection.webViewReadyDetails title:title];
            webViewController.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:welf action:@selector(sectionDetailsDoneButtonTapped)];
            PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:webViewController];
            navController.modalPresentationStyle = UIModalPresentationFormSheet;
            [welf presentViewController:navController animated:YES completion:NULL];
            [welf trackAction:@"ViewSectionDetails" contentInfo:moodleSection.title];
        }];
    }
    return header;
}

#pragma mark - UITableViewDataSource

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MoodleCourseSection2* section = tableView == self.tableView ? self.sections[indexPath.section] : self.searchFilteredSections[indexPath.section];
        MoodleResource2* resource = section.resources[indexPath.row];
        if (!resource.file) {
            //should not happen, as delete button should not appear if resource if not a file
            return;
        }
        [self trackAction:PCGAITrackerActionDelete contentInfo:resource.file.name];
        if ([self.moodleService deleteDownloadedMoodleFile:resource.file]) {
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
            NSString* message = self.segmentedControl.selectedSegmentIndex == kSegmentIndexFavorites ? NSLocalizedStringFromTable(@"MoodleNoFavorites", @"MoodlePlugin", nil) : NSLocalizedStringFromTable(@"MoodleEmptyCourse", @"MoodlePlugin", nil);
            return [[PCCenterMessageCell alloc] initWithMessage:message];
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
    
    MoodleCourseSection2* section = tableView == self.tableView ? self.sections[indexPath.section] : self.searchFilteredSections[indexPath.section];
    MoodleResource2* resource = section.resources[indexPath.row];
    MoodleResourceCell* cell = [self.cellForMoodleResource objectForKey:resource];
    
    if (!cell) {
        // happens if a "fake" resource was created to display a file outside its folder for example (in favorites or search modes)
        cell = [self newCellForMoodleResource:resource];
        [self.cellForMoodleResource setObject:cell forKey:resource];
    }
    
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
        MoodleCourseSection2* secObj = self.sections[section];
        return secObj.resources.count;
    }
    if (tableView == self.searchController.searchResultsTableView) {
        if (!self.searchFilteredSections) {
            return 0;
        }
        MoodleCourseSection2* secObj = self.searchFilteredSections[section];
        return secObj.resources.count;
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
        return self.searchFilteredSections.count;
    }
    return 0;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.moodleService removeMoodleFileObserver:self];
    [self.moodleService cancelOperationsForDelegate:self];
    [self.searchQueue cancelAllOperations];
    [self.typingTimer invalidate];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    @try {
        [self.segmentedControl removeObserver:self forKeyPath:NSStringFromSelector(@selector(frame))];
    }
    @catch (NSException *exception) {}
}

@end
