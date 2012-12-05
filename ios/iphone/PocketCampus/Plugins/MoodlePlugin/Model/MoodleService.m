
#import "MoodleService.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

@interface MoodleService ()

@property (nonatomic, strong) MoodleSession* session;

@end

@implementation MoodleService

static MoodleService* instance __weak = nil;

static int kFetchMoodleResourceTimeoutSeconds = 20;

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
    return [[NSFileManager defaultManager] removeItemAtPath:[self localPathForMoodleResource:moodleResource] error:nil]; //OK to pass nil for error, method returns aleary YES/NO is case of success/failure
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
    operation.cacheValidity = 1209600; // seconds == 2 weeks
    operation.keepInCache = YES;
    operation.skipCache = YES;
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
    operation.keepInCache = YES;
    operation.skipCache = YES;
    operation.cacheValidity = 10*60.0; // 10 minutes
    operation.customTimeout = 60.0; // might take time
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
    NSLog(@"%@", request.userInfo);
    id<MoodleServiceDelegate> delegate_ = request.userInfo[kServiceDelegateKey];
    MoodleResource* moodleResource = request.userInfo[kMoodleResourceKey];
    if (request.responseStatusCode != 200) {
        [self downloadMoodleResourceFailed:request];
        return;
    }
    NSURL* fileLocalURL = [NSURL fileURLWithPath:request.downloadDestinationPath];
    if ([delegate_ respondsToSelector:@selector(downloadOfMoodleResource:didFinish:)]) {
        [delegate_ downloadOfMoodleResource:moodleResource didFinish:fileLocalURL];
    }
}

- (void)downloadMoodleResourceFailed:(ASIHTTPRequest*)request {
    id<MoodleServiceDelegate> delegate = request.userInfo[kServiceDelegateKey];
    MoodleResource* moodleResource = request.userInfo[kMoodleResourceKey];
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