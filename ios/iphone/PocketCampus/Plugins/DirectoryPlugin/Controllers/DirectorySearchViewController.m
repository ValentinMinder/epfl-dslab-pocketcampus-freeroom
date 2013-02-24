//
//  DirectorySearchViewController.m
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "DirectorySearchViewController.h"

#import "MapController.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

#import "DirectoryEmptyDetailViewController.h"

#import "PCRecentResultTableViewCell.h"

#import "MainController.h"

@interface DirectorySearchViewController ()
 
@property (nonatomic, strong) DirectoryService* directoryService;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSArray* autocompleteResults; //array of NSString*
@property (nonatomic, strong) NSArray* searchResults; //array of Person*
@property (nonatomic, strong) NSMutableOrderedSet* recentSearches; //ordered mutable set of NSString*  (most recent at index 0)
@property (nonatomic) ResultsMode resultsMode;
@property (nonatomic, strong) PCUnkownPersonViewController* personViewController;
@property (nonatomic, strong) Person* displayedPerson;
@property (nonatomic) BOOL skipNextSearchBarValueChange;
@property (nonatomic) BOOL searchBarWasFirstResponder;

@end

@implementation DirectorySearchViewController

static NSString* kAutocompleteResultCellIdentifier = @"autocompleteResultCell";
static NSString* kSearchResultCellIdentifier = @"searchResultCell";
static NSString* kRecentSearchCellIdentifier = @"recentSearchCell";

static NSUInteger kMaxRecentSearches = 15;
static NSString* kRecentSearchesKey = @"recentSearches";

- (id)init
{
    self = [super initWithNibName:@"DirectorySearchView" bundle:nil];
    if (self) {
        // Custom initialization
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.resultsMode = ResutlsModeNotStarted;
        self.recentSearches = [(NSOrderedSet*)[ObjectArchiver objectForKey:kRecentSearchesKey andPluginName:@"directory" isCache:YES] mutableCopy]; //archived object are always returned as copy => immutable
        if (!self.recentSearches) {
            self.recentSearches = [NSMutableOrderedSet orderedSet];
        }
        self.searchBarWasFirstResponder = YES; //such that search bar is first responder at first launch
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/directory" withError:NULL];
    if ([PCUtils isIdiomPad]) {
        self.barActivityIndicator.frame = CGRectOffset(self.barActivityIndicator.frame, 0, 1.0);
    }
    
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchFieldPlaceholder", @"DirectoryPlugin", @"");
    [self.searchBar setIsAccessibilityElement:YES];
    self.searchBar.accessibilityLabel = NSLocalizedStringFromTable(@"SearchBar", @"DirectoryPlugin", nil);
    self.tableView.accessibilityIdentifier = @"SearchResults";
    [self searchBar:self.searchBar textDidChange:self.searchBar.text]; //show recent searches if any
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"Directory"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"Directory"];
}

- (void)willLoseForeground {
    self.searchBarWasFirstResponder = [self.searchBar isFirstResponder];
    [self.searchBar resignFirstResponder];
}

- (void)didEnterForeground {
    if (self.searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    self.displayedPerson = nil;
    self.personViewController = nil; //so that profile picture request does not try to set picture for personViewController that is no longer displayed (and thus released)
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.searchBar resignFirstResponder];
}

- (void)viewDidDisappear:(BOOL)animated {
    [ObjectArchiver saveObject:self.recentSearches forKey:kRecentSearchesKey andPluginName:@"directory" isCache:YES]; //persist recent searches to disk
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)showNoResultMessage {
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.backgroundIcon.hidden = NO;
    self.messageLabel.text = NSLocalizedStringFromTable(@"SearchNoResult", @"DirectoryPlugin", @"Message that says the autocomplete/search returned empty result.");
    self.messageLabel.hidden = NO;
}

- (void)showPersonViewControllerForPerson:(Person*)person {
    if (!person) {
        [self showEmptyDetailViewController];
        return;
    }
    self.personViewController = [[PCUnkownPersonViewController alloc] initWithDelegate:self];
    [self.personViewController setPerson:person];
    if (self.splitViewController) {
        UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:self.personViewController];
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else {
        [self.navigationController pushViewController:self.personViewController animated:YES];
    }
    self.displayedPerson = person;
}

- (void)showEmptyDetailViewController {
    if (self.splitViewController) {
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[DirectoryEmptyDetailViewController alloc] init]];
    }
    self.displayedPerson = nil;
}

- (void)putPersonAtTopOfRecentSearches:(Person*)person {
    NSString* firstLast = [NSString stringWithFormat:@"%@ %@", person.firstName, person.lastName];
    NSUInteger currentIntex = [self.recentSearches indexOfObject:firstLast];
    if (currentIntex == NSNotFound) { //this stupid logic needs to be done because there is now way to do in one step: add the object to top if it's not in the set already or move it if it is.
        [self.recentSearches insertObject:firstLast atIndex:0]; // adding to top (works only if object not in set)
    } else {
        [self.recentSearches moveObjectsAtIndexes:[NSIndexSet indexSetWithIndex:currentIntex] toIndex:0]; //moving to top
    }
    
    /* Cleaning old results */
    if (self.recentSearches.count > kMaxRecentSearches) {
        [self.recentSearches removeObjectsInRange:NSMakeRange(kMaxRecentSearches, self.recentSearches.count - kMaxRecentSearches)];
    }
}

