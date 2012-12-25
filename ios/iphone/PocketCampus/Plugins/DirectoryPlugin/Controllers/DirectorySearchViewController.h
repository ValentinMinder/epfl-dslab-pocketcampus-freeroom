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
    ResultsModeRecentSearches = 3,
    ResultsModeFailed = 4
} ResultsMode;

@interface DirectorySearchViewController : UIViewController<UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource, ABUnknownPersonViewControllerDelegate, DirectoryServiceDelegate>

@property (nonatomic, weak) IBOutlet UISearchBar* searchBar;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* barActivityIndicator;
@property (nonatomic, weak) IBOutlet UITableView* tableView;
@property (nonatomic, weak) IBOutlet UILabel* messageLabel;
@property (nonatomic, weak) IBOutlet UIImageView* backgroundIcon;

@end
