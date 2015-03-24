/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */


//  Created by Loïc Gardiol on 06.03.13.

#import "PCTableViewAdditions.h"

#import "AFNetworking.h"

#import "AppDelegate.h"

static id kEmptyImageValue;

static NSString* const kCancelledOperationUserInfoBoolKey = @"CancelledOperationUserInfoBool";

@interface PCTableViewAdditions ()

@property (nonatomic, strong) NSOperationQueue* operationQueue;
@property (nonatomic, strong) NSMutableDictionary* operationForIndexPath; //key: NSIndexPath, value: AFHTTPRequestOperation
@property (nonatomic, strong) NSMutableDictionary* urlForIndexPath; //key: NSIndexPath, value: NSURL
@property (nonatomic, strong) NSCache* cachedRawImageForUrlString; //key: NSURL.absoluteString, value: UIImage
@property (nonatomic, strong) NSCache* cachedImageForUrlString; //key: NSURL.absoluteString, value: UIImage (processed by imageProcessingBlock)
@property (nonatomic, strong) AFNetworkReachabilityManager* reachabilityManager;
@property (nonatomic, strong) NSMutableSet* failedThumbsIndexPaths;
@property (nonatomic) BOOL cellImagesManagementInitDone;

@property (nonatomic, strong) NSString* reuseIdentifierPrefix;

@property (nonatomic, strong) NSMutableDictionary* scrollViewStateForIdentifier;

@end

@implementation PCTableViewAdditions

#pragma mark - init

+ (void)initialize {
    if (self == [PCTableViewAdditions self]) {
        kEmptyImageValue = [NSNull null];
    }
}

- (id)init {
    self = [super init];
    if (self) {
        [self initDefaultValues];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)decoder {
    self = [super initWithCoder:decoder];
    if (self) {
        [self initDefaultValues];
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initDefaultValues];
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame style:(UITableViewStyle)style {
    self = [super initWithFrame:frame style:style];
    if (self) {
        [self initDefaultValues];
    }
    return self;
}

#pragma mark - Values initialization

- (void)initDefaultValues {
    self.reloadsDataWhenContentSizeCategoryChanges = YES;
    [self resetReuseIdentifiers];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(preferredContentSizeChanged:) name:UIContentSizeCategoryDidChangeNotification object:nil];
}

- (void)initCellImagesManagement {
    //private
    if (!self.cellImagesManagementInitDone) {
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
        [self.operationQueue setSuspended:NO];
        self.operationForIndexPath = [NSMutableDictionary dictionary];
        self.urlForIndexPath = [NSMutableDictionary dictionary];
        self.cachedImageForUrlString = [NSCache new];
        self.cachedRawImageForUrlString = [NSCache new];
        self.failedThumbsIndexPaths = [NSMutableSet set];
        
        __weak __typeof(self) welf = self;
        self.reachabilityManager = [AFNetworkReachabilityManager managerForDomain:@"google.com"];
        [self.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            [welf adaptMaxNbConcurrentOperationsBasedOnConnection];
            if (status > 0 && welf.failedThumbsIndexPaths.count > 0) { //means internet reachable
                [welf reloadFailedThumbnailsCells];
            }
        }];
        [self.reachabilityManager startMonitoring];
        
        //http://www.objc.io/issue-5/iOS7-hidden-gems-and-workarounds.html
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(adaptMaxNbConcurrentOperationsBasedOnConnection) name:CTRadioAccessTechnologyDidChangeNotification object:nil];
        
        [self adaptMaxNbConcurrentOperationsBasedOnConnection];
        
        self.cellImagesManagementInitDone = YES;
    }
    
    //Only init if user not already set them
    if (!self.cellsImageViewSelectorString) {
        self.cellsImageViewSelectorString = @"imageView";
    }
}

#pragma mark - Notification listening