#pragma mark - clear button

- (void)clearButtonPressed {
    self.searchBar.text = @"";
    [self.recentSearches removeAllObjects];
    [ObjectArchiver saveObject:nil forKey:kRecentSearchesKey andPluginName:@"directory" isCache:YES]; //deleted cached recent searches
    [self searchBar:self.searchBar textDidChange:self.searchBar.text]; //reload default state
    [self.navigationItem setRightBarButtonItem:nil animated:YES]; //hide button after clearing
    [self showEmptyDetailViewController];
    [self.searchBar becomeFirstResponder];
}

#pragma mark - Requests start

- (void)startAutocompleteRequest {
    [self.directoryService cancelOperationsForDelegate:self];
    if (self.searchBar.text.length == 0) {
        return;
    }
    [self.barActivityIndicator startAnimating];
    [self.directoryService autocomplete:self.searchBar.text delegate:self];
}

- (void)startSearchRequest {
    [self.directoryService cancelOperationsForDelegate:self];
    if (self.searchBar.text.length == 0) {
        return;
    }
    [self.barActivityIndicator startAnimating];
    [self.directoryService searchPersons:self.searchBar.text delegate:self];
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
            [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Clear", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(clearButtonPressed)] animated:YES];
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
        return;
    }
    [self.typingTimer invalidate];
    
    NSNumber* potentatialSciper = [[[NSNumberFormatter alloc] init] numberFromString:searchText];
    NSArray* words = [searchText componentsSeparatedByString:@" "];
    if (words.count > 1 || potentatialSciper) { //would actually start an LDAP search on server instead of autocomplete anyway
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startSearchRequest) userInfo:nil repeats:NO];
    } else {
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startAutocompleteRequest) userInfo:nil repeats:NO];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    if (self.resultsMode == ResutlsModeNotStarted || self.resultsMode == ResultsModeRecentSearches || self.resultsMode == ResultsModeFailed) {
        self.messageLabel.text = @"";
        [self startAutocompleteRequest];
    }
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self.searchBar resignFirstResponder];
}


#pragma mark - DirectoryServiceDelegate

- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results {
    [self.barActivityIndicator stopAnimating];
    if (results.count == 0) {
        [self showNoResultMessage];
        return;
    }
    
    if (self.searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        return;
    }
    
    self.autocompleteResults = [[NSSet setWithArray:results] allObjects]; //eliminate duplicates
    self.resultsMode = ResultsModeAutocomplete;
    self.navigationItem.rightBarButtonItem = nil;
    if (results.count == 1) {
        [self.barActivityIndicator startAnimating];
        NSString* searchString = [NSString stringWithFormat:@"%@", [results objectAtIndex:0]];
        [self.directoryService searchPersons:searchString delegate:self];
    } else {
        self.tableView.hidden = NO;
        self.backgroundIcon.hidden = YES;
        self.messageLabel.hidden = YES;
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    }
    
}

- (void)autocompleteFailedFor:(NSString *)constraint {
    [self resultsError];
}

- (void)searchDirectoryFor:(NSString*)searchPattern didReturn:(NSArray*)results {
    [self.barActivityIndicator stopAnimating];
    if (results.count == 0) {
        if (self.resultsMode == ResultsModeRecentSearches && self.searchBar.text.length == 0) {
            [self.recentSearches removeObject:searchPattern]; //means this recent result is not longer in directory (ex. left EPFL)
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
            [self putPersonAtTopOfRecentSearches:person];
            UIActivityIndicatorView* loadingView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:selectedIndexPath] accessoryView];
            if ([loadingView isKindOfClass:[UIActivityIndicatorView class]]) {
                [loadingView stopAnimating];
            }
        }
        
    } else {
        self.resultsMode = ResultsModeSearch;
        self.navigationItem.rightBarButtonItem = nil;
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
        [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
    }
}

- (void)searchDirectoryFailedFor:(NSString*)searchPattern {
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
        self.messageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
        self.messageLabel.hidden = NO;
        self.resultsMode = ResultsModeFailed;
    }
}

- (void)serviceConnectionToServerTimedOut {
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
        self.messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
        self.messageLabel.hidden = NO;
        self.resultsMode = ResultsModeFailed;
    }
}


