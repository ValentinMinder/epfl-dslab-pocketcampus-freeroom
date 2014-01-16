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

#import "PCObjectArchiver.h"

#import "AFNetworking.h"

#import "UIProgressView+AFNetworking.h"

#pragma mark - MoodleResourceObserver implementation

@implementation MoodleResourceObserver

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleResourceObserver:object];
}

- (BOOL)isEqualToMoodleResourceObserver:(MoodleResourceObserver*)resourceObserver {
    return self.observer == resourceObserver.observer && [self.resource isEqual:resourceObserver.resource];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.observer hash];
    hash += [self.resource hash];
    return hash;
}

@end


NSString* const kMoodleFavoritesMoodleResourcesUpdatedNotification = @"kFavoritesMoodleResourcesUpdatedNotificationName";
NSString* const kMoodleFavoriteStatusMoodleResourceUpdatedUserInfoKey = @"kFavoriteStatusMoodleResourceUpdatedKey";

static const NSTimeInterval kFetchMoodleResourceTimeoutSeconds = 30.0;

static NSString* const kMoodleSessionKey = @"moodleSession";
static NSString* const kServiceDelegateKey = @"serviceDelegate";
static NSString* const kMoodleResourceKey = @"moodleResource";

static MoodleService* instance __weak = nil;

#pragma mark - MoodleService implementation

@interface MoodleService ()

@property (nonatomic, strong) MoodleSession* session;
@property (strong) NSMutableDictionary* resourcesObserversForResourceKey; //key: [self keyForMoodleResource:] value: NSArray of MoodleResourceObserver
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

#pragma mark - Session

- (MoodleRequest*)createMoodleRequestWithCourseId:(int)courseId {
    SessionId* sessionId = [[SessionId alloc] initWithTos:TypeOfService_SERVICE_MOODLE pocketCampusSessionId:nil moodleCookie:[self lastSession].moodleCookie camiproCookie:nil isaCookie:nil];
    MoodleRequest* request = [[MoodleRequest alloc] initWithISessionId:sessionId iLanguage:[PCUtils userLanguageCode] iCourseId:courseId];
    return request;
}

- (MoodleSession*)lastSession {
    if (self.session) {
        return self.session;
    }
    self.session = (MoodleSession*)[PCObjectArchiver objectForKey:kMoodleSessionKey andPluginName:@"moodle"];
    return self.session;
}

- (BOOL)saveSession:(MoodleSession*)session {
    self.session = session;
    return [PCObjectArchiver saveObject:session forKey:kMoodleSessionKey andPluginName:@"moodle"];
}

- (BOOL)deleteSession {
    self.session = nil;
    return [PCObjectArchiver saveObject:nil forKey:kMoodleSessionKey andPluginName:@"moodle"];
}

#pragma mark - Resources favorites and file management

static NSString* const kFavoriteMoodleResourcesURLs = @"favoriteMoodleResourcesURLs ";

- (void)initFavorites {
    if (!self.favoriteMoodleResourcesURLs) { //first try to get it from persistent storage
        self.favoriteMoodleResourcesURLs = [(NSSet*)[PCObjectArchiver objectForKey:kFavoriteMoodleResourcesURLs andPluginName:@"moodle"] mutableCopy];
    }
    if (!self.favoriteMoodleResourcesURLs) { //if not present in persistent storage, create set
        self.favoriteMoodleResourcesURLs = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteMoodleResourcesURLs) {
        return YES;
    }
    return [PCObjectArchiver saveObject:self.favoriteMoodleResourcesURLs forKey:kFavoriteMoodleResourcesURLs andPluginName:@"moodle"];
}

- (void)addFavoriteMoodleResource:(MoodleResource*)moodleResource {
    [PCUtils throwExceptionIfObject:moodleResource notKindOfClass:[MoodleResource class]];
    [self initFavorites];
    [self.favoriteMoodleResourcesURLs addObject:moodleResource.iUrl];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kMoodleFavoritesMoodleResourcesUpdatedNotification object:self userInfo:@{kMoodleFavoriteStatusMoodleResourceUpdatedUserInfoKey:moodleResource}];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)removeFavoriteMoodleResource:(MoodleResource*)moodleResource {
    [PCUtils throwExceptionIfObject:moodleResource notKindOfClass:[MoodleResource class]];
    [self initFavorites];
    [self.favoriteMoodleResourcesURLs removeObject:moodleResource.iUrl];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kMoodleFavoritesMoodleResourcesUpdatedNotification object:self userInfo:@{kMoodleFavoriteStatusMoodleResourceUpdatedUserInfoKey:moodleResource}];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (BOOL)isFavoriteMoodleResource:(MoodleResource*)moodleResource {
    [self initFavorites];
    return [self.favoriteMoodleResourcesURLs containsObject:moodleResource.iUrl];
}

- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource {
    return [self localPathForMoodleResource:moodleResource createIntermediateDirectories:NO];
}

- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource createIntermediateDirectories:(BOOL)createIntermediateDirectories {
    if (![moodleResource isKindOfClass:[MoodleResource class]]) {
        @throw [NSException exceptionWithName:@"bad moodleResource argument" reason:@"moodleResource is not kind of class MoodleResource" userInfo:nil];
    }
    NSString* urlString = moodleResource.iUrl;
    NSRange nsr = [urlString rangeOfString:@"/file.php/"];
    if (nsr.location == NSNotFound) {
        nsr = [urlString rangeOfString:@"/resource.php/"];
    }
    if (nsr.location == NSNotFound) {
        nsr = [urlString rangeOfCharacterFromSet:[NSCharacterSet characterSetWithCharactersInString:@"/"]];
    }
    NSString* nss = [urlString substringFromIndex:(nsr.location + nsr.length)];
    NSArray* cachePathArray = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES);
    NSString* cachePath = [[cachePathArray lastObject] stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    NSString* cacheMoodlePath = [cachePath stringByAppendingPathComponent:@"moodle"];
    cacheMoodlePath = [cacheMoodlePath stringByAppendingPathComponent:@"downloads"];
    NSString* filePath = [cacheMoodlePath stringByAppendingPathComponent:nss];
    
    if (createIntermediateDirectories) {
        NSString* directory = [filePath substringToIndex:[filePath rangeOfString:@"/" options:NSBackwardsSearch].location];
        BOOL isDir = TRUE;
        NSFileManager *fileManager= [NSFileManager defaultManager];
        if(![fileManager fileExistsAtPath:directory isDirectory:&isDir]) {
            if(![fileManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:NULL]) {
                NSLog(@"-> Error while creating directory in cache : %@", directory);
            }
        }
    }
    return filePath;
}

- (BOOL)isMoodleResourceDownloaded:(MoodleResource*)moodleResource {
    return [[NSFileManager defaultManager] fileExistsAtPath:[self localPathForMoodleResource:moodleResource]];
}

- (BOOL)deleteDownloadedMoodleResource:(MoodleResource*)moodleResource {
    NSError* error = nil;
    [[NSFileManager defaultManager] removeItemAtPath:[self localPathForMoodleResource:moodleResource] error:&error]; //OK to pass nil for error, method returns aleary YES/NO is case of success/failure
    if (!error) {
        /* Execute observers block */
        for (MoodleResourceObserver* observer in self.resourcesObserversForResourceKey[[self keyForMoodleResource:moodleResource]]) {
            if (observer.observer && observer.eventBlock) {
                observer.eventBlock(MoodleResourceEventDeleted);
            }
        }
        return YES;
    }
    return NO;
}

- (BOOL)deleteAllDownloadedResources {
    NSArray* cachePathArray = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES);
    NSString* path = [[cachePathArray lastObject] stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    path = [path stringByAppendingPathComponent:@"moodle"];
    path = [path stringByAppendingPathComponent:@"downloads"];
    NSFileManager* fileManager= [NSFileManager defaultManager];
    NSError* error = nil;
    [fileManager removeItemAtPath:path error:&error];
    if (!error) {
        /* Execute observers block */
        [self.resourcesObserversForResourceKey enumerateKeysAndObjectsUsingBlock:^(id key, NSMutableSet* observers, BOOL *stop) {
            for (MoodleResourceObserver* observer in observers) {
                if (observer.observer && observer.eventBlock) {
                    observer.eventBlock(MoodleResourceEventDeleted);
                }
            }
        }];
        return YES;
    }
    return NO;
}

#pragma mark - Service methods

- (void)getTequilaTokenForMoodleDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMoodle);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMoodleDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMoodleFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMoodleSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCoursesList:);
    operation.delegateDidReturnSelector = @selector(getCoursesList:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getEventsList:);
    operation.delegateDidReturnSelector = @selector(getEventsList:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventsListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstanceWithCustomTimeoutInterval:90.0] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCourseSections:);
    operation.delegateDidReturnSelector = @selector(getCourseSections:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseSectionsFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Saved elements

static NSString* const kCourseListReplyKey = @"courseListReply";
static NSString* const kSectionsListReplyForCourseIdWithFormat = @"sectionsListReply-%d";

- (NSString*)keyForSectionsListReplyForCourse:(MoodleCourse*)course {
    return [NSString stringWithFormat:kSectionsListReplyForCourseIdWithFormat, course.iId];
}

- (CoursesListReply*)getFromCacheCourseListReply {
    return (CoursesListReply*)[PCObjectArchiver objectForKey:kCourseListReplyKey andPluginName:@"moodle" isCache:YES];
}

- (BOOL)saveToCacheCourseListReply:(CoursesListReply*)courseListReply {
    return [PCObjectArchiver saveObject:courseListReply forKey:kCourseListReplyKey andPluginName:@"moodle" isCache:YES];
}

- (SectionsListReply*)getFromCacheSectionsListReplyForCourse:(MoodleCourse*)course {
    return (SectionsListReply*)[PCObjectArchiver objectForKey:[self keyForSectionsListReplyForCourse:course] andPluginName:@"moodle" isCache:YES];
}

- (BOOL)saveToCacheSectionsListReply:(SectionsListReply*)sectionsListReply forCourse:(MoodleCourse*)course {
    return [PCObjectArchiver saveObject:sectionsListReply forKey:[self keyForSectionsListReplyForCourse:course] andPluginName:@"moodle" isCache:YES];
}

#pragma mark - MoodleResources observation

- (NSString*)keyForMoodleResource:(MoodleResource*)resource {
    return [NSString stringWithFormat:@"%ud", (unsigned int)[resource.iUrl hash]];
}

- (void)addMoodleResourceObserver:(id)observer_ forResource:(MoodleResource*)resource eventBlock:(MoodleResourceEventBlock)eventBlock {
    @synchronized(self) {
        NSString* key = [self keyForMoodleResource:resource];
        
        if (!self.resourcesObserversForResourceKey) {
            self.resourcesObserversForResourceKey = [NSMutableDictionary dictionary];
        }
        
        NSMutableSet* currentObservers = self.resourcesObserversForResourceKey[key];
        if (!currentObservers) {
            currentObservers = [NSMutableSet set];
            self.resourcesObserversForResourceKey[key] = currentObservers;
        }
        
        MoodleResourceObserver* observer = [[MoodleResourceObserver alloc] init];
        observer.observer = observer_;
        observer.resource = resource;
        observer.eventBlock = eventBlock;
        
        [currentObservers addObject:observer];
    }
}

- (void)removeMoodleResourceObserver:(id)observer {
    @synchronized (self) {
        [[self.resourcesObserversForResourceKey copy] enumerateKeysAndObjectsUsingBlock:^(id key, NSMutableSet* observers, BOOL *stop) {
            for (MoodleResourceObserver* resourceObserver in [observers copy]) {
                if (resourceObserver.observer == observer) {
                    [observers removeObject:resourceObserver];
                    if (observers.count == 0) {
                        [self.resourcesObserversForResourceKey removeObjectForKey:key];
                    }
                }
            }
        }];
    }
}

- (void)removeMoodleResourceObserver:(id)observer forResource:(MoodleResource*)resource {
    @synchronized (self) {
        NSString* key = [self keyForMoodleResource:resource];
        NSMutableSet* observers = self.resourcesObserversForResourceKey[key];
        for (MoodleResourceObserver* resourceObserver in [observers copy]) {
            if (resourceObserver.observer == observer) {
                [observers removeObject:resourceObserver];
                if (observers.count == 0) {
                    [self.resourcesObserversForResourceKey removeObjectForKey:key];
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

- (void)downloadMoodleResource:(MoodleResource*)moodleResource progressView:(UIProgressView*)progressView delegate:(id)delegate {
    __weak __typeof(delegate) weakDelegate = delegate;
    NSString* localPath = [self localPathForMoodleResource:moodleResource createIntermediateDirectories:YES];
    NSURL* localURL = [NSURL fileURLWithPath:localPath];
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:moodleResource.iUrl] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:kFetchMoodleResourceTimeoutSeconds];
    [request addValue:[self lastSession].moodleCookie forHTTPHeaderField:@"Cookie"];
    NSURLSessionDownloadTask* downloadTask = [self.resourcesDownloadSessionManager downloadTaskWithRequest:request progress:nil destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
        return localURL;
    } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
        if (error.code == NSURLErrorCancelled) {
            //should not call delegate when cancelled
            return;
        }
        void (^failedBlock)(NSInteger) = ^void (NSInteger statusCode) {
            [[NSFileManager defaultManager] removeItemAtPath:localPath error:nil]; //to be sure not empty/wrong file is there
            if ([weakDelegate respondsToSelector:@selector(downloadFailedForMoodleResource:responseStatusCode:)]) {
                [weakDelegate downloadFailedForMoodleResource:moodleResource responseStatusCode:(int)statusCode];
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
                    if ([weakDelegate respondsToSelector:@selector(downloadOfMoodleResource:didFinish:)]) {
                        [weakDelegate downloadOfMoodleResource:moodleResource didFinish:localURL];
                    }
                    // Execute observers block
                    for (MoodleResourceObserver* observer in self.resourcesObserversForResourceKey[[self keyForMoodleResource:moodleResource]]) {
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

- (void)cancelDownloadOfMoodleResourceForDelegate:(id)delegate {
    [self.resourcesDownloadSessionManager.tasks enumerateObjectsUsingBlock:^(NSURLSessionTask* task, NSUInteger index, BOOL *stop) {
        if ([task.taskDescription isEqualToString:[NSString stringWithFormat:@"%p", delegate]]) {
            [task cancel];
        }
    }];
}

#pragma mark - Service overrides

- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    [super cancelOperationsForDelegate:delegate];
    [self cancelDownloadOfMoodleResourceForDelegate:delegate];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end