//
//  PCTableViewWithRemoteThumbnails.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCTableViewWithRemoteThumbnails.h"

#import "AFNetworking.h"

#import "UIImage+Additions.h"

@interface PCTableViewWithRemoteThumbnails ()

@property (nonatomic, strong) NSOperationQueue* operationQueue;
@property (nonatomic, strong) NSMutableDictionary* operationForIndexPath; //key: NSIndexPath, value: AFHTTPRequestOperation
@property (nonatomic, strong) NSMutableDictionary* urlForIndexPath; //key: NSIndexPath, value: NSURL
@property (nonatomic, strong) NSMutableDictionary* rawImageForUrlString; //key: NSURL.absoluteString, value: UIImage
@property (nonatomic, strong) NSMutableDictionary* imageForUrlString; //key: NSURL.absoluteString, value: UIImage (processed by imageProcessingBlock)
//@property (nonatomic, strong) Reachability* reachability;
@property (nonatomic, strong) NSMutableSet* failedThumbsIndexPaths;
@property (nonatomic) BOOL initDone;

@end

//static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@implementation PCTableViewWithRemoteThumbnails

#pragma mark - init

- (void)initDefaultValues {
    
    //private
    if (!self.initDone) {
        self.operationQueue = [NSOperationQueue new];
        [self.operationQueue setSuspended:NO];
        self.operationForIndexPath = [NSMutableDictionary dictionary];
        self.urlForIndexPath = [NSMutableDictionary dictionary];
        self.imageForUrlString = [NSMutableDictionary dictionary];
        self.rawImageForUrlString = [NSMutableDictionary dictionary];
        self.failedThumbsIndexPaths = [NSMutableSet set];
        
        PCTableViewWithRemoteThumbnails* weakSelf __weak = self;
        [[AFNetworkReachabilityManager sharedManager] setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            if (status > 0) { //means internet reachable
                [weakSelf reloadFailedThumbnailsCells];
            }
        }];
        
        self.initDone = YES;
    }
    
    //Only init if user not already set them
    if (self.imagesCacheSeconds == 0.0) {
        self.imagesCacheSeconds = 86400; //1day
    }
    if (!self.cellsImageViewSelectorString) {
        self.cellsImageViewSelectorString = @"imageView";
    }
}

#pragma mark - Public methods

- (void)setImageURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath {
    
    [self initDefaultValues];
    
    [PCUtils throwExceptionIfObject:cell notKindOfClass:[UITableViewCell class]];
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    
    if (!url) {
        NSURL* url = self.urlForIndexPath[indexPath];
        if (url) {
            [self.imageForUrlString removeObjectForKey:url];
            [self.rawImageForUrlString removeObjectForKey:url];
        }
        [self.urlForIndexPath removeObjectForKey:indexPath];
        [self imageViewForCell:cell].image = self.temporaryImage; //Generic image sign
        return;
    }
    
    self.urlForIndexPath[indexPath] = url;
    
    if (self.imageForUrlString[url.absoluteString]) {
        [self imageViewForCell:cell].image = self.imageForUrlString[url.absoluteString];
        [cell layoutSubviews];
        return;
    }
    
    [self imageViewForCell:cell].image = self.temporaryImage; //Temporary thumbnail until image is loaded
    AFHTTPRequestOperation* prevOperation = self.operationForIndexPath[indexPath];
    
    if (prevOperation) {
        [prevOperation cancel];
        [self.operationForIndexPath removeObjectForKey:indexPath];
    }
    
    /*ASIHTTPRequest* thumbnailRequest = [ASIHTTPRequest requestWithURL:url];
    thumbnailRequest.downloadCache = [ASIDownloadCache sharedCache];
    thumbnailRequest.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;
    thumbnailRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
    thumbnailRequest.secondsToCache = self.imagesCacheSeconds;
    thumbnailRequest.delegate = self;
    thumbnailRequest.didFinishSelector = @selector(thumbnailRequestFinished:);
    thumbnailRequest.didFailSelector = @selector(thumbnailRequestFailed:);
    
    NSMutableDictionary* userInfo = [NSMutableDictionary dictionary];
    userInfo[kThumbnailIndexPathKey] = indexPath;
    thumbnailRequest.userInfo = userInfo;
    thumbnailRequest.timeOutSeconds = 10.0; //do not overload network with thumbnails that fail to load
    self.requestForIndexPath[indexPath] = thumbnailRequest;
    [self.networkQueue addOperation:thumbnailRequest];*/
    
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc]initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:10.0]; //do not overload network with thumbnails that fail to loa
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = [AFImageResponseSerializer serializer];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* image) {
        [self.operationForIndexPath removeObjectForKey:indexPath];
        [self processAndSetImage:image forCell:cell atIndexPath:indexPath url:url];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self.operationForIndexPath removeObjectForKey:indexPath];
        [self.failedThumbsIndexPaths addObject:operation];
    }];
    [self.operationQueue addOperation:operation];
}

