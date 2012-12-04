
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

#pragma mark -
                              
+ (NSString*)fileTypeForURL:(NSString*)urlString {
    NSString* ext = [urlString pathExtension];
    if (ext) {
        return [ext uppercaseString];
    }
    return @"";
}

+ (NSString*)localPathForURL:(NSString*)urlString {
    return [self localPathForURL:urlString createIntermediateDirectories:NO];
}

+ (NSString*)localPathForURL:(NSString*)urlString createIntermediateDirectories:(BOOL)createIntermediateDirectories {
    if (![urlString isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad urlString argument" reason:@"urlString is not kind of class NSString" userInfo:nil];
    }
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

+ (BOOL)isFileCached:(NSString*)localPath {
    return [[NSFileManager defaultManager] fileExistsAtPath:localPath];
}

+ (BOOL)deleteFileAtPath:(NSString*)localPath {
    return [[NSFileManager defaultManager] removeItemAtPath:localPath error:nil]; //OK to pass nil for error, method returns aleary YES/NO is case of success/failure
}

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

- (CoursesListReply*)getFromCacheCoursesListForRequest:(MoodleRequest*)moodleRequest {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getCoursesList:);
    operation.delegateDidReturnSelector = @selector(getCoursesList:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesListFailed:);
    [operation addObjectArgument:moodleRequest];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
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

- (SectionsListReply*)getFromCacheCourseSectionsForRequest:(MoodleRequest*)moodleRequest {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getCourseSections:);
    operation.delegateDidReturnSelector = @selector(getCourseSections:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseSectionsFailed:);
    [operation addObjectArgument:moodleRequest];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)fetchMoodleResourceWithURL:(NSString*)url cookie:(NSString*)cookie delegate:(id)delegate {    
    NSURL *nsurl = [NSURL URLWithString:url];
    ASIHTTPRequest *request = [[ASIHTTPRequest alloc] initWithURL:nsurl];
    
    NSString* filePath = [[self class] localPathForURL:url createIntermediateDirectories:YES];
    [request setDownloadDestinationPath:filePath];
    [request addRequestHeader:@"Cookie" value:cookie];
    [request setTimeOutSeconds:kFetchMoodleResourceTimeoutSeconds];
    [request setDelegate:delegate];
    [request setDidFinishSelector:@selector(fetchMoodleResourceDidReturn:)];
    [request setDidFailSelector:@selector(fetchMoodleResourceFailed:)];
    [operationQueue addOperation:request];
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end