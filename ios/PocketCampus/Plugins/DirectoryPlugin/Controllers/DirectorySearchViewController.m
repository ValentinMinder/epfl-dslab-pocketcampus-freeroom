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



//  DirectoryPlugin
//  Created by Loïc Gardiol on 01.03.12.


#import "DirectorySearchViewController.h"

#import "MapController.h"

#import "DirectoryEmptyDetailViewController.h"

#import "PCRecentResultTableViewCell.h"

#import "MainController.h"

#import "PushNotifController.h"

#import "DirectoryPersonViewController.h"

#import "DirectoryService.h"

#import "DirectoryPersonCell.h"

typedef enum {
    ResutlsModeNotStarted = 0,
    ResultsModeSearch = 1,
    ResultsModeAutocomplete = 2, //deprecated, we are not using autocomplete anymore, but search directly
    ResultsModeRecentSearches = 3,
    ResultsModeFailed = 4
} ResultsMode;

@interface DirectorySearchViewController ()<UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource, DirectoryServiceDelegate>
 
@property (nonatomic, strong) DirectoryService* directoryService;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSArray* searchResults; //array of Person*
@property (nonatomic, strong) NSMutableOrderedSet* recentSearches; //ordered mutable set of NSString*  (most recent at index 0)
@property (nonatomic) ResultsMode resultsMode;
@property (nonatomic, strong) DirectoryPersonViewController* personViewController;
@property (nonatomic, strong) Person* displayedPerson;
@property (nonatomic) BOOL skipNextSearchBarValueChange;
@property (nonatomic) BOOL searchBarWasFirstResponder;

@property (nonatomic, strong) IBOutlet UISearchBar* searchBar;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* barActivityIndicator;
@property (nonatomic, strong) IBOutlet PCTableViewAdditions* tableView;
@property (nonatomic, strong) IBOutlet UILabel* messageLabel;
@property (nonatomic, strong) IBOutlet UIImageView* backgroundIcon;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* backgroundIconCenterYConstraint;

//#warning TO REMOVE, tests for PushNotif
//@property (nonatomic, strong) PushNotifController* pushController;

@end

@implementation DirectorySearchViewController

static NSString* const kSearchResultCellIdentifier = @"searchResultCell";
static NSString* const kRecentSearchCellIdentifier = @"recentSearchCell";

static NSUInteger const kMaxRecentSearches = 15;
static NSString* const kRecentSearchesKey = @"recentSearches";

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"DirectorySearchView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/directory";
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.resultsMode = ResutlsModeNotStarted;
        self.recentSearches = [(NSOrderedSet*)[PCPersistenceManager objectForKey:kRecentSearchesKey pluginName:@"directory" isCache:YES] mutableCopy]; //archived object are always returned as copy => immutable
        if (!self.recentSearches) {
            self.recentSearches = [NSMutableOrderedSet orderedSet];
        }
        self.searchBarWasFirstResponder = YES; //such that search bar is first responder at first launch
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    if ([PCUtils isIdiomPad]) {
        self.backgroundIconCenterYConstraint.constant = 130.0;
    }
    self.tableView.temporaryImage = [UIImage imageNamed:@"DirectoryEmptyPictureSmall"];
    self.tableView.imageProcessingBlock = ^UIImage*(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image) {
        CGFloat rowHeight = tableView.rowHeightBlock(tableView);
        image = [image imageByScalingAndCroppingForSize:CGSizeMake(rowHeight, rowHeight) applyDeviceScreenMultiplyingFactor:YES];
        return image;
    };
    self.tableView.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        if ([[UIApplication sharedApplication].preferredContentSizeCategory isEqualToString:UIContentSizeCategoryLarge]) { //Default
            return 50.0; //Because cell images are adapted for that size. This is common case.
        }
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle];
    };
    self.tableView.contentInset = UIEdgeInsetsMake(self.topLayoutGuide.length+self.searchBar.frame.size.height, 0.0, 0.0, 0.0);
    self.tableView.scrollIndicatorInsets = self.tableView.contentInset;
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchFieldPlaceholder", @"DirectoryPlugin", @"");
    [self searchBar:self.searchBar textDidChange:self.searchBar.text]; //show recent searches if any
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"Directory"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"Directory"];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    self.displayedPerson = nil;
    self.personViewController = nil; //so that profile picture request does not try to set picture for personViewController that is no longer displayed (and thus released)
    
    
/*#warning TO REMOVE, tests for PushNotif
    self.pushController = [PushNotifController sharedInstanceToRetain];
    
    [self.pushController registerDeviceForPushNotificationsWithPluginLowerIdentifier:@"<your_plugin>" reason:@"<notifications_are_used_for_this_and_that_bla_bla" success:^{
        
        //from this point, all requests to your thrift backend will include the device token
        
    } failure:^(PushNotifDeviceRegistrationError error) {
        //you might want to try again later
        //Note: this is normal in the simulator
    }];
    
    [self.pushController addPushNotificationObserver:self forPluginLowerIdentifier:@"<your_plugin_name_in_lowercase>" newNotificationBlock:^(NSString *notifMessage, NSDictionary *notifFullDictionary) {
        
        //
        
    }];
    
    [self.pushController removeObserver:self forPluginLowerIdentifier:@"<your_plugin_name_in_lowercase>"];*/
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.searchBar resignFirstResponder];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [PCPersistenceManager saveObject:self.recentSearches forKey:kRecentSearchesKey pluginName:@"directory" isCache:YES]; //persist recent searches to disk
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Notifications listening

- (void)willLoseForeground {
    self.searchBarWasFirstResponder = [self.searchBar isFirstResponder];
    [self.searchBar resignFirstResponder];
}

- (void)didEnterForeground {
    if (self.searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    }
}

#pragma mark - Misc

- (void)showNoResultMessage {
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.backgroundIcon.hidden = YES;    
    self.messageLabel.attributedText = [[NSAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"NoResult", @"DirectoryPlugin", nil) attributes:@{NSFontAttributeName:[UIFont preferredFontForTextStyle:UIFontTextStyleHeadline]}];
    self.messageLabel.hidden = NO;
}

- (void)showPersonViewControllerForPerson:(Person*)person {
    if (!person) {
        [self showEmptyDetailViewController];
        return;
    }
    self.personViewController = [[DirectoryPersonViewController alloc] initWithPerson:person];

    //self.personViewController = [[DirectoryPersonViewController alloc] initAndLoadPersonWithFullName:person.fullFirstnameLastname]; //TEST
    
    if (self.splitViewController) {
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:self.personViewController];
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else {
        [self.navigationController pushViewController:self.personViewController animated:YES];
    }
    self.displayedPerson = person;
}

- (void)showEmptyDetailViewController {
    if (self.splitViewController) {
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:[DirectoryEmptyDetailViewController new]]];
    }
    self.displayedPerson = nil;
}

- (void)promotePersonToRecentSearches:(Person*)person {
    NSUInteger currentIndex = [self.recentSearches indexOfObject:person.firstnameLastname];
    if (currentIndex == NSNotFound) { //this stupid logic needs to be done because there is no way to do in one step: add the object to top if it's not in the set already or move it if it is.
        [self.recentSearches insertObject:person.firstnameLastname atIndex:0]; // adding to top (works only if object not in set)
    } else {
        [self.recentSearches moveObjectsAtIndexes:[NSIndexSet indexSetWithIndex:currentIndex] toIndex:0]; //moving to top
    }
    
    // Cleaning old results
    if (self.recentSearches.count > kMaxRecentSearches) {
        [self.recentSearches removeObjectsInRange:NSMakeRange(kMaxRecentSearches, self.recentSearches.count - kMaxRecentSearches)];
    }
}

#pragma mark - Clear button

- (void)clearButtonPressed {
    [self trackAction:PCGAITrackerActionClearHistory];
    self.searchBar.text = @"";
    [self.recentSearches removeAllObjects];
    [PCPersistenceManager saveObject:nil forKey:kRecentSearchesKey pluginName:@"directory" isCache:YES]; //deleted cached recent searches
    [self searchBar:self.searchBar textDidChange:self.searchBar.text]; //reload default state
    [self.navigationItem setRightBarButtonItem:nil animated:YES]; //hide button after clearing
    [self showEmptyDetailViewController];
    [self.searchBar becomeFirstResponder];
}

#pragma mark - Requests start

- (void)startSearchRequest {
    [self.directoryService cancelOperationsForDelegate:self];
    if (self.searchBar.text.length == 0) {
        return;
    }
    [self.barActivityIndicator startAnimating];
    DirectoryRequest* request = [[DirectoryRequest alloc] initWithQuery:self.searchBar.text language:[PCUtils userLanguageCode] resultSetCookie:nil];
    [self.directoryService searchForRequest:request delegate:self];
}

