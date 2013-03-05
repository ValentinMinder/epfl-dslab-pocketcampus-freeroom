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

#import "UIImage+Additions.h"

@interface EventItemCell ()

@property (nonatomic, strong) ASIHTTPRequest* imageRequest;
@property (nonatomic, strong) NSString* customReuseIdentifier;

@end

@implementation EventItemCell

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EventItemCell" owner:self options:nil];
    EventItemCell* cell = (EventItemCell*)[elements objectAtIndex:0];
    self = cell;
    if (self) {
        self.customReuseIdentifier = reuseIdentifier;
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        self.selectionStyle = UITableViewCellSelectionStyleGray;
        self.titleLabel.text = @"";
        self.subtitleLabel.text = @"";
        self.rightSubtitleLabel.text = @"";
        self.rightSubtitleLabel.textColor = [UIColor colorWithRed:0.156863 green:0.250980 blue:0.458824 alpha:1.0];
        self.eventItem = eventItem;
    }
    return self;
}

+ (CGFloat)height {
    return 64.0;
}

- (NSString *) reuseIdentifier {
    return self.customReuseIdentifier;
}

- (void)setEventItem:(EventItem *)eventItem {
    _eventItem = eventItem;
    [self.imageRequest cancel];
    self.imageView.image = [PCUtils strechableEmptyImageForCell];
    self.titleLabel.text = eventItem.eventTitle;
    self.subtitleLabel.text = eventItem.eventPlace;
    if (eventItem.secondLine) {
        self.subtitleLabel.text = eventItem.secondLine;
    }
    if (eventItem.startDate) {
        self.rightSubtitleLabel.text = [eventItem dateString:EventItemDateStyleShort];
    }
    [self layoutSubviews];
    [self startImageRequest];
}

- (void)startImageRequest {
    if (!self.eventItem.eventThumbnail) {
        return;
    }
    [self.imageRequest cancel];
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
    self.imageRequest = nil;
    if (request.responseData.length == 0) {
        [self requestFailed:request];
        return;
    }

    UIImage* image = [[UIImage alloc] initWithCGImage:[UIImage imageWithData:request.responseData].CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
    
    NSData* imageData = UIImageJPEGRepresentation(image, 1.0);
    
    CGFloat size = [[self class] height];
    
    self.imageView.image = [[UIImage imageWithData:imageData] imageScaledToFitSize:CGSizeMake(size, size)]; //using UIImage+Additions
    
    [self layoutSubviews];
}

- (void)requestFailed:(ASIHTTPRequest*)request {
    request.delegate = nil;
    self.imageRequest = nil;
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