- (void)adaptMaxNbConcurrentOperationsBasedOnConnection {
    if (AFNetworkReachabilityManager.sharedManager.networkReachabilityStatus == AFNetworkReachabilityStatusReachableViaWWAN) {
        NSString* currentRadioAccessTechnology = [[(AppDelegate*)[[UIApplication sharedApplication] delegate] telephonyInfo] currentRadioAccessTechnology];
        if ([currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyGPRS]
            || [currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyEdge]) {
            //max 1 op if no Wifi and (EDGE or GPRS)
            self.operationQueue.maxConcurrentOperationCount = 1;
            return;
        }
    }
    self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
}

- (void)preferredContentSizeChanged:(NSNotification *)notification {
    [NSTimer scheduledTimerWithTimeInterval:0.1 block:^{
        //delayed, so that all UIContentSizeCategoryDidChangeNotification in other classes have been delivered
        __weak __typeof(self) welf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            @try {
                // 1)
                if (welf.contentSizeCategoryDidChangeBlock) {
                    welf.contentSizeCategoryDidChangeBlock(welf);
                }
                // 2)
                if (welf.rowHeightBlock) {
                    welf.rowHeight = welf.rowHeightBlock(welf);
                }
                // 3)
                if (welf.reprocessesImagesWhenContentSizeCategoryChanges) {
                    [welf reprocessAllCachedImages];
                }
                // 4)
                if (welf.reloadsDataWhenContentSizeCategoryChanges) {
                    [welf resetReuseIdentifiers];
                    [welf reloadData];
                }
            }
            @catch (NSException *exception) {}
        });
    } repeats:NO];
}

#pragma mark - UITableView overrides

- (void)insertRowsAtIndexPaths:(NSArray *)indexPaths withRowAnimation:(UITableViewRowAnimation)animation {
    [self invalidateFailedThumbnailsIndexPaths];
    [super insertRowsAtIndexPaths:indexPaths withRowAnimation:animation];
}

- (void)deleteRowsAtIndexPaths:(NSArray *)indexPaths withRowAnimation:(UITableViewRowAnimation)animation {
    [self cancelImageDownloadForIndexPaths:indexPaths];
    [self invalidateFailedThumbnailsIndexPaths];
    [super deleteRowsAtIndexPaths:indexPaths withRowAnimation:animation];
}

- (void)moveRowAtIndexPath:(NSIndexPath *)indexPath toIndexPath:(NSIndexPath *)newIndexPath {
    [self invalidateFailedThumbnailsIndexPaths];
    [super moveRowAtIndexPath:indexPath toIndexPath:newIndexPath];
}

- (void)insertSections:(NSIndexSet *)sections withRowAnimation:(UITableViewRowAnimation)animation {
    [self invalidateFailedThumbnailsIndexPaths];
    [super insertSections:sections withRowAnimation:animation];
}

- (void)deleteSections:(NSIndexSet *)sections withRowAnimation:(UITableViewRowAnimation)animation {
    [self cancelImageDownloadForSections:sections];
    [self invalidateFailedThumbnailsIndexPaths];
    [super deleteSections:sections withRowAnimation:animation];
}

- (void)moveSection:(NSInteger)section toSection:(NSInteger)newSection {
    [self invalidateFailedThumbnailsIndexPaths];
    [self moveSection:section toSection:newSection];
}

- (void)reloadData {
    [self cancelAllImageDownloads];
    [self invalidateFailedThumbnailsIndexPaths];
    [super reloadData];
}

- (void)reloadRowsAtIndexPaths:(NSArray *)indexPaths withRowAnimation:(UITableViewRowAnimation)animation {
    [self cancelImageDownloadForIndexPaths:indexPaths];
    [self invalidateFailedThumbnailsIndexPaths];
    [super reloadRowsAtIndexPaths:indexPaths withRowAnimation:animation];
}

- (void)reloadSections:(NSIndexSet *)sections withRowAnimation:(UITableViewRowAnimation)animation {
    [self cancelImageDownloadForSections:sections];
    [self invalidateFailedThumbnailsIndexPaths];
    [super reloadSections:sections withRowAnimation:animation];
}

#pragma mark - Public methods

