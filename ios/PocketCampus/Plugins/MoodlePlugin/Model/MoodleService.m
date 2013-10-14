
#import "MoodleService.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

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

#pragma mark - MoodleService implementation

@interface MoodleService ()

@property (nonatomic, strong) MoodleSession* session;
@property (strong) NSMutableDictionary* resourcesObserversForResourceKey; //key: [self keyForMoodleResource:] value: NSArray of MoodleResourceObserver

@end

@implementation MoodleService

static MoodleService* instance __weak = nil;

static int kFetchMoodleResourceTimeoutSeconds = 30;

static NSString* kMoodleSessionKey = @"moodleSession";
static NSString* kServiceDelegateKey = @"serviceDelegate";
static NSString* kMoodleResourceKey = @"moodleResource";

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MoodleService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"moodle"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

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

- (id)thriftServiceClientInstance {
    return [[MoodleServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
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
    self.session = (MoodleSession*)[ObjectArchiver objectForKey:kMoodleSessionKey andPluginName:@"moodle"];
    return self.session;
}

- (BOOL)saveSession:(MoodleSession*)session {
    self.session = session;
    return [ObjectArchiver saveObject:session forKey:kMoodleSessionKey andPluginName:@"moodle"];
}

- (BOOL)deleteSession {
    self.session = nil;
    return [ObjectArchiver saveObject:nil forKey:kMoodleSessionKey andPluginName:@"moodle"];
}

#pragma mark - Resources files management

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
    BOOL success = [[NSFileManager defaultManager] removeItemAtPath:[self localPathForMoodleResource:moodleResource] error:nil]; //OK to pass nil for error, method returns aleary YES/NO is case of success/failure
    if (success) {
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
    [operationQueue addOperation:operation];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMoodleSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCoursesList:);
    operation.delegateDidReturnSelector = @selector(getCoursesList:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getEventsList:);
    operation.delegateDidReturnSelector = @selector(getEventsList:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventsListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.customTimeout = 90.0; // might take time
    operation.serviceClientSelector = @selector(getCourseSections:);
    operation.delegateDidReturnSelector = @selector(getCourseSections:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseSectionsFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

#pragma mark - Saved elements

static NSString* kCourseListReplyKey = @"courseListReply";
static NSString* kSectionsListReplyForCourseIdWithFormat = @"sectionsListReply-%d";

- (NSString*)keyForSectionsListReplyForCourse:(MoodleCourse*)course {
    return [NSString stringWithFormat:kSectionsListReplyForCourseIdWithFormat, course.iId];
}

- (CoursesListReply*)getFromCacheCourseListReply {
    return (CoursesListReply*)[ObjectArchiver objectForKey:kCourseListReplyKey andPluginName:@"moodle" isCache:YES];
}

- (BOOL)saveToCacheCourseListReply:(CoursesListReply*)courseListReply {
    return [ObjectArchiver saveObject:courseListReply forKey:kCourseListReplyKey andPluginName:@"moodle" isCache:YES];
}

- (SectionsListReply*)getFromCacheSectionsListReplyForCourse:(MoodleCourse*)course {
    return (SectionsListReply*)[ObjectArchiver objectForKey:[self keyForSectionsListReplyForCourse:course] andPluginName:@"moodle" isCache:YES];
}

- (BOOL)saveToCacheSectionsListReply:(SectionsListReply*)sectionsListReply forCourse:(MoodleCourse*)course {
    return [ObjectArchiver saveObject:sectionsListReply forKey:[self keyForSectionsListReplyForCourse:course] andPluginName:@"moodle" isCache:YES];
}

#pragma mark - MoodleResources observation

- (NSString*)keyForMoodleResource:(MoodleResource*)resource {
    return [NSString stringWithFormat:@"%u", [resource.iUrl hash]];
}

- (void)addMoodleResourceObserver:(id)observer_ forResource:(MoodleResource*)resource eventBlock:(MoodleResourceEventBlock)eventBlock {
    @synchronized(self) {
        NSString* key = [self keyForMoodleResource:resource];
        
        if (!self.resourcesObserversForResourceKey) {
            self.resourcesObserversForResourceKey = [NSMutableDictionary dictionary];
        }
        
        NSMutableArray* currentObservers = self.resourcesObserversForResourceKey[key];
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

- (void)downloadMoodleResource:(MoodleResource*)moodleResource progressView:(UIProgressView*)progressView delegate:(id)delegate {
    NSURL *nsurl = [NSURL URLWithString:moodleResource.iUrl];
    ASIHTTPRequest *request = [[ASIHTTPRequest alloc] initWithURL:nsurl];
    NSString* filePath = [self localPathForMoodleResource:moodleResource createIntermediateDirectories:YES];
    [request setDownloadDestinationPath:filePath];
    [request addRequestHeader:@"Cookie" value:[self lastSession].moodleCookie];
    [request setTimeOutSeconds:kFetchMoodleResourceTimeoutSeconds];
    [request setDelegate:self];
    [request setDidFinishSelector:@selector(downloadMoodleResourceRequestDidFinish:)];
    [request setDidFailSelector:@selector(downloadMoodleResourceFailed:)];
    NSMutableDictionary* userInfo = [NSMutableDictionary dictionaryWithCapacity:2];
    userInfo[kServiceDelegateKey] = delegate;
    userInfo[kMoodleResourceKey] = moodleResource;
    request.userInfo = userInfo;
    request.showAccurateProgress = YES;
    request.downloadProgressDelegate = progressView;
    request.shouldRedirect = NO;
    request.useCookiePersistence = NO;
    request.cachePolicy = ASIDoNotReadFromCacheCachePolicy;
    [operationQueue addOperation:request];
}

- (void)cancelDownloadOfMoodleResourceForDelegate:(id)delegate {
    [[operationQueue.operations copy] enumerateObjectsUsingBlock:^(NSOperation* operation, NSUInteger idx, BOOL *stop) {
        if ([operation isKindOfClass:[ASIHTTPRequest class]]) {
            ASIHTTPRequest* request = (ASIHTTPRequest*)operation;
            id reqDelegate = request.userInfo[kServiceDelegateKey];
            if (delegate == reqDelegate) {
                [request cancel];
                request.delegate = nil;
            }
        }
    }];
}

#pragma mark - ASIHTTPRequestDelegate

- (void)downloadMoodleResourceRequestDidFinish:(ASIHTTPRequest*)request {
    id<MoodleServiceDelegate> delegate_ = request.userInfo[kServiceDelegateKey];
    MoodleResource* moodleResource = request.userInfo[kMoodleResourceKey];
    if (request.responseStatusCode != 200) {
        [self downloadMoodleResourceFailed:request];
        return;
    }
    /* Notify delegate */
    NSURL* fileLocalURL = [NSURL fileURLWithPath:request.downloadDestinationPath];
    if ([delegate_ respondsToSelector:@selector(downloadOfMoodleResource:didFinish:)]) {
        [delegate_ downloadOfMoodleResource:moodleResource didFinish:fileLocalURL];
    }
    /* Execute observers block */
    for (MoodleResourceObserver* observer in self.resourcesObserversForResourceKey[[self keyForMoodleResource:moodleResource]]) {
        if (observer.observer && observer.eventBlock) {
            observer.eventBlock(MoodleResourceEventDownloaded);
        }
    }
}

- (void)downloadMoodleResourceFailed:(ASIHTTPRequest*)request {
    id<MoodleServiceDelegate> delegate = request.userInfo[kServiceDelegateKey];
    MoodleResource* moodleResource = request.userInfo[kMoodleResourceKey];
    [[NSFileManager defaultManager] removeItemAtPath:request.downloadDestinationPath error:nil]; //to be sure not empty/wrong file is there
    if (request.responseStatusCode != 200 && [delegate respondsToSelector:@selector(downloadFailedForMoodleResource:responseStatusCode:)]) {
        [delegate downloadFailedForMoodleResource:moodleResource responseStatusCode:request.responseStatusCode];
        return;
    }
}

//override
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    [super cancelOperationsForDelegate:delegate];
    [self cancelDownloadOfMoodleResourceForDelegate:delegate];
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end