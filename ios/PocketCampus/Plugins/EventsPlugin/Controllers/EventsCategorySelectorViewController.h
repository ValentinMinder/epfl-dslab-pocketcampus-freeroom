//
//  CategorySelectorViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface EventsCategorySelectorViewController : UITableViewController

- (id)initWithCategories:(NSArray*)allCategories selectedInitially:(NSArray*)selectedInitially userValidatedSelectionBlock:(void (^)(NSArray* newlySelected))userValidatedSelectionBlock;

@property (nonatomic, copy) void (^userValidatedSelectionBlock)(NSArray*);

@end
