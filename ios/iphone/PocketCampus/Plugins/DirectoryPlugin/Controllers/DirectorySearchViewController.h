//
//  DirectorySearchViewController.h
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

#import "DirectoryService.h"

#import "directory.h"

typedef enum {
    ResutlsModeNotStarted = 0,
    ResultsModeSearch = 1,
    ResultsModeAutocomplete = 2,
    ResultsModeFailed = 3
} ResultsMode;

@interface DirectorySearchViewController : UIViewController<UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource, ABUnknownPersonViewControllerDelegate, DirectoryServiceDelegate> {
    IBOutlet UISearchBar* searchBar;
    IBOutlet UIActivityIndicatorView* barActivityIndicator;
    IBOutlet UITableView* tableView;
    IBOutlet UILabel* messageLabel;
    IBOutlet UIImageView* backgroundIcon;
    DirectoryService* directoryService;
    NSTimer* typingTimer;
    NSArray* autocompleteResults;
    NSArray* searchResults;
    ResultsMode resultsMode;
    ABUnknownPersonViewController* personViewController;
}

@end