- (UIImage*)imageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    return self.imageForUrlString[url.absoluteString];
}

- (UIImage*)rawImageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    return self.rawImageForUrlString[url.absoluteString];
}

#pragma mark - Private methods

- (void)processAndSetImage:(UIImage*)image_ forCell:(UITableViewCell*)cell_ atIndexPath:(NSIndexPath*)indexPath_ url:(NSURL*)url_ {
    [PCUtils throwExceptionIfObject:image_ notKindOfClass:[UIImage class]];
    [PCUtils throwExceptionIfObject:cell_ notKindOfClass:[UITableViewCell class]];
    [PCUtils throwExceptionIfObject:indexPath_ notKindOfClass:[NSIndexPath class]];
    [PCUtils throwExceptionIfObject:url_ notKindOfClass:[NSURL class]];
    UIImage* image __block = image_;
    UITableViewCell* cell __block = cell_;
    NSIndexPath* indexPath __block = indexPath_;
    NSURL* url __block = url_;
    PCTableViewWithRemoteThumbnails* weakSelf __weak = self;
    dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(void){
        if (image && (image.imageOrientation != UIImageOrientationUp)) {
            image = [UIImage imageWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        }
        if (!weakSelf) {
            return;
        }
        UIImage* rawImage = image;
        if (weakSelf.imageProcessingBlock) {
            image = weakSelf.imageProcessingBlock(indexPath, cell, image);
        }
        if (!weakSelf) {
            return;
        }
        dispatch_async(dispatch_get_main_queue(), ^(void){
            if (!weakSelf) {
                return;
            }
            weakSelf.imageForUrlString[url.absoluteString] = image;
            weakSelf.rawImageForUrlString[url.absoluteString] = rawImage;
            [weakSelf imageViewForCell:cell].image = image;
            [cell layoutSubviews];
        });
    });
}

- (UIImageView*)imageViewForCell:(UITableViewCell*)cell {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    return (UIImageView*)[cell performSelector:NSSelectorFromString(self.cellsImageViewSelectorString)];
#pragma clang diagnostic pop
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

/*- (void)thumbnailRequestFinished:(ASIHTTPRequest *)request {
    NSIndexPath* indexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    if (!indexPath) { //should never happen
        return;
    }
    
    [self.failedThumbsIndexPaths removeObject:indexPath];
    [self.requestForIndexPath removeObjectForKey:indexPath];
    
    UITableViewCell* cell = [self cellForRowAtIndexPath:indexPath];
    
    if (request.responseData) {
        
        UIImage* image __block = [UIImage imageWithData:request.responseData];
        
        if (image) {
            dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(void){
                if (image && (image.imageOrientation != UIImageOrientationUp)) {
                    image = [UIImage imageWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
                }
                UIImage* rawImage = image;
                if (self.imageProcessingBlock) {
                    image = self.imageProcessingBlock(indexPath, cell, image);
                }
                dispatch_async(dispatch_get_main_queue(), ^(void){
                    self.imageForUrlString[request.url.absoluteString] = image;
                    self.rawImageForUrlString[request.url.absoluteString] = rawImage;
                    [self imageViewForCell:cell].image = image;
                    [cell layoutSubviews];
                });
            });
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
}*/

#pragma mark - Dealloc

- (void)dealloc
{
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
    [self.operationQueue cancelAllOperations];
}


@end