#pragma mark - UISearchBarDelegate

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    if (self.skipNextSearchBarValueChange) {
        self.skipNextSearchBarValueChange = NO;
        return;
    }
    self.messageLabel.text = @"";
    if (searchText.length == 0) {
        [self.barActivityIndicator stopAnimating];
        [self.directoryService cancelOperationsForDelegate:self];
        
        if (self.recentSearches && self.recentSearches.count > 0) {
            self.tableView.hidden = NO;
            self.backgroundIcon.hidden = YES;
            self.messageLabel.hidden = YES;
            self.resultsMode = ResultsModeRecentSearches;
            [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
            UIBarButtonItem* clearButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Clear", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(clearButtonPressed)];
            clearButton.accessibilityHint = NSLocalizedStringFromTable(@"ClearsHistoryOfRecentSearches", @"PocketCampus", nil);
            [self.navigationItem setRightBarButtonItem:clearButton animated:YES];
        } else {
            
            self.backgroundIcon.hidden = NO;
            self.messageLabel.hidden = YES;
            self.resultsMode = ResutlsModeNotStarted;
            [UIView animateWithDuration:0.2 animations:^{
                self.tableView.alpha = 0.0;
                [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
            } completion:^(BOOL finished) {
                self.tableView.hidden = YES;
                self.tableView.alpha = 1.0;
            }];
        }
    } else {
        [self.typingTimer invalidate];
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startSearchRequest) userInfo:nil repeats:NO];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    if (self.resultsMode == ResutlsModeNotStarted || self.resultsMode == ResultsModeRecentSearches || self.resultsMode == ResultsModeFailed) {
        self.messageLabel.text = @"";
        [self startSearchRequest];
    }
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self.searchBar resignFirstResponder];
}

#pragma mark - DirectoryServiceDelegate

- (void)searchForRequest:(DirectoryRequest *)request didReturn:(DirectoryResponse *)response {
    NSArray* results = response.results;
    [self.barActivityIndicator stopAnimating];
    if (results.count == 0) {
        if (self.resultsMode == ResultsModeRecentSearches && self.searchBar.text.length == 0) {
            [self.recentSearches removeObject:request.query]; //means this recent result is not longer in directory (ex. left EPFL)
        }
        [self showNoResultMessage];
        return;
    }
    
    if (self.searchBar.text.length == 0 && self.resultsMode != ResultsModeRecentSearches) { //result from previous non-empty search returned => return to initial state
        return;
    }
    self.tableView.hidden = NO;
    self.backgroundIcon.hidden = YES;
    self.messageLabel.hidden = YES;
    
    self.searchResults = results;
    
    if (self.resultsMode == ResultsModeRecentSearches && self.searchBar.text.length == 0) {
        NSIndexPath* selectedIndexPath = [self.tableView indexPathForSelectedRow];
        if (selectedIndexPath) {
            Person* person = self.searchResults[0]; //first is excepted to be the one as recent searched made request with full first and last name
            [self showPersonViewControllerForPerson:person];
            [self promotePersonToRecentSearches:person];
            UIActivityIndicatorView* loadingView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:selectedIndexPath] accessoryView];
            if ([loadingView isKindOfClass:[UIActivityIndicatorView class]]) {
                [loadingView stopAnimating];
            }
        }
        
    } else {
        self.resultsMode = ResultsModeSearch;
        self.navigationItem.rightBarButtonItem = nil;
        @try {
            [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
            [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
        }
        @catch (NSException *exception) {
            [self.tableView reloadData];
        }
    }

}

- (void)searchFailedForRequest:(DirectoryRequest *)request {
    [self resultsError];
}

- (void)resultsError {
    [self.directoryService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    
    if (self.resultsMode == ResultsModeRecentSearches) {
        [PCUtils showServerErrorAlert];
        NSIndexPath* selectedIndexPath = [self.tableView indexPathForSelectedRow];
        if (selectedIndexPath) {
            UIActivityIndicatorView* loadingView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:selectedIndexPath] accessoryView];
            if ([loadingView isKindOfClass:[UIActivityIndicatorView class]]) {
                [loadingView stopAnimating];
            }
            [self.tableView deselectRowAtIndexPath:selectedIndexPath animated:YES];
        }
    } else {
        self.tableView.hidden = YES;
        self.backgroundIcon.hidden = YES;
        self.messageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
        self.messageLabel.hidden = NO;
        self.resultsMode = ResultsModeFailed;
    }
}

- (void)serviceConnectionToServerFailed {
    [self.directoryService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    
    if (self.resultsMode == ResultsModeRecentSearches) {
        [PCUtils showConnectionToServerTimedOutAlert];
        NSIndexPath* selectedIndexPath = [self.tableView indexPathForSelectedRow];
        if (selectedIndexPath) {
            UIActivityIndicatorView* loadingView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:selectedIndexPath] accessoryView];
            if ([loadingView isKindOfClass:[UIActivityIndicatorView class]]) {
                [loadingView stopAnimating];
            }
            [self.tableView deselectRowAtIndexPath:selectedIndexPath animated:YES];
        }
    } else {
        self.tableView.hidden = YES;
        self.backgroundIcon.hidden = YES;
        self.messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
        self.messageLabel.hidden = NO;
        self.resultsMode = ResultsModeFailed;
    }
}


