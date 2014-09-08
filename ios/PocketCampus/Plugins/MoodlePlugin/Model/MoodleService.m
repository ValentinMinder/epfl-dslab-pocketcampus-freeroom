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


#import "MoodleService.h"

#import "PCPersistenceManager.h"

#import "AFNetworking.h"

#import "UIProgressView+AFNetworking.h"

#pragma mark - MoodleResourceObserver

@interface MoodleFileObserver : NSObject

@property (nonatomic, unsafe_unretained) id observer;
@property (nonatomic, strong) MoodleFile2* file;
@property (nonatomic, copy) MoodleResourceEventBlock eventBlock;

@end

@implementation MoodleFileObserver

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleFileObserver:object];
}

- (BOOL)isEqualToMoodleFileObserver:(MoodleFileObserver*)resourceObserver {
    return self.observer == resourceObserver.observer && [self.file isEqual:resourceObserver.file];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.observer hash];
    hash += [self.file hash];
    return hash;
}

@end

/**
 * Only used for fake casting from id to MoodleFile2 or MoodleUrl2
 * equivalent.
 */
@protocol MoodleLeafItem <NSObject>

@property (nonatomic, readonly) NSString* url;

@end


NSString* const kMoodleFavoritesMoodleItemsUpdatedNotification = @"MoodleFavoritesMoodleItemsUpdated";
NSString* const kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey = @"MoodleFavoritesStatusMoodleItemUpdated";

static const NSTimeInterval kFetchMoodleResourceTimeoutSeconds = 30.0;

static NSString* const kMoodleSessionKey = @"moodleSession";
static NSString* const kServiceDelegateKey = @"serviceDelegate";
static NSString* const kMoodleResourceKey = @"moodleResource";

static MoodleService* instance __weak = nil;

#pragma mark - MoodleService implementation

@interface MoodleService ()

@property (strong) NSMutableDictionary* filesObserversForFileKey; //key: [self keyForMoodleFile:] value: NSArray of MoodleResourceObserver
@property (nonatomic, strong) NSMutableSet* favoriteMoodleResourcesURLs; //set of NSString

@property (nonatomic, strong) AFHTTPSessionManager* resourcesDownloadSessionManager;

@end

