//
//  PCTableViewWithRemoteThumbnails.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCTableViewWithRemoteThumbnails.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "ASIDownloadCache.h"

#import "UIImage+Additions.h"

@interface PCTableViewWithRemoteThumbnails ()

@property (nonatomic, strong) ASINetworkQueue* networkQueue;
@property (nonatomic, strong) NSMutableDictionary* requestForIndexPath; //key: NSIndexPath, value: ASIHTTPRequest
@property (nonatomic, strong) NSMutableDictionary* thumbnails; //key : NSIndexPath , value : UIImage
@property (nonatomic, strong) Reachability* reachability;
@property (nonatomic, strong) NSMutableSet* failedThumbsIndexPaths;
@property (nonatomic) BOOL initDone;

@end

static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@implementation PCTableViewWithRemoteThumbnails


- (void)initDefaultValues {
    
    //private
    if (!self.initDone) {
        self.networkQueue = [[ASINetworkQueue alloc] init];
        self.networkQueue.maxConcurrentOperationCount = 6;
        [self.networkQueue setSuspended:NO];
        self.thumbnails = [[NSMutableDictionary alloc] init];
        self.requestForIndexPath = [NSMutableDictionary dictionary];
        self.initDone = YES;
    }
    
    //Only init if user not already set them
    if (!self.temporaryThumnail) {
        
        self.temporaryThumnail = [PCUtils strechableEmptyImageForCell];
    }
    if (self.thumbnailsCacheSeconds == 0.0) {
        self.thumbnailsCacheSeconds = 86400; //1day
    }
}

- (void)setThumbnailURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath {
    
    [self initDefaultValues];
    
    if (!url) {
        cell.imageView.image = self.temporaryThumnail; //Generic image sign
        return;
    }
    
    [PCUtils throughExceptionIfObject:cell notKindOfClass:[UITableViewCell class]];
    [PCUtils throughExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    
    if (!self.thumbnails[indexPath]) {
        cell.imageView.image = self.temporaryThumnail; //Temporary thumbnail until image is loaded
        ASIHTTPRequest* prevRequest = self.requestForIndexPath[indexPath];
        
        if (prevRequest) {
            [prevRequest cancel];
            prevRequest.delegate = nil;
            [self.requestForIndexPath removeObjectForKey:indexPath];
        }
        
        ASIHTTPRequest* thumbnailRequest = [ASIHTTPRequest requestWithURL:url];
        thumbnailRequest.downloadCache = [ASIDownloadCache sharedCache];
        thumbnailRequest.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;
        thumbnailRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
        thumbnailRequest.secondsToCache = self.thumbnailsCacheSeconds;
        thumbnailRequest.delegate = self;
        thumbnailRequest.didFinishSelector = @selector(thumbnailRequestFinished:);
        thumbnailRequest.didFailSelector = @selector(thumbnailRequestFailed:);
        thumbnailRequest.userInfo = [NSMutableDictionary dictionaryWithObjectsAndKeys:indexPath, kThumbnailIndexPathKey, nil];
        thumbnailRequest.timeOutSeconds = 10.0; //do not overload network with thumbnails that fail to load
        self.requestForIndexPath[indexPath] = thumbnailRequest;
        [self.networkQueue addOperation:thumbnailRequest];
    } else {
        cell.imageView.image = self.thumbnails[indexPath];
    }
}

- (void)reloadFailedThumbnailsCells {
    [self reloadRowsAtIndexPaths:[self.failedThumbsIndexPaths allObjects] withRowAnimation:UITableViewRowAnimationNone];
}

- (CGSize)thumbnailSizeForIndexPath:(NSIndexPath*)indexPath {
    CGFloat length = self.rowHeight;
    
    if ([self.delegate respondsToSelector:@selector(tableView:heightForRowAtIndexPath:)]) {
        length = [self.delegate tableView:self heightForRowAtIndexPath:indexPath];
    }
    return CGSizeMake(length, length);
}

#pragma mark - ASIHTTPRequestDelegate

- (void)thumbnailRequestFinished:(ASIHTTPRequest *)request {
    NSIndexPath* indexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    if (!indexPath) { //should never happen
        return;
    }
    if (self.failedThumbsIndexPaths) {
        [self.failedThumbsIndexPaths removeObject:indexPath];
    }
    
    [self.requestForIndexPath removeObjectForKey:indexPath];
    
    UITableViewCell* cell = [self cellForRowAtIndexPath:indexPath];
    
    if (request.responseData) {
        UIImage* image = [[UIImage alloc] initWithCGImage:[UIImage imageWithData:request.responseData].CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        
        if (image) {
            
            //NSData* imageData = UIImageJPEGRepresentation(image, 1.0);
            
            image = [image imageScaledToFitSize:[self thumbnailSizeForIndexPath:indexPath]];
            
            [self.thumbnails setObject:image forKey:indexPath];
            
            cell.imageView.contentMode = UIViewContentModeScaleAspectFill;
            cell.imageView.image = image; //using UIImage+Additions;
            
            [cell layoutSubviews];
        }
    }

}

- (void)thumbnailRequestFailed:(ASIHTTPRequest *)request {
    NSIndexPath* reqIndexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    
    if (!self.failedThumbsIndexPaths) {
        self.failedThumbsIndexPaths = [NSMutableSet setWithObject:reqIndexPath];
    } else {
        [self.failedThumbsIndexPaths addObject:reqIndexPath];
    }
    
    if (!self.reachability) {
        self.reachability = [Reachability reachabilityForInternetConnection];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadFailedThumbnailsCells) name:kReachabilityChangedNotification object:self.reachability];
        [self.reachability startNotifier];
    }
}

- (void)dealloc
{
    [self.reachability stopNotifier];
    for (ASIHTTPRequest* req in self.networkQueue.operations) {
        req.delegate = nil;
        [req cancel];
    }
    self.networkQueue.delegate = nil;
}


@end
