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
@property (nonatomic, strong) NSCache* cachedRawImageForUrlString; //key: NSURL.absoluteString, value: UIImage
@property (nonatomic, strong) NSCache* cachedImageForUrlString; //key: NSURL.absoluteString, value: UIImage (processed by imageProcessingBlock)
@property (nonatomic, strong) AFNetworkReachabilityManager* reachabilityManager;
@property (nonatomic, strong) NSMutableSet* failedThumbsIndexPaths;
@property (nonatomic) BOOL initDone;

@end

@implementation PCTableViewWithRemoteThumbnails

#pragma mark - init

- (void)initDefaultValues {
    
    //private
    if (!self.initDone) {
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
        [self.operationQueue setSuspended:NO];
        self.operationForIndexPath = [NSMutableDictionary dictionary];
        self.urlForIndexPath = [NSMutableDictionary dictionary];
        self.cachedImageForUrlString = [NSCache new];
        self.cachedRawImageForUrlString = [NSCache new];
        self.failedThumbsIndexPaths = [NSMutableSet set];
        
        __weak __typeof(self) weakSelf = self;
        self.reachabilityManager = [AFNetworkReachabilityManager managerForDomain:@"google.com"];
        [self.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            if (status > 0 && weakSelf.failedThumbsIndexPaths.count > 0) { //means internet reachable
                [weakSelf reloadFailedThumbnailsCells];
            }
        }];
        [self.reachabilityManager startMonitoring];
        self.initDone = YES;
    }
    
    //Only init if user not already set them
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
            [self.cachedImageForUrlString removeObjectForKey:url];
            [self.cachedRawImageForUrlString removeObjectForKey:url];
        }
        [self.urlForIndexPath removeObjectForKey:indexPath];
        [self imageViewForCell:cell].image = self.temporaryImage; //Generic image sign
        [cell layoutSubviews];
        return;
    }
    
    if (self.cachedImageForUrlString[url.absoluteString]) {
        self.urlForIndexPath[indexPath] = url;
        [self imageViewForCell:cell].image = self.cachedImageForUrlString[url.absoluteString];
        [cell layoutSubviews];
        return;
    }
    
    self.urlForIndexPath[indexPath] = url;
    
    AFHTTPRequestOperation* prevOperation = self.operationForIndexPath[indexPath];
    if ([prevOperation.request.URL.absoluteString isEqualToString:url.absoluteString]) {
        //no need to start new request, just wait that current finishes
        //just make sure operation has correct priority
        [self manageOperationsPriority];
        return;
    } else {
        //need to start a new request as no current or with different URL
        [prevOperation setCompletionBlockWithSuccess:NULL failure:NULL];
        [prevOperation cancel];
        [self.operationForIndexPath removeObjectForKey:indexPath];
    }
    
    [self imageViewForCell:cell].image = self.temporaryImage; //Temporary thumbnail until image is loaded
    
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc]initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:10.0]; //do not overload network with thumbnails that fail to loa
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    self.operationForIndexPath[indexPath] = operation;
    operation.responseSerializer = [AFImageResponseSerializer serializer];
    __weak __typeof(self) weakSelf = self;
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* image) {
        [weakSelf.operationForIndexPath removeObjectForKey:indexPath];
        [weakSelf.failedThumbsIndexPaths removeObject:indexPath];
        if (image) {
            [weakSelf processAndSetImage:image atIndexPath:indexPath url:url];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [weakSelf.operationForIndexPath removeObjectForKey:indexPath];
        [weakSelf.failedThumbsIndexPaths addObject:indexPath];
    }];
    [self manageOperationsPriority];
    [self.operationQueue addOperation:operation];
}

- (UIImage*)cachedImageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    return self.cachedImageForUrlString[url.absoluteString];
}

- (UIImage*)cachedRawImageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    return self.cachedRawImageForUrlString[url.absoluteString];
}

#pragma mark - Private methods

- (void)processAndSetImage:(UIImage*)image_ atIndexPath:(NSIndexPath*)indexPath_ url:(NSURL*)url_ {
    [PCUtils throwExceptionIfObject:image_ notKindOfClass:[UIImage class]];
    [PCUtils throwExceptionIfObject:indexPath_ notKindOfClass:[NSIndexPath class]];
    [PCUtils throwExceptionIfObject:url_ notKindOfClass:[NSURL class]];
    UIImage* image __block = image_;
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
            image = weakSelf.imageProcessingBlock(indexPath, image);
        }
        if (!weakSelf) {
            return;
        }
        dispatch_async(dispatch_get_main_queue(), ^(void){
            if (!weakSelf) {
                return;
            }
            weakSelf.cachedImageForUrlString[url.absoluteString] = image;
            weakSelf.cachedRawImageForUrlString[url.absoluteString] = rawImage;
            UITableViewCell* cell = [weakSelf cellForRowAtIndexPath:indexPath];
            [weakSelf imageViewForCell:cell].image = image;
            [cell layoutSubviews];
        });
    });
}

- (void)manageOperationsPriority {
    NSSet* visibleIndexPaths = [NSSet setWithArray:self.indexPathsForVisibleRows];
    NSArray* allIndexPaths = [self.operationForIndexPath allKeys];
    for (NSIndexPath* indexPath in allIndexPaths) {
        NSOperation* operation = self.operationForIndexPath[indexPath];
        operation.queuePriority = [visibleIndexPaths containsObject:indexPath] ? NSOperationQueuePriorityHigh : NSOperationQueuePriorityLow;
    }
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

#pragma mark - Dealloc

- (void)dealloc
{
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
    [self.reachabilityManager stopMonitoring];
    
    [self.operationQueue cancelAllOperations];
}


@end