@implementation MoodleService

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MoodleService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"moodle" thriftServiceClientClassName:NSStringFromClass(MoodleServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark - Resources favorites and file management

#pragma mark Private

static NSString* const kFavoriteMoodleResourcesURLs = @"favoriteMoodleResourcesURLs ";

- (void)initFavorites {
    if (!self.favoriteMoodleResourcesURLs) { //first try to get it from persistent storage
        self.favoriteMoodleResourcesURLs = [(NSSet*)[PCPersistenceManager objectForKey:kFavoriteMoodleResourcesURLs pluginName:@"moodle"] mutableCopy];
    }
    if (!self.favoriteMoodleResourcesURLs) { //if not present in persistent storage, create set
        self.favoriteMoodleResourcesURLs = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteMoodleResourcesURLs) {
        return YES;
    }
    return [PCPersistenceManager saveObject:self.favoriteMoodleResourcesURLs forKey:kFavoriteMoodleResourcesURLs pluginName:@"moodle"];
}

- (void)throwIfNotMoodleLeafItem:(id)object {
    if (![object isKindOfClass:[MoodleFile2 class]] && ![object isKindOfClass:[MoodleUrl2 class]]) {
        [NSException raise:@"Illegal argument" format:@"object must be of type MoodleFile2 or MoodleUrl2"];
    }
}

#pragma mark Public

 - (void)addFavoriteMoodleItem:(id)moodleItem {
    [self throwIfNotMoodleLeafItem:moodleItem];
    [self initFavorites];
    [self.favoriteMoodleResourcesURLs addObject:[(id<MoodleLeafItem>)moodleItem url]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kMoodleFavoritesMoodleItemsUpdatedNotification object:self userInfo:@{kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey:moodleItem}];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)removeFavoriteMoodleItem:(id)moodleItem {
    [self throwIfNotMoodleLeafItem:moodleItem];
    [self initFavorites];
    [self.favoriteMoodleResourcesURLs removeObject:[(id<MoodleLeafItem>)moodleItem url]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kMoodleFavoritesMoodleItemsUpdatedNotification object:self userInfo:@{kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey:moodleItem}];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (BOOL)isFavoriteMoodleItem:(id)moodleItem {
    [self initFavorites];
    return [self.favoriteMoodleResourcesURLs containsObject:[(id<MoodleLeafItem>)moodleItem url]];
}

- (NSString*)localPathForMoodleFile:(MoodleFile2*)moodleFile {
    return [self localPathForMoodleFile:moodleFile createIntermediateDirectories:NO];
}

 - (NSString*)localPathForMoodleFile:(MoodleFile2*)moodleFile createIntermediateDirectories:(BOOL)createIntermediateDirectories {
     [PCUtils throwExceptionIfObject:moodleFile notKindOfClass:[MoodleFile2 class]];
    
    //Trick to remove url query paramters if any (we don't want them for the filename)
    //http://stackoverflow.com/a/4272070/1423774
    NSURL* url = [NSURL URLWithString:moodleFile.url];
    url = [[NSURL alloc] initWithScheme:url.scheme host:url.host path:url.path];
    NSString* urlString = [url absoluteString];
    
    NSRange nsr = [urlString rangeOfString:@"/file.php/"];
    if (nsr.location == NSNotFound) {
        nsr = [urlString rangeOfString:@"/resource.php/"];
    }
    if (nsr.location == NSNotFound) {
        nsr = [urlString rangeOfCharacterFromSet:[NSCharacterSet characterSetWithCharactersInString:@"/"]];
    }
    NSString* nss = [urlString substringFromIndex:(nsr.location + nsr.length)];
    NSString* cacheMoodlePath = [self pathForResourcesDownloadFolder];
    NSString* filePath = [cacheMoodlePath stringByAppendingPathComponent:nss];
    
    if (createIntermediateDirectories) {
        NSString* directory = [filePath substringToIndex:[filePath rangeOfString:@"/" options:NSBackwardsSearch].location];
        BOOL isDir = TRUE;
        NSFileManager *fileManager= [NSFileManager defaultManager];
        if(![fileManager fileExistsAtPath:directory isDirectory:&isDir]) {
            if(![fileManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:NULL]) {
                CLSNSLog(@"-> Error while creating directory in cache : %@", directory);
            }
        }
    }
    return filePath;
}

 - (BOOL)isMoodleFileDownloaded:(MoodleFile2*)moodleFile {
    return [[NSFileManager defaultManager] fileExistsAtPath:[self localPathForMoodleFile:moodleFile]];
}

 - (BOOL)deleteDownloadedMoodleFile:(MoodleFile2*)moodleFile {
    NSError* error = nil;
    [[NSFileManager defaultManager] removeItemAtPath:[self localPathForMoodleFile:moodleFile] error:&error]; //OK to pass nil for error, method returns aleary YES/NO is case of success/failure
    if (!error) {
        /* Execute observers block */
        for (MoodleFileObserver* observer in self.filesObserversForFileKey[[self keyForMoodleFile:moodleFile]]) {
            if (observer.observer && observer.eventBlock) {
                observer.eventBlock(MoodleResourceEventDeleted);
            }
        }
        return YES;
    }
    return NO;
}

 - (BOOL)deleteAllDownloadedMoodleFiles {
    NSString* path = [self pathForResourcesDownloadFolder];
    NSFileManager* fileManager= [NSFileManager defaultManager];
    NSError* error = nil;
    [fileManager removeItemAtPath:path error:&error];
    if (!error) {
        /* Execute observers block */
        [self.filesObserversForFileKey enumerateKeysAndObjectsUsingBlock:^(id key, NSMutableSet* observers, BOOL *stop) {
            for (MoodleFileObserver* observer in observers) {
                if (observer.observer && observer.eventBlock) {
                    observer.eventBlock(MoodleResourceEventDeleted);
                }
            }
        }];
        return YES;
    }
    return NO;
}

 - (void)totalNbBytesAllDownloadedMoodleFilesWithCompletion:(void (^)(unsigned long long totalNbBytes, BOOL error))completion {
    if (!completion) {
        return;
    }
    NSString* path = [self pathForResourcesDownloadFolder];
    [PCUtils fileOrFolderSizeWithPath:path completion:^(unsigned long long totalNbBytes, BOOL error) {
        completion(totalNbBytes, error);
    }];
}

#pragma mark Private

- (NSString*)pathForResourcesDownloadFolder {
    NSString* path = [PCPersistenceManager appGroupBundleIdentifierPersistencePath];
    path = [path stringByAppendingPathComponent:@"moodle"];
    path = [path stringByAppendingPathComponent:@"downloads"];
    return path;
}

#pragma mark - Service methods

- (void)getCoursesWithRequest:(MoodleCoursesRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        MoodleCoursesResponse2* response = (__bridge id)result;
        return (response.statusCode == MoodleStatusCode2_OK);
    };
    operation.skipCache = YES;
    operation.serviceClientSelector = @selector(getCourses:);
    operation.delegateDidReturnSelector = @selector(getCoursesForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getSectionsWithRequest:(MoodleCourseSectionsRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        MoodleCourseSectionsResponse2* response = (__bridge id)result;
        return (response.statusCode == MoodleStatusCode2_OK);
    };
    operation.skipCache = YES;
    operation.serviceClientSelector = @selector(getSections:);
    operation.delegateDidReturnSelector = @selector(getSectionsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)printFileWithRequest:(MoodlePrintFileRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(printFile:);
    operation.delegateDidReturnSelector = @selector(printFileForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(printFileFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Cached versions

- (MoodleCoursesResponse2*)getFromCacheCoursesWithRequest:(MoodleCoursesRequest2*)request {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getCourses:);
    operation.delegateDidReturnSelector = @selector(getCoursesForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (MoodleCourseSectionsResponse2*)getFromCacheSectionsWithRequest:(MoodleCourseSectionsRequest2*)request {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getSections:);
    operation.delegateDidReturnSelector = @selector(getSectionsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

#pragma mark - MoodleResources observation

- (NSString*)keyForMoodleFile:(MoodleFile2*)file {
    return [NSString stringWithFormat:@"%u", (unsigned int)[file.url hash]];
}

- (void)addMoodleFileObserver:(id)observer_ forFile:(MoodleFile2*)file eventBlock:(MoodleResourceEventBlock)eventBlock {
    @synchronized(self) {
        NSString* key = [self keyForMoodleFile:file];
        
        if (!self.filesObserversForFileKey) {
            self.filesObserversForFileKey = [NSMutableDictionary dictionary];
        }
        
        NSMutableSet* currentObservers = self.filesObserversForFileKey[key];
        if (!currentObservers) {
            currentObservers = [NSMutableSet set];
            self.filesObserversForFileKey[key] = currentObservers;
        }
        
        MoodleFileObserver* observer = [[MoodleFileObserver alloc] init];
        observer.observer = observer_;
        observer.file = file;
        observer.eventBlock = eventBlock;
        [currentObservers addObject:observer];
    }
}

- (void)removeMoodleFileObserver:(id)observer {
    @synchronized (self) {
        [[self.filesObserversForFileKey copy] enumerateKeysAndObjectsUsingBlock:^(id key, NSMutableSet* observers, BOOL *stop) {
            for (MoodleFileObserver* resourceObserver in [observers copy]) {
                if (resourceObserver.observer == observer) {
                    [observers removeObject:resourceObserver];
                    if (observers.count == 0) {
                        [self.filesObserversForFileKey removeObjectForKey:key];
                    }
                }
            }
        }];
    }
}

- (void)removeMoodleFileObserver:(id)observer forFile:(MoodleFile2*)file {
    @synchronized (self) {
        NSString* key = [self keyForMoodleFile:file];
        NSMutableSet* observers = self.filesObserversForFileKey[key];
        for (MoodleFileObserver* resourceObserver in [observers copy]) {
            if (resourceObserver.observer == observer) {
                [observers removeObject:resourceObserver];
                if (observers.count == 0) {
                    [self.filesObserversForFileKey removeObjectForKey:key];
                }
            }
        }
    }
}

#pragma mark - Resource download

- (AFHTTPSessionManager*)resourcesDownloadSessionManager {
    if (!_resourcesDownloadSessionManager) {
        _resourcesDownloadSessionManager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        [_resourcesDownloadSessionManager setTaskWillPerformHTTPRedirectionBlock:^NSURLRequest *(NSURLSession *session, NSURLSessionTask *task, NSURLResponse *response, NSURLRequest *request) {
            /*
             * Redirect block is actually called for original request as well.
             * This logic allows the first request to pursue but not the second one
             * => prevents redirect
             */
            if (!response && task.originalRequest == task.currentRequest) {
                return request;
            }
            return nil;
        }];
    }
    return _resourcesDownloadSessionManager;
}

- (void)downloadMoodleFile:(MoodleFile2*)file progressView:(UIProgressView*)progressView delegate:(id)delegate {
    
    __weak __typeof(delegate) weakDelegate = delegate;
    NSString* localPath = [self localPathForMoodleFile:file createIntermediateDirectories:YES];
    NSURL* localURL = [NSURL fileURLWithPath:localPath];
    
    NSMutableURLRequest* mRequest = [self pcProxiedRequest];
    mRequest.cachePolicy = NSURLRequestReloadIgnoringCacheData;
    mRequest.timeoutInterval = kFetchMoodleResourceTimeoutSeconds;
    mRequest.HTTPMethod = @"POST";
    NSError* error = nil;
    NSDictionary* parameters = @{
                                 [moodleConstants MOODLE_RAW_ACTION_KEY]:[moodleConstants MOODLE_RAW_ACTION_DOWNLOAD_FILE],
                                 [moodleConstants MOODLE_RAW_FILE_PATH]:file.url
                                 };
    NSURLRequest* request = [[AFHTTPRequestSerializer serializer] requestBySerializingRequest:mRequest withParameters:parameters error:&error];
    if (error) {
        if ([delegate respondsToSelector:@selector(downloadFailedForMoodleFile:responseStatusCode:)]) {
            [delegate downloadFailedForMoodleFile:file responseStatusCode:-1];
        }
        return;
    }
    
    NSURLSessionDownloadTask* downloadTask = [self.resourcesDownloadSessionManager downloadTaskWithRequest:request progress:nil destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
        return localURL;
    } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
        if (error.code == NSURLErrorCancelled) {
            //should not call delegate when cancelled
            return;
        }
        void (^failedBlock)(NSInteger) = ^void (NSInteger statusCode) {
            [[NSFileManager defaultManager] removeItemAtPath:localPath error:nil]; //to be sure not empty/wrong file is there
            if ([weakDelegate respondsToSelector:@selector(downloadFailedForMoodleFile:responseStatusCode:)]) {
                [weakDelegate downloadFailedForMoodleFile:file responseStatusCode:(int)statusCode];
            }
        };
        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
            failedBlock(0);
            return;
        }
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        if (error) {
            failedBlock(httpResponse.statusCode);
        } else {
            switch (httpResponse.statusCode) {
                case 200:
                {
                    if ([weakDelegate respondsToSelector:@selector(downloadOfMoodleFile:didFinish:)]) {
                        [weakDelegate downloadOfMoodleFile:file didFinish:localURL];
                    }
                    // Execute observers block
                    for (MoodleFileObserver* observer in self.filesObserversForFileKey[[self keyForMoodleFile:file]]) {
                        if (observer.observer && observer.eventBlock) {
                            observer.eventBlock(MoodleResourceEventDownloaded);
                        }
                    }
                    break;
                }
                default:
                    failedBlock(httpResponse.statusCode);
                    break;
            }
            
        }
    }];
    downloadTask.taskDescription = [NSString stringWithFormat:@"%p", delegate];
    [progressView setProgressWithDownloadProgressOfTask:downloadTask animated:YES];
    [downloadTask resume];
}

- (void)cancelDownloadOfMoodleFilesForDelegate:(id)delegate {
    if (!_resourcesDownloadSessionManager) {
        return;
    }
    for (NSURLSessionTask* task in self.resourcesDownloadSessionManager.tasks) {
        if ([task.taskDescription isEqualToString:[NSString stringWithFormat:@"%p", delegate]]) {
            [task cancel];
        }
    }
}

#pragma mark - Service overrides

- (void)cancelOperationsForDelegate:(id<PCServiceDelegate>)delegate {
    [super cancelOperationsForDelegate:delegate];
    [self cancelDownloadOfMoodleFilesForDelegate:delegate];
}

#pragma mark - Dealloc

- (void)dealloc
{
    if (_resourcesDownloadSessionManager) {
        [_resourcesDownloadSessionManager invalidateSessionCancelingTasks:YES]; //might retain cycle the manager with its session otherwise. See http://stackoverflow.com/a/24370373/1423774
    }
    @synchronized(self) {
        instance = nil;
    }
}

@end