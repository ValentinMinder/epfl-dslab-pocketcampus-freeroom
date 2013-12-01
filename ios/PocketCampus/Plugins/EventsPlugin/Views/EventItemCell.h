//
//  EventItemCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "events.h"

#import "PCTableViewCellAdditions.h"

@interface EventItemCell : PCTableViewCellAdditions

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier;

+ (CGFloat)preferredHeight;

@property (nonatomic, strong) EventItem* eventItem;

@property (nonatomic) BOOL glowIfEventItemIsNow;

@end
