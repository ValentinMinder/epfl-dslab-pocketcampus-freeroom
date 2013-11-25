//
//  TransportNextDeparturesCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportNextDeparturesCell.h"

@implementation TransportNextDeparturesCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"TransportNextDeparturesCell" owner:nil options:nil];
    self = (TransportNextDeparturesCell*)elements[0];
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

@end
