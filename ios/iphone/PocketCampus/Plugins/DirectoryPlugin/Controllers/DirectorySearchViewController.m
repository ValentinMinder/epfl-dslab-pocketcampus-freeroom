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

@interface DirectorySearchViewController ()
 
@property (nonatomic, strong) DirectoryService* directoryService;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSArray* autocompleteResults;
@property (nonatomic, strong) NSArray* searchResults;
@property (nonatomic) ResultsMode resultsMode;
@property (nonatomic, strong) PCUnkownPersonViewController* personViewController;
@property (nonatomic, strong) Person* displayedPerson;
@property (nonatomic) BOOL skipNextSearchBarValueChange;

@end

@implementation DirectorySearchViewController

static NSString* kAutocompleteResultCellIdentifier = @"autocompleteResult";
static NSString* kSearchResultCellIdentifier = @"searchResult";

static NSString* kRecentSearchesKey = @"recentSearches";

- (id)init
{
    self = [super initWithNibName:@"DirectorySearchView" bundle:nil];
    if (self) {
        // Custom initialization
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.resultsMode = ResutlsModeNotStarted;
        //skipNextSearchBarValueChange = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/directory" withError:NULL];
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchFieldPlaceholder", @"DirectoryPlugin", @"");
    [self.searchBar setIsAccessibilityElement:YES];
    self.searchBar.accessibilityLabel = NSLocalizedStringFromTable(@"SearchBar", @"DirectoryPlugin", nil);
    self.tableView.accessibilityIdentifier = @"SearchResults";
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    self.displayedPerson = nil;
    self.personViewController = nil; //so that profile picture request does not try to set picture for personViewController that is no longer displayed (and thus released)
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (self.resultsMode == ResutlsModeNotStarted) {
        [self.searchBar becomeFirstResponder];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.searchBar resignFirstResponder];
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
    /*if (skipNextSearchBarValueChange) {
        skipNextSearchBarValueChange = NO;
        return;
    }*/
    self.messageLabel.text = @"";
    if (searchText.length == 0) {
        [self.barActivityIndicator stopAnimating];
        [self.directoryService cancelOperationsForDelegate:self];
        self.tableView.hidden = YES;
        self.backgroundIcon.hidden = NO;
        self.messageLabel.hidden = YES;
        self.resultsMode = ResutlsModeNotStarted;
        [self.tableView reloadData];
        return;
    }
    [self.typingTimer invalidate];
    self.typingTimer = nil;
    
    NSArray* words = [searchText componentsSeparatedByString:@" "];
    
    if (words.count > 1) { //would actually start an LDAP search on server instead of autocomplete anyway
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startSearchRequest) userInfo:nil repeats:NO];
    } else {
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startAutocompleteRequest) userInfo:nil repeats:NO];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    if (self.resultsMode == ResutlsModeNotStarted || self.resultsMode == ResultsModeFailed) {
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
        self.tableView.hidden = YES;
        self.backgroundIcon.hidden = NO;
        self.messageLabel.hidden = YES;
        return;
    }
    
    self.autocompleteResults = [[NSSet setWithArray:results] allObjects]; //eliminate duplicates
    self.resultsMode = ResultsModeAutocomplete;
    
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
        [self showNoResultMessage];
        return;
    }
    
    if (self.searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        self.tableView.hidden = YES;
        self.backgroundIcon.hidden = NO;
        self.messageLabel.hidden = YES;
        return;
    }
    self.tableView.hidden = NO;
    self.backgroundIcon.hidden = YES;
    self.messageLabel.hidden = YES;
    
    self.searchResults = results;
    self.resultsMode = ResultsModeSearch;
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
}

- (void)searchDirectoryFailedFor:(NSString*)searchPattern {
    [self resultsError];
}

- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data {
    if (self.navigationController.topViewController == self.personViewController) {
        [self.personViewController setProfilePictureData:data];
    }
}

- (void)profilePictureFailedFor:(NSString*)sciper {
    if (self.navigationController.topViewController == self.personViewController) {
        [self.personViewController setProfilePictureData:NULL];
    }
}

- (void)resultsError {
    [self.directoryService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    self.messageLabel.hidden = NO;
    self.resultsMode = ResultsModeFailed;
}

- (void)serviceConnectionToServerTimedOut {
    [self.directoryService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    self.messageLabel.hidden = NO;
    self.resultsMode = ResultsModeFailed;
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
        //skipNextSearchBarValueChange = YES;
        self.searchBar.text = searchString;
        [self.directoryService searchPersons:searchString delegate:self];
        [self.searchBar resignFirstResponder];
    } else if (self.resultsMode == ResultsModeSearch) {
        Person* person = [self.searchResults objectAtIndex:indexPath.row];
        self.personViewController = [[PCUnkownPersonViewController alloc] initWithDelegate:self];
        [self.personViewController setPerson:person];
        UIImage* loadingImage = [UIImage imageNamed:@"LoadingIndicator"];
        NSData* imageData = UIImagePNGRepresentation(loadingImage);
        [self.personViewController setProfilePictureData:imageData];
        [self.navigationController pushViewController:self.personViewController animated:YES];
        self.displayedPerson = person;
        [self.directoryService getProfilePicture:person.sciper delegate:self];
        //skipNextSearchBarValueChange = NO; //prevent bug in UIAutomation where sometimes search bar delegation is not called
    } else {
        //Not supported mode
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
        Person* person = [self.searchResults objectAtIndex:indexPath.row];
        newCell.textLabel.text = [NSString stringWithFormat:@"%@ %@", person.firstName, person.lastName];
        if (person.organisationalUnit) {
            newCell.detailTextLabel.text = [person.organisationalUnit objectAtIndex:0];
        }
        
        return newCell;
    } else {
        //Unsupported mode
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(self.resultsMode == ResultsModeAutocomplete) {
        if (self.autocompleteResults == nil) { //should not happen in such mode
            return 0; 
        }
        return self.autocompleteResults.count;
    } else if (self.resultsMode == ResultsModeSearch) {
        if (self.searchResults == nil) { //should not happen in such mode
            return 0; 
        }
        return self.searchResults.count;
    } else {
        //ResulsModeNotStarted or ResultsModeFailed
        return 0;
    }
}


#pragma mark - ABUnknownPersonViewControllerDelegate

- (BOOL)unknownPersonViewController:(ABUnknownPersonViewController *)personViewController shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    if (property == kABPersonAddressProperty) { //office was clicked
        /*NSString* firstName = (NSString*)ABRecordCopyValue(person, kABPersonFirstNameProperty);
        NSString* lastName = (NSString*)ABRecordCopyValue(person, kABPersonLastNameProperty);*/
        if (self.displayedPerson != nil) {
            [self.navigationController pushViewController:[MapController viewControllerWithInitialSearchQuery:self.displayedPerson.office pinLabelText:[NSString stringWithFormat:@"%@ %@", self.displayedPerson.firstName, self.displayedPerson.lastName]] animated:YES];
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
    self.tableView.delegate = nil;
    self.tableView.dataSource = nil;
    [self.directoryService cancelOperationsForDelegate:self];
}


@end
