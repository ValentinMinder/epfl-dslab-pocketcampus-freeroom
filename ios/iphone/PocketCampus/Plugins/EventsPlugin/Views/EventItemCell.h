//
//  EventItemCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "events.h"

#import "ASIHTTPRequest.h"

@interface EventItemCell : UITableViewCell<ASIHTTPRequestDelegate>

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier;

+ (CGFloat)height;

@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UILabel* subtitleLabel;
@property (nonatomic, weak) IBOutlet UILabel* rightSubtitleLabel;

@property (nonatomic, strong) EventItem* eventItem;

@end
