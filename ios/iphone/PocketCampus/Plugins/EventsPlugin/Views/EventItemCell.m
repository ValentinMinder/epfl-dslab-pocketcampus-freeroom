//
//  EventItemCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItemCell.h"

#import "EventItem+Additions.h"

@implementation EventItemCell

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    if (self) {
        NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EventItemCell" owner:self options:nil];
        EventItemCell* cell = (EventItemCell*)[elements objectAtIndex:0];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        self.eventItem = eventItem;
        return cell;
    }
    return self;
}

+ (CGFloat)height {
    return 64.0;
}

- (void)setEventItem:(EventItem *)eventItem {
    //TODO: load thumbnail
    _eventItem = eventItem;
    self.titleLabel.text = eventItem.eventTitle;
    self.subtitleLabel.text = eventItem.eventPlace;
    if (eventItem.secondLine) {
        self.subtitleLabel.text = eventItem.secondLine;
    }
    self.rightSubtitleLabel.text = [eventItem shortDateString];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
