//
//  PendingTableCell.m
//  PocketCampus
//
//  Created by Susheng on 4/21/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PendingTableCell.h"

@implementation PendingTableCell
@synthesize time, content, username, orderNumber;
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)dealloc {
    [username release];
    [time release];
    [content release];
    [orderNumber release];
    [super dealloc];
}
@end