- (void)setImageURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath {
    
    [self initCellImagesManagement];
    
    [PCUtils throwExceptionIfObject:cell notKindOfClass:[UITableViewCell class]];
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    
    if (!url) {
        NSURL* prevURL = self.urlForIndexPath[indexPath];
        if (prevURL) {
            [self.cachedImageForUrlString removeObjectForKey:url];
            [self.cachedRawImageForUrlString removeObjectForKey:url];
            [self.urlForIndexPath removeObjectForKey:indexPath];
        }
        if (self.temporaryImage && [self imageViewForCell:cell].image) {
            [self setImage:self.temporaryImage ofCell:cell];
        }
        return;
    }
    
    if (self.cachedImageForUrlString[url.absoluteString]) {
        if (self.cachedImageForUrlString[url.absoluteString] == kEmptyImageValue) {
            //means previous request returned empty image. No need to check for it again.
            if (self.temporaryImage) {
                [self setImage:self.temporaryImage ofCell:cell];
            }
        } else {
            self.urlForIndexPath[indexPath] = url;
            [self setImage:self.cachedImageForUrlString[url.absoluteString] ofCell:cell];
        }
        return;
    }
    
    self.urlForIndexPath[indexPath] = url;
    
    AFHTTPRequestOperation* prevOperation = self.operationForIndexPath[indexPath];
    if ([prevOperation.request.URL.absoluteString isEqualToString:url.absoluteString]) {
        //no need to start new request, just wait that current finishes
        //just make sure operation has correct priority and reapply temporary image as cell might have been reused in between
        if (self.temporaryImage) {
            [self setImage:self.temporaryImage ofCell:cell];
        }
        [self manageOperationsPriority];
        return;
    } else if (prevOperation) {
        //need to start a new request as no current or with different UR
        prevOperation.userInfo = @{kCancelledOperationUserInfoBoolKey:@YES};
        [prevOperation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
            //nothing. Safer than NULL in case of race conditions
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            //nothing. Safer than NULL in case of race conditions
        }];
        [prevOperation cancel];
        [self.operationForIndexPath removeObjectForKey:indexPath];
    }
    if (self.temporaryImage) {
        [self setImage:self.temporaryImage ofCell:cell]; //Temporary thumbnail until image is loaded
    }
    
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc]initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:10.0]; //do not overload network with thumbnails that fail to loa
    
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    self.operationForIndexPath[indexPath] = operation;
    operation.responseSerializer = [AFImageResponseSerializer serializer];
    __weak __typeof(self) welf = self;
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* image) {
        if (image) {
            [welf processAndSetImage:image atIndexPath:indexPath url:url checkOperationUserInfoNotCancelled:operation completion:^{
                [welf.operationForIndexPath removeObjectForKey:indexPath];
                [welf.failedThumbsIndexPaths removeObject:indexPath];
            }];
        } else {
            welf.cachedRawImageForUrlString[url.absoluteString] = kEmptyImageValue;
            welf.cachedImageForUrlString[url.absoluteString] = kEmptyImageValue;
            if (welf.temporaryImage && ![operation.userInfo[kCancelledOperationUserInfoBoolKey] boolValue]) {
                [welf setImage:welf.temporaryImage atIndexIndex:indexPath];
            }
            [welf.operationForIndexPath removeObjectForKey:indexPath];
            [welf.failedThumbsIndexPaths removeObject:indexPath];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [welf.operationForIndexPath removeObjectForKey:indexPath];
        [welf.failedThumbsIndexPaths addObject:indexPath];
    }];
    [self manageOperationsPriority];
    [self.operationQueue addOperation:operation];
}

- (void)cancelImageDownloadForIndexPaths:(NSArray*)indexPaths {
    for (NSIndexPath* indexPath in indexPaths) {
        AFHTTPRequestOperation* operation = self.operationForIndexPath[indexPath];
        if (operation) {
            operation.userInfo = @{kCancelledOperationUserInfoBoolKey:@YES};
            [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
                //nothing. Safer that NULL in case of race conditions
            } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                //nothing. Safer that NULL in case of race conditions
            }];
            [operation cancel];
            [self.operationForIndexPath removeObjectForKey:indexPath];
        }
    }
}

