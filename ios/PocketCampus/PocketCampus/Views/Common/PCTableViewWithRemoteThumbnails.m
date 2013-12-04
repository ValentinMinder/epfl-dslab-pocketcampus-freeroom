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
@property (nonatomic, strong) AFNetworkReachabilityManager* reachabilityManager;
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
    
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc]initWithURL:url cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:10.0]; //do not overload network with thumbnails that fail to loa
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = [AFImageResponseSerializer serializer];
    __weak __typeof(self) weakSelf = self;
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* image) {
        [weakSelf.operationForIndexPath removeObjectForKey:indexPath];
        [weakSelf.failedThumbsIndexPaths removeObject:indexPath];
        if (image) {
            [weakSelf processAndSetImage:image forCell:cell atIndexPath:indexPath url:url];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [weakSelf.operationForIndexPath removeObjectForKey:indexPath];
        [weakSelf.failedThumbsIndexPaths addObject:indexPath];
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
