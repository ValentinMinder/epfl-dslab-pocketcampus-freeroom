//
//  PCTableViewSectionHeader.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCTableViewSectionHeader : UIView {
    UITableView* tableView;
}

- (id)initWithSectionTitle:(NSString*)sectionTitle tableView:(UITableView*)tableView;

@end
