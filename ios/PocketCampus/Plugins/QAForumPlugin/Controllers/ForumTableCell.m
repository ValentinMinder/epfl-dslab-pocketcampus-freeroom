//
//  ForumTableCell.m
//  PocketCampus
//
//  Created by Susheng on 5/27/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "ForumTableCell.h"

@implementation ForumTableCell
@synthesize time, content, username, orderNumber, imageView;
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
    [imageView release];
    [super dealloc];
}

@end
