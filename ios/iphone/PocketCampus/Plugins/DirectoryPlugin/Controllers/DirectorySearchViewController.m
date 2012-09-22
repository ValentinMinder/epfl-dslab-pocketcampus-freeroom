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

@implementation DirectorySearchViewController

@synthesize searchBar, barActivityIndicator, tableView, messageLabel, backgroundIcon; 

static NSString* kAutocompleteResultCellIdentifier = @"autocompleteResult";
static NSString* kSearchResultCellIdentifier = @"searchResult";

- (id)init
{
    self = [super initWithNibName:@"DirectorySearchView" bundle:nil];
    if (self) {
        // Custom initialization
        directoryService = [[DirectoryService sharedInstanceToRetain] retain];
        typingTimer = nil;
        searchResults = nil;
        autocompleteResults = nil;
        resultsMode = ResutlsModeNotStarted;
        personViewController = nil;
        displayedPerson = nil;
        //skipNextSearchBarValueChange = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/directory" withError:NULL];
    searchBar.placeholder = NSLocalizedStringFromTable(@"SearchFieldPlaceholder", @"DirectoryPlugin", @"");
    [searchBar setIsAccessibilityElement:YES];
    searchBar.accessibilityLabel = NSLocalizedStringFromTable(@"SearchBar", @"DirectoryPlugin", nil);
    tableView.accessibilityIdentifier = @"SearchResults";
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:YES];
    [displayedPerson release];
    displayedPerson = nil;
    personViewController = nil; //so that profile picture request does not try to set picture for personViewController that is no longer displayed (and thus released)
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (resultsMode == ResutlsModeNotStarted) {
        [searchBar becomeFirstResponder];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [searchBar resignFirstResponder];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)startAutocompleteRequest {
    [directoryService cancelOperationsForDelegate:self];
    if (searchBar.text.length == 0) {
        return;
    }
    [barActivityIndicator startAnimating];
    [directoryService autocomplete:searchBar.text delegate:self];
}

- (void)startSearchRequest {
    [directoryService cancelOperationsForDelegate:self];
    if (searchBar.text.length == 0) {
        return;
    }
    [barActivityIndicator startAnimating];
    [directoryService searchPersons:searchBar.text delegate:self];
}

- (void)showNoResultMessage {
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    backgroundIcon.hidden = NO;
    messageLabel.text = NSLocalizedStringFromTable(@"SearchNoResult", @"DirectoryPlugin", @"Message that says the autocomplete/search returned empty result.");
    messageLabel.hidden = NO;
}

/* Search bar delegation */

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    /*if (skipNextSearchBarValueChange) {
        skipNextSearchBarValueChange = NO;
        return;
    }*/
    messageLabel.text = @"";
    if (searchText.length == 0) {
        [barActivityIndicator stopAnimating];
        [directoryService cancelOperationsForDelegate:self];
        tableView.hidden = YES;
        backgroundIcon.hidden = NO;
        messageLabel.hidden = YES;
        resultsMode = ResutlsModeNotStarted;
        [tableView reloadData];
        return;
    }
    [typingTimer invalidate];
    [typingTimer release];
    typingTimer = nil;
    
    NSArray* words = [searchText componentsSeparatedByString:@" "];
    
    if (words.count > 1) { //would actually start an LDAP search on server instead of autocomplete anyway
        typingTimer = [[NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startSearchRequest) userInfo:nil repeats:NO] retain];
    } else {
        typingTimer = [[NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startAutocompleteRequest) userInfo:nil repeats:NO] retain];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    if (resultsMode == ResutlsModeNotStarted || resultsMode == ResultsModeFailed) {
        messageLabel.text = @"";
        [self startAutocompleteRequest];
    }
    [searchBar resignFirstResponder];
}

/* UIScrollView delegation */

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [searchBar resignFirstResponder];
}


/* DirectoryServiceDelegate delegation */

- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results {
    [barActivityIndicator stopAnimating];
    if (results.count == 0) {
        [self showNoResultMessage];
        return;
    }
    
    if (searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        tableView.hidden = YES;
        backgroundIcon.hidden = NO;
        messageLabel.hidden = YES;
        return;
    }
    
    [autocompleteResults release];
    NSSet* autocompleteSet = [NSSet setWithArray:results]; //eliminate duplicates
    autocompleteResults = [[autocompleteSet allObjects] retain];
    resultsMode = ResultsModeAutocomplete;
    
    if (results.count == 1) {
        [barActivityIndicator startAnimating];
        NSString* searchString = [NSString stringWithFormat:@"%@", [results objectAtIndex:0]];
        [directoryService searchPersons:searchString delegate:self];
    } else {
        tableView.hidden = NO;
        backgroundIcon.hidden = YES;
        messageLabel.hidden = YES;
        [tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    }
    
}

- (void)autocompleteFailedFor:(NSString *)constraint {
    [self resultsError];
}

- (void)searchDirectoryFor:(NSString*)searchPattern didReturn:(NSArray*)results {
    [barActivityIndicator stopAnimating];
    if (results.count == 0) {
        [self showNoResultMessage];
        return;
    }
    
    if (searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        tableView.hidden = YES;
        backgroundIcon.hidden = NO;
        messageLabel.hidden = YES;
        return;
    }
    tableView.hidden = NO;
    backgroundIcon.hidden = YES;
    messageLabel.hidden = YES;
    
    [searchResults release];
    searchResults = [results retain];
    resultsMode = ResultsModeSearch;
    [tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    [tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
}

- (void)searchDirectoryFailedFor:(NSString*)searchPattern {
    [self resultsError];
}

- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data {
    if (self.navigationController.topViewController == personViewController) {
        [personViewController setProfilePictureData:data];
    }
}

- (void)profilePictureFailedFor:(NSString*)sciper {
    if (self.navigationController.topViewController == personViewController) {
        [personViewController setProfilePictureData:NULL];
    }
}

- (void)resultsError {
    [directoryService cancelOperationsForDelegate:self];
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    messageLabel.hidden = NO;
    resultsMode = ResultsModeFailed;
}

- (void)serviceConnectionToServerTimedOut {
    [directoryService cancelOperationsForDelegate:self];
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    messageLabel.hidden = NO;
    resultsMode = ResultsModeFailed;
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (resultsMode == ResultsModeAutocomplete) {
        UIActivityIndicatorView* activityIndicatorView = (UIActivityIndicatorView*)[[tableView cellForRowAtIndexPath:indexPath] accessoryView];
        if ([activityIndicatorView isAnimating]) {
            return; //means cell was already selected
        }
        [activityIndicatorView startAnimating];
        NSString* searchString = [NSString stringWithFormat:@"%@", [tableView cellForRowAtIndexPath:indexPath].textLabel.text];
        //skipNextSearchBarValueChange = YES;
        searchBar.text = searchString;
        [directoryService searchPersons:searchString delegate:self];
        [searchBar resignFirstResponder];
    } else if (resultsMode == ResultsModeSearch) {
        Person* person = [searchResults objectAtIndex:indexPath.row];
        personViewController = [[PCUnkownPersonViewController alloc] initWithDelegate:self];
        [personViewController setPerson:person];
        UIImage* loadingImage = [UIImage imageNamed:@"LoadingIndicator"];
        NSData* imageData = UIImagePNGRepresentation(loadingImage);
        [personViewController setProfilePictureData:imageData];
        [self.navigationController pushViewController:personViewController animated:YES];
        [personViewController release];
        displayedPerson = [person retain];
        [directoryService getProfilePicture:person.sciper delegate:self];
        //skipNextSearchBarValueChange = NO; //prevent bug in UIAutomation where sometimes search bar delegation is not called
    } else {
        //Not supported mode
    }
}


/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (resultsMode == ResultsModeAutocomplete) {
        UITableViewCell* newCell =  [tableView dequeueReusableCellWithIdentifier:kAutocompleteResultCellIdentifier];
        if (newCell == nil) {
            newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kAutocompleteResultCellIdentifier] autorelease];
            newCell.selectionStyle = UITableViewCellSelectionStyleGray;
            newCell.accessoryView = [[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite] autorelease];
        } else {
            [(UIActivityIndicatorView*)(newCell.accessoryView) stopAnimating];
        }
        newCell.textLabel.text = [autocompleteResults objectAtIndex:indexPath.row];
        return newCell;
    } else if (resultsMode == ResultsModeSearch) {
        UITableViewCell* newCell =  [tableView dequeueReusableCellWithIdentifier:kSearchResultCellIdentifier];
        if (newCell == nil) {
            newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kSearchResultCellIdentifier] autorelease];
            newCell.selectionStyle = UITableViewCellSelectionStyleGray;
        }
        Person* person = [searchResults objectAtIndex:indexPath.row];
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
    if(resultsMode == ResultsModeAutocomplete) {
        if (autocompleteResults == nil) { //should not happen in such mode
            return 0; 
        }
        return autocompleteResults.count;
    } else if (resultsMode == ResultsModeSearch) {
        if (searchResults == nil) { //should not happen in such mode
            return 0; 
        }
        return searchResults.count;
    } else {
        //ResulsModeNotStarted or ResultsModeFailed
        return 0;
    }
}


/* ABUnknownPersonViewControllerDelegate delegation */

- (BOOL)unknownPersonViewController:(ABUnknownPersonViewController *)personViewController shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    if (property == kABPersonAddressProperty) { //office was clicked
        /*NSString* firstName = (NSString*)ABRecordCopyValue(person, kABPersonFirstNameProperty);
        NSString* lastName = (NSString*)ABRecordCopyValue(person, kABPersonLastNameProperty);*/
        if (displayedPerson != nil) {
            [self.navigationController pushViewController:[MapController viewControllerWithInitialSearchQuery:displayedPerson.office pinLabelText:[NSString stringWithFormat:@"%@ %@", displayedPerson.firstName, displayedPerson.lastName]] animated:YES];
        }
        return NO;
    }
    return YES;
}

- (void)unknownPersonViewController:(ABUnknownPersonViewController *)unknownPersonView didResolveToPerson:(ABRecordRef)person {
    //Nothing
}

- (void)dealloc
{
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [directoryService cancelOperationsForDelegate:self];
    [directoryService release];
    [typingTimer release];
    [autocompleteResults release];
    [searchResults release];
    [super dealloc];
}


@end
