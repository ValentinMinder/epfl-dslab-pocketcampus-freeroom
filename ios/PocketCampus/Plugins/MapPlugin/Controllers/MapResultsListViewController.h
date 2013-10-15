//
//  MapResultsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MapResultsListViewController : UITableViewController

- (id)initWithMapItems:(NSArray*)mapItems selectedInitially:(NSArray*)selectedInitially userValidatedSelectionBlock:(void (^)(NSArray* newlySelected))userValidatedSelectionBlock;

@property (nonatomic, copy) void (^userValidatedSelectionBlock)(NSArray*);

@end
