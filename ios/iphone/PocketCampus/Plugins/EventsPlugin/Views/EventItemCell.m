//
//  EventItemCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItemCell.h"

#import "EventItem+Additions.h"

#import "ASIDownloadCache.h"

#import "PCUtils.h"

@interface EventItemCell ()

@property (nonatomic, strong) ASIHTTPRequest* imageRequest;

@end

@implementation EventItemCell

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    if (self) {
        NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EventItemCell" owner:self options:nil];
        EventItemCell* cell = (EventItemCell*)[elements objectAtIndex:0];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
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
    self.imageView.image = [PCUtils strechableEmptyImageForCell];
    self.titleLabel.text = eventItem.eventTitle;
    self.subtitleLabel.text = eventItem.eventPlace;
    if (eventItem.secondLine) {
        self.subtitleLabel.text = eventItem.secondLine;
    }
    self.rightSubtitleLabel.text = [eventItem shortDateString];
    [self layoutSubviews];
    [self startImageRequest];
}

- (void)startImageRequest {
    if (!self.eventItem.eventThumbnail) {
        return;
    }
    self.imageRequest = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:self.eventItem.eventThumbnail]];
    self.imageRequest.timeOutSeconds = 10;
    self.imageRequest.delegate = self;
    self.imageRequest.downloadCache = [ASIDownloadCache sharedCache];
    self.imageRequest.cachePolicy = ASIAskServerIfModifiedWhenStaleCachePolicy;
    self.imageRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
    self.imageRequest.secondsToCache = 86400; //seconds == 1 day.
    [self.imageRequest startAsynchronous];
}

- (void)requestFinished:(ASIHTTPRequest*)request {
    request.delegate = nil;
    if (request.responseData.length == 0) {
        [self requestFailed:request];
        return;
    }
    self.imageView.image = [UIImage imageWithData:request.responseData];
    self.imageView.alpha = 1.0;
    [self layoutSubviews];
}

- (void)requestFailed:(ASIHTTPRequest*)request {
    self.imageView.image = [PCUtils strechableEmptyImageForCell];
    [self layoutSubviews];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)dealloc {
    [self.imageRequest cancel];
    self.imageRequest.delegate = nil;
}

@end
