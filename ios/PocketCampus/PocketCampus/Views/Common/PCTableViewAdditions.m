//
//  PCTableViewAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCTableViewAdditions.h"

#import "AFNetworking.h"

#import "UIImage+Additions.h"

static id kEmptyImageValue;

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

@end

@implementation PCTableViewAdditions

#pragma mark - init

+ (void)initialize {
    kEmptyImageValue = [NSNull null];
}

- (id)init {
    self = [super init];
    if (self) {
        [self initDefaultValues];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
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
        
        __weak __typeof(self) weakSelf = self;
        self.reachabilityManager = [AFNetworkReachabilityManager managerForDomain:@"google.com"];
        [self.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            if (status > 0 && weakSelf.failedThumbsIndexPaths.count > 0) { //means internet reachable
                [weakSelf reloadFailedThumbnailsCells];
            }
        }];
        [self.reachabilityManager startMonitoring];
        self.cellImagesManagementInitDone = YES;
    }
    
    //Only init if user not already set them
    if (!self.cellsImageViewSelectorString) {
        self.cellsImageViewSelectorString = @"imageView";
    }
}

#pragma mark - Notification listening

- (void)preferredContentSizeChanged:(NSNotification *)notification {
    [NSTimer scheduledTimerWithTimeInterval:0.1 block:^{
        //delayed, so that all UIContentSizeCategoryDidChangeNotification in other classes have been delivered
        __weak __typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            @try {
                // 1)
                if (weakSelf.contentSizeCategoryDidChangeBlock) {
                    weakSelf.contentSizeCategoryDidChangeBlock(weakSelf);
                }
                // 2)
                if (weakSelf.rowHeightBlock) {
                    weakSelf.rowHeight = self.rowHeightBlock(weakSelf);
                }
                // 3)
                if (weakSelf.reprocessesImagesWhenContentSizeCategoryChanges) {
                    [weakSelf reprocessAllCachedImages];
                }
                // 4)
                if (weakSelf.reloadsDataWhenContentSizeCategoryChanges) {
                    [weakSelf invalidateReuseIdentifiers];
                    [weakSelf reloadData];
                }
            }
            @catch (NSException *exception) {}
        });
    } repeats:NO];
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
        if ([self imageViewForCell:cell].image) {
            [self setImage:self.temporaryImage ofCell:cell];
        }
        return;
    }
    
    if (self.cachedImageForUrlString[url.absoluteString]) {
        if (self.cachedImageForUrlString[url.absoluteString] == kEmptyImageValue) {
            //means previous request returned empty image. No need to check for it again.
            [self setImage:self.temporaryImage ofCell:cell];
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
        [self setImage:self.temporaryImage ofCell:cell];
        [self manageOperationsPriority];
        return;
    } else if (prevOperation) {
        //need to start a new request as no current or with different URL
        [prevOperation setCompletionBlockWithSuccess:NULL failure:NULL];
        [prevOperation cancel];
        [self.operationForIndexPath removeObjectForKey:indexPath];
    }
    
    [self setImage:self.temporaryImage ofCell:cell]; //Temporary thumbnail until image is loaded
    
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
        } else {
            weakSelf.cachedRawImageForUrlString[url.absoluteString] = kEmptyImageValue;
            weakSelf.cachedImageForUrlString[url.absoluteString] = kEmptyImageValue;
            [weakSelf setImage:self.temporaryImage atIndexIndex:indexPath];
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

#pragma mark - Private methods

- (void)invalidateReuseIdentifiers {
    self.reuseIdentifierPrefix = [NSString stringWithFormat:@"%ld", random()];
}

- (void)processAndSetImage:(UIImage*)image_ atIndexPath:(NSIndexPath*)indexPath_ url:(NSURL*)url_ {
    [PCUtils throwExceptionIfObject:image_ notKindOfClass:[UIImage class]];
    [PCUtils throwExceptionIfObject:indexPath_ notKindOfClass:[NSIndexPath class]];
    [PCUtils throwExceptionIfObject:url_ notKindOfClass:[NSURL class]];
    UIImage* image __block = image_;
    NSIndexPath* indexPath __block = indexPath_;
    NSURL* url __block = url_;
    PCTableViewAdditions* weakSelf __weak = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(void){
        if (image && (image.imageOrientation != UIImageOrientationUp)) {
            image = [UIImage imageWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        }
        if (!weakSelf) {
            return;
        }
        UIImage* rawImage = image;
        if (weakSelf.imageProcessingBlock) {
            image = weakSelf.imageProcessingBlock(weakSelf, indexPath, rawImage);
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
            [weakSelf setImage:image atIndexIndex:indexPath];
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
