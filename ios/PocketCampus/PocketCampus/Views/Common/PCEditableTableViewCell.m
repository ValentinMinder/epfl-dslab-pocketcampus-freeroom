//
//  PCEditableTableViewCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCEditableTableViewCell.h"

@interface PCEditableTableViewCell ()

@property (nonatomic, strong, readwrite) UITextField* textField;

@end

@implementation PCEditableTableViewCell


+ (id)editableCellWithPlaceholder:(NSString*)placeholder {
    if (placeholder) {
        [PCUtils throwExceptionIfObject:placeholder notKindOfClass:[NSString class]];
    }
    PCEditableTableViewCell* cell = [[PCEditableTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    cell.textField = [[UITextField alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    cell.textField.placeholder = placeholder;
    cell.textField.font = cell.textLabel.font;
    [cell.contentView addSubview:cell.textField];
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
    CGFloat textLabelTextWidth = [self.textLabel textRectForBounds:CGRectMake(0, 0, 1000, self.frame.size.height) limitedToNumberOfLines:self.textLabel.numberOfLines].size.width;
    CGFloat x = self.textLabel.frame.origin.x+textLabelTextWidth+15.0;
    self.textField.frame = CGRectMake(x, self.textLabel.frame.origin.y, self.frame.size.width - x - 5.0, self.textLabel.frame.size.height > 0.0 ? self.textLabel.frame.size.height : self.frame.size.height);
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self repositionTextField];
}

- (void)dealloc {
    @try {
        [self.textLabel removeObserver:self forKeyPath:@"text"];
    }
    @catch (NSException *exception) {}
}

@end