#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.resultsMode == ResultsModeSearch) {
        if (indexPath.row >= self.searchResults.count) {
            //should not be required but crash report shows it can still happen...
            // https://www.crashlytics.com/pocketcampusorg/ios/apps/org.pocketcampus/issues/536b9213e3de5099ba2d40d0?km_variation=view+new+issue&kme=Clicked+from+Email&kmi=pocketcampus.ios%40gmail.com
            return;
        }
        Person* person = self.searchResults[indexPath.row];
        [self trackAction:@"ViewPerson" contentInfo:person.fullFirstnameLastname];
        if (self.splitViewController && [person.sciper isEqualToString:self.displayedPerson.sciper]) { //isEqual not implemented in Thrift
            [self.personViewController.navigationController popToRootViewControllerAnimated:YES]; //return to contact info if in map for example
            return;
        } else {
            [self showPersonViewControllerForPerson:person];
            [self promotePersonToRecentSearches:person];
            if (self.splitViewController) {
                [self.searchBar resignFirstResponder];
            }
        }
    } else if (self.resultsMode == ResultsModeRecentSearches) {
        
        UIActivityIndicatorView* activityIndicatorView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:indexPath] accessoryView];
         NSString* searchString = [NSString stringWithFormat:@"%@", [self.tableView cellForRowAtIndexPath:indexPath].textLabel.text];
        if ([activityIndicatorView isAnimating] || (self.displayedPerson && [searchString rangeOfString:self.displayedPerson.firstName].location != NSNotFound && [searchString rangeOfString:self.displayedPerson.lastName].location != NSNotFound)) {
            return; //means cell was already selected
        }
        [activityIndicatorView startAnimating];
        [self.directoryService cancelOperationsForDelegate:self];
        [self.directoryService searchForRequest:[[DirectoryRequest alloc] initWithQuery:searchString language:[PCUtils userLanguageCode] resultSetCookie:nil] delegate:self];
        [self.searchBar resignFirstResponder];
        [self trackAction:@"ViewRecentPerson" contentInfo:searchString];
    } else {
        //Unsupported mode
    }
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PCTableViewCellAdditions* cell = nil;
    switch (self.resultsMode) {
        case ResultsModeSearch:
        {
            NSString* const identifier = [self.tableView autoInvalidatingReuseIdentifierForIdentifier:@"SearchResultCell"];
            cell =  [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[DirectoryPersonCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
                cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellEditingStyleNone : UITableViewCellAccessoryDisclosureIndicator;
                cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
                cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
                cell.detailTextLabel.textColor = [UIColor grayColor];
                [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
                    return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
                }];
                [cell setAccessibilityHintBlock:^NSString *{
                    return NSLocalizedStringFromTable(@"ShowInfoAboutThisPerson", @"DirectoryPlugin", nil);
                }];
            }
            Person* person = self.searchResults[indexPath.row];
            cell.textLabel.text = person.firstnameLastname;
            cell.detailTextLabel.text = person.organizationalUnitsStrings;
            NSURL* url = person.pictureUrl ? [NSURL URLWithString:person.pictureUrl] : nil;
            [self.tableView setImageURL:url forCell:cell atIndexPath:indexPath];
            break;
        }
        case ResultsModeRecentSearches:
        {
            NSString* const identifier = [self.tableView autoInvalidatingReuseIdentifierForIdentifier:@"RecentSearchCell"];
            cell =  [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[PCRecentResultTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
                cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellEditingStyleNone : UITableViewCellAccessoryDisclosureIndicator;
                cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
                cell.accessoryView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
                [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
                    return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
                }];
            } else {
                [(UIActivityIndicatorView*)(cell.accessoryView) stopAnimating];
            }
            cell.textLabel.text = self.recentSearches[indexPath.row];
            break;
        }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (self.resultsMode) {
        case ResultsModeSearch:
            if (!self.searchResults) { //should not happen in such mode
                return 0;
            }
            return self.searchResults.count;
            break;
         case ResultsModeRecentSearches:
            if (!self.recentSearches) {
                return 0;
            }
            return self.recentSearches.count;
            break;
        default:
            return 0;
            break;
    }
}


#pragma mark - dealloc

- (void)dealloc
{
    [self.typingTimer invalidate];
    [[MainController publicController] removePluginStateObserver:self];
    [self.directoryService cancelOperationsForDelegate:self];
}


@end