- (void)cancelImageDownloadForSections:(NSIndexSet*)sections {
    NSMutableArray* indexPathsToCancel = [NSMutableArray array];
    for (NSIndexPath* indexPath in self.operationForIndexPath) {
        if ([sections containsIndex:indexPath.section]) {
            [indexPathsToCancel addObject:indexPath];
        }
    }
    [self cancelImageDownloadForIndexPaths:indexPathsToCancel];
}

- (void)cancelAllImageDownloads {
    [self cancelImageDownloadForIndexPaths:self.operationForIndexPath.allKeys];
}

- (UIImage*)cachedImageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    UIImage* image = self.cachedImageForUrlString[url.absoluteString];
    return image == kEmptyImageValue ? nil : image;
}

- (UIImage*)cachedRawImageAtIndexPath:(NSIndexPath*)indexPath {
    [PCUtils throwExceptionIfObject:indexPath notKindOfClass:[NSIndexPath class]];
    NSURL* url = self.urlForIndexPath[indexPath];
    UIImage* rawImage = self.cachedRawImageForUrlString[url.absoluteString];
    return rawImage == kEmptyImageValue ? nil : rawImage;
}

- (void)setImageProcessingBlock:(ImageProcessingBlock)imageProcessingBlock {
    _imageProcessingBlock = [imageProcessingBlock copy];
    [self reprocessAllCachedImages];
}

- (void)setRowHeightBlock:(RowHeightBlock)rowHeightBlock {
    _rowHeightBlock = [rowHeightBlock copy];
    if (rowHeightBlock) {
        self.rowHeight = rowHeightBlock(self);
    }
}

- (NSString*)autoInvalidatingReuseIdentifierForIdentifier:(NSString*)identifier {
    if (!identifier) {
        return nil;
    }
    return [self.reuseIdentifierPrefix stringByAppendingString:identifier];
}

static NSString* const kScrollViewStateContentOffset = @"ContentOffset";
static NSString* const kScrollViewStateContentSize = @"ContentSize";

- (void)saveContentOffsetForIdentifier:(NSString*)identifier {
    [PCUtils throwExceptionIfObject:identifier notKindOfClass:[NSString class]];
    if (!self.scrollViewStateForIdentifier) {
        self.scrollViewStateForIdentifier = [NSMutableDictionary dictionary];
    }
    self.scrollViewStateForIdentifier[identifier] = @{kScrollViewStateContentOffset:NSStringFromCGPoint(self.contentOffset),
                                                      kScrollViewStateContentSize:NSStringFromCGSize(self.contentSize)};
}

- (void)restoreContentOffsetForIdentifier:(NSString*)identifier {
    [PCUtils throwExceptionIfObject:identifier notKindOfClass:[NSString class]];
    NSString* originalContentOffsetString = self.scrollViewStateForIdentifier[identifier][kScrollViewStateContentOffset];
    if (!originalContentOffsetString) {
        return;
    }
    CGPoint originalContentOffset = CGPointFromString(originalContentOffsetString);
    NSString* originalContentSizeString = self.scrollViewStateForIdentifier[identifier][kScrollViewStateContentSize];
    if (!originalContentSizeString) {
        return;
    }
    CGSize originalContentSize = CGSizeFromString(originalContentSizeString);
    
    CGSize currentContentSize = self.contentSize;
    
    CGFloat newContentOffsetX = originalContentOffset.x * (currentContentSize.width / originalContentSize.width);
    CGFloat newContentOffsetY = originalContentOffset.y * (currentContentSize.height / originalContentSize.height);
    
    self.contentOffset = CGPointMake(newContentOffsetX, newContentOffsetY);
}

#pragma mark - Private methods

- (void)resetReuseIdentifiers {
    self.reuseIdentifierPrefix = [NSString stringWithFormat:@"%ld", random()];
}

