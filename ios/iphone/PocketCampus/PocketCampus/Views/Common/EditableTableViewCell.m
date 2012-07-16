//
//  EditableTableViewCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "EditableTableViewCell.h"

@implementation EditableTableViewCell

@synthesize textField;


+ (id)editableCellWithPlaceholder:(NSString*)placeholder {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EditableTableViewCell" owner:self options:nil];
    EditableTableViewCell* cell = (EditableTableViewCell*)[elements objectAtIndex:0];
    cell.textField.placeholder = placeholder;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

@end
