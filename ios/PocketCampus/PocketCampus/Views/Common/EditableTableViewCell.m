//
//  EditableTableViewCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "EditableTableViewCell.h"

@implementation EditableTableViewCell


+ (id)editableCellWithPlaceholder:(NSString*)placeholder {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EditableTableViewCell" owner:self options:nil];
    EditableTableViewCell* cell = (EditableTableViewCell*)[elements objectAtIndex:0];
    cell.textField.placeholder = placeholder;
    [cell.contentView addSubview:cell.textField];
    //cell.textField.frame = CGRectOffset(cell.textField.frame, 0, -10.0);
    cell.textLabel.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    [cell.textLabel addObserver:cell forKeyPath:@"text" options:NSKeyValueObservingOptionNew context:NULL];
    return cell;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if ([keyPath isEqualToString:@"text"]) {
        [self repositionTextField];
    }
}

- (void)repositionTextField {
    CGSize reqSize = [self.textLabel.text sizeWithFont:[UIFont boldSystemFontOfSize:16.0]];
    CGFloat x = reqSize.width+25.0;
    self.textField.frame = CGRectMake(x, self.textField.frame.origin.y, self.frame.size.width - x - 5.0, self.frame.size.height);
}

- (void)dealloc {
    @try {
        [self.textLabel removeObserver:self forKeyPath:@"text"];
    }
    @catch (NSException *exception) {}
}

@end