- (void)processAndSetImage:(UIImage*)image_ atIndexPath:(NSIndexPath*)indexPath_ url:(NSURL*)url_ checkOperationUserInfoNotCancelled:(AFHTTPRequestOperation*)operation completion:(void (^)())completion_ {
    [PCUtils throwExceptionIfObject:image_ notKindOfClass:[UIImage class]];
    [PCUtils throwExceptionIfObject:indexPath_ notKindOfClass:[NSIndexPath class]];
    [PCUtils throwExceptionIfObject:url_ notKindOfClass:[NSURL class]];
    UIImage* image __block = image_;
    NSIndexPath* indexPath __block = indexPath_;
    NSURL* url __block = url_;
    void (^completion)() __block = completion_;
    __weak __typeof(self) welf = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(void){
        if (!completion) {
            completion = ^void(){};
        }
        if (!welf || [operation.userInfo[kCancelledOperationUserInfoBoolKey] boolValue]) {
            completion();
            return;
        }
        if (image && (image.imageOrientation != UIImageOrientationUp)) {
            image = [UIImage imageWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        }
        if (!welf || [operation.userInfo[kCancelledOperationUserInfoBoolKey] boolValue]) {
            completion();
            return;
        }
        UIImage* rawImage = image;
        if (welf.imageProcessingBlock) {
            image = welf.imageProcessingBlock(welf, indexPath, rawImage);
        }
        if (!welf || [operation.userInfo[kCancelledOperationUserInfoBoolKey] boolValue]) {
            completion();
            return;
        }
        dispatch_async(dispatch_get_main_queue(), ^(void){
            if (!welf || [operation.userInfo[kCancelledOperationUserInfoBoolKey] boolValue]) {
                completion();
                return;
            }
            welf.cachedImageForUrlString[url.absoluteString] = image;
            welf.cachedRawImageForUrlString[url.absoluteString] = rawImage;
            [welf setImage:image atIndexIndex:indexPath];
            completion();
        });
    });
}

- (void)reprocessAllCachedImages {
    //reprocessing already cached processed image
    [self.urlForIndexPath enumerateKeysAndObjectsUsingBlock:^(NSIndexPath* indexPath, NSURL* url, BOOL *stop) {
        UIImage* image = self.cachedImageForUrlString[url.absoluteString];
        UIImage* rawImage = self.cachedRawImageForUrlString[url.absoluteString];
        if (image && rawImage) {
            self.cachedImageForUrlString[url.absoluteString] = self.imageProcessingBlock ? self.imageProcessingBlock(self, indexPath, rawImage) : rawImage;
        }
    }];
}

- (void)manageOperationsPriority {
    NSSet* visibleIndexPaths = [NSSet setWithArray:self.indexPathsForVisibleRows];
    NSArray* allIndexPaths = [self.operationForIndexPath allKeys];
    for (NSIndexPath* indexPath in allIndexPaths) {
        NSOperation* operation = self.operationForIndexPath[indexPath];
        operation.queuePriority = [visibleIndexPaths containsObject:indexPath] ? NSOperationQueuePriorityHigh : NSOperationQueuePriorityLow;
    }
}

- (void)setImage:(UIImage*)image atIndexIndex:(NSIndexPath*)indexPath {
    if (!indexPath) {
        return;
    }
    UITableViewCell* cell = [self cellForRowAtIndexPath:indexPath];
    if (cell) {
        [self setImage:image ofCell:cell];
    }
}

- (void)setImage:(UIImage*)image ofCell:(UITableViewCell*)cell {
    if (!cell || [self imageViewForCell:cell].image == image) {
        return;
    }
    [[self imageViewForCell:cell] setImage:image];
    [cell layoutSubviews];
}

- (UIImageView*)imageViewForCell:(UITableViewCell*)cell {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    return (UIImageView*)[cell performSelector:NSSelectorFromString(self.cellsImageViewSelectorString)];
#pragma clang diagnostic pop
}

- (void)invalidateFailedThumbnailsIndexPaths {
    [self.failedThumbsIndexPaths removeAllObjects];
}

- (void)reloadFailedThumbnailsCells {
    @try {
        [self reloadRowsAtIndexPaths:[self.failedThumbsIndexPaths allObjects] withRowAnimation:UITableViewRowAnimationNone];
    }
    @catch (NSException *exception) {
        //Not suppose to happen as all data-modifing methods have been
        //overriden above to invalidate failed index paths.
        //but in case... too bad to crash.
    }
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
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.reachabilityManager stopMonitoring];
    [self cancelAllImageDownloads];
    [self.operationQueue cancelAllOperations];
}


@end
