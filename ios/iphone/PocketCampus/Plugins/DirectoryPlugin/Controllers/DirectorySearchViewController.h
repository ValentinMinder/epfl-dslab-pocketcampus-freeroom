//
//  DirectorySearchViewController.h
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "PCUnkownPersonViewController.h"

#import "DirectoryService.h"

#import "directory.h"

typedef enum {
    ResutlsModeNotStarted = 0,
    ResultsModeSearch = 1,
    ResultsModeAutocomplete = 2,
    ResultsModeFailed = 3
} ResultsMode;

@interface DirectorySearchViewController : UIViewController<UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource, ABUnknownPersonViewControllerDelegate, DirectoryServiceDelegate> {
    UISearchBar* searchBar;
    UIActivityIndicatorView* barActivityIndicator;
    UITableView* tableView;
    UILabel* messageLabel;
    UIImageView* backgroundIcon;
    DirectoryService* directoryService;
    NSTimer* typingTimer;
    NSArray* autocompleteResults;
    NSArray* searchResults;
    ResultsMode resultsMode;
    PCUnkownPersonViewController* personViewController;
    Person* displayedPerson;
    BOOL skipNextSearchBarValueChange;
}

@property (nonatomic, assign) IBOutlet UISearchBar* searchBar;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* barActivityIndicator;
@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, assign) IBOutlet UILabel* messageLabel;
@property (nonatomic, assign) IBOutlet UIImageView* backgroundIcon;

@end