#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.resultsMode == ResultsModeAutocomplete) {
        UIActivityIndicatorView* activityIndicatorView = (UIActivityIndicatorView*)[[self.tableView cellForRowAtIndexPath:indexPath] accessoryView];
        if ([activityIndicatorView isAnimating]) {
            return; //means cell was already selected
        }
        [activityIndicatorView startAnimating];
        NSString* searchString = [NSString stringWithFormat:@"%@", [self.tableView cellForRowAtIndexPath:indexPath].textLabel.text];
        if (![PCUtils isOSVersionSmallerThan:6.0]) {
            //in iOS < 6.0, programatically setting search bar text triggers searchBar:textDidChange: we do not want it
            self.searchBar.text = searchString;
        }
        [self.directoryService searchPersons:searchString delegate:self];
        [self.searchBar resignFirstResponder];
    } else if (self.resultsMode == ResultsModeSearch) {
        Person* person = [self.searchResults objectAtIndex:indexPath.row];
        if (self.splitViewController && [person.sciper isEqualToString:self.displayedPerson.sciper]) { //isEqual not implemented in Thrift
            [self.personViewController.navigationController popToRootViewControllerAnimated:YES]; //return to contact info if in map for example
            return;
        } else {
            [self showPersonViewControllerForPerson:person];
            [self putPersonAtTopOfRecentSearches:person];
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
        [self.directoryService searchPersons:searchString delegate:self];
        [self.searchBar resignFirstResponder];
    } else {
        //Unsupported mode
    }
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (self.resultsMode == ResultsModeAutocomplete) {
        UITableViewCell* newCell =  [self.tableView dequeueReusableCellWithIdentifier:kAutocompleteResultCellIdentifier];
        if (newCell == nil) {
            newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kAutocompleteResultCellIdentifier];
            newCell.selectionStyle = UITableViewCellSelectionStyleGray;
            newCell.accessoryView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        } else {
            [(UIActivityIndicatorView*)(newCell.accessoryView) stopAnimating];
        }
        newCell.textLabel.text = [self.autocompleteResults objectAtIndex:indexPath.row];
        return newCell;
    } else if (self.resultsMode == ResultsModeSearch) {
        UITableViewCell* newCell =  [self.tableView dequeueReusableCellWithIdentifier:kSearchResultCellIdentifier];
        if (newCell == nil) {
            newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kSearchResultCellIdentifier];
            newCell.selectionStyle = UITableViewCellSelectionStyleGray;
        }
        /* Remove secondary first names */
        Person* person = [self.searchResults objectAtIndex:indexPath.row];
        NSString* firstNameOnly = person.firstName;
        NSArray* elems = [firstNameOnly componentsSeparatedByString:@" "];
        firstNameOnly = elems[0];
        
        NSString* firstLastName = [NSString stringWithFormat:@"%@ %@", firstNameOnly, person.lastName];
        
        if (firstLastName.length > 24) { //prevent textLabel hiding detailTextLabel
            firstLastName = [firstLastName stringByReplacingCharactersInRange:NSMakeRange(24, firstLastName.length-24) withString:@"..."];
        }
        
        newCell.textLabel.text = firstLastName;
        
        if (person.organisationalUnit) {
            newCell.detailTextLabel.text = [person.organisationalUnit objectAtIndex:0];
        }
        
        return newCell;
    } else if (self.resultsMode == ResultsModeRecentSearches) {
        PCRecentResultTableViewCell* newCell =  [self.tableView dequeueReusableCellWithIdentifier:kRecentSearchCellIdentifier];
        if (newCell == nil) {
            newCell = [[PCRecentResultTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kRecentSearchCellIdentifier];
            newCell.selectionStyle = UITableViewCellSelectionStyleGray;
            newCell.accessoryView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        } else {
            [(UIActivityIndicatorView*)(newCell.accessoryView) stopAnimating];
        }
        newCell.textLabel.text = [self.recentSearches objectAtIndex:indexPath.row];
        return newCell;
    } else {
        //Unsupported mode
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (self.resultsMode) {
        case ResultsModeAutocomplete:
            if (!self.autocompleteResults) { //should not happen in such mode
                return 0;
            }
            return self.autocompleteResults.count;
            break;
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


#pragma mark - ABUnknownPersonViewControllerDelegate

- (BOOL)unknownPersonViewController:(ABUnknownPersonViewController *)personViewController shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    if (property == kABPersonAddressProperty) { //office was clicked
        /*NSString* firstName = (NSString*)ABRecordCopyValue(person, kABPersonFirstNameProperty);
        NSString* lastName = (NSString*)ABRecordCopyValue(person, kABPersonLastNameProperty);*/
        if (self.displayedPerson != nil) {
            [self.personViewController.navigationController pushViewController:[MapController viewControllerWithInitialSearchQuery:self.displayedPerson.office pinLabelText:[NSString stringWithFormat:@"%@ %@", self.displayedPerson.firstName, self.displayedPerson.lastName]] animated:YES];
        }
        return NO;
    }
    return YES;
}

- (void)unknownPersonViewController:(ABUnknownPersonViewController *)unknownPersonView didResolveToPerson:(ABRecordRef)person {
    //Nothing
}

#pragma mark - dealloc

- (void)dealloc
{
    [[MainController publicController] removePluginStateObserver:self];
    [self.directoryService cancelOperationsForDelegate:self];
}


@end
