//
//  MapRecentSearchesListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MapRecentSearchesListViewController : UITableViewController

- (id)initWithUserSelectedRecentSearchBlock:(void (^)(NSString* searchPattern))userSelectedRecentSearchBlock;

@property (nonatomic, copy) void (^userSelectedRecentSearchBlock)(NSString*);

@end
