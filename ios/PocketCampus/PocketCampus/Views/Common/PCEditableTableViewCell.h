//
//  PCEditableTableViewCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCEditableTableViewCell : UITableViewCell

@property (nonatomic, readonly) UITextField* textField;

+ (id)editableCellWithPlaceholder:(NSString*)placeholder;

@end
