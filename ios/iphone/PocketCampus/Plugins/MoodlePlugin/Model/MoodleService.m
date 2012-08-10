
#import "MoodleService.h"

#import "ObjectArchiver.h"

@implementation MoodleService

static MoodleService* instance = nil;

static int kFetchMoodleResourceTimeoutSeconds = 120; //must be long to download heavy PDFs on 3G

static NSString* kMoodleCookieKey = @"moodleCookie";

@synthesize moodleCookie;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"moodle"];            
            instance.moodleCookie = (NSString*)[ObjectArchiver objectForKey:kMoodleCookieKey andPluginName:@"moodle"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[MoodleServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (BOOL)saveMoodleCookie:(NSString*)moodleCookie_ {
    self.moodleCookie = moodleCookie_; //previous cookie is released by synthesized setter
    return [ObjectArchiver saveObject:moodleCookie_ forKey:kMoodleCookieKey andPluginName:@"moodle"];
}

+ (NSString*)fileTypeForURL:(NSString*)urlString {
    NSString* ext = [urlString pathExtension];
    if (ext) {
        return [ext uppercaseString];
    }
    return @"";
}

- (NSString*)localPathForURL:(NSString*)urlString {
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
    NSArray* cachePathArray = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString* cachePath = [[cachePathArray lastObject] stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    NSString* cacheMoodlePath = [cachePath stringByAppendingPathComponent:@"Moodle"];
    NSString* filePath = [cacheMoodlePath stringByAppendingPathComponent:nss];

    NSString* directory = [filePath substringToIndex:[filePath rangeOfString:@"/" options:NSBackwardsSearch].location];
    BOOL isDir = TRUE;
    NSFileManager *fileManager= [NSFileManager defaultManager]; 
    if(![fileManager fileExistsAtPath:directory isDirectory:&isDir]) {
        if(![fileManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:NULL]) {
            NSLog(@"-> Error while creating directory in cache : %@", directory);
        }
    }
    return filePath;
}

- (void)getTequilaTokenForMoodleDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMoodle);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMoodleDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMoodleFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMoodleSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCoursesList:);
    operation.delegateDidReturnSelector = @selector(getCoursesList:didReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getEventsList:);
    operation.delegateDidReturnSelector = @selector(getEventsList:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventsListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidity = 10*60.0; // 10 minutes
    operation.customTimeout = 35.0; // might take time
    operation.serviceClientSelector = @selector(getCourseSections:);
    operation.delegateDidReturnSelector = @selector(getCourseSections:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseSectionsFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)fetchMoodleResource:(NSString*)cookie :(NSString*)url withDelegate:(id)delegate {    
    NSURL *nsurl = [NSURL URLWithString:url];
    ASIHTTPRequest *request = [[ASIHTTPRequest alloc] initWithURL:nsurl];
    
    NSString* filePath = [self localPathForURL:url];
    [request setDownloadDestinationPath:filePath];
    
    [request addRequestHeader:@"Cookie" value:cookie];
    [request setTimeOutSeconds:kFetchMoodleResourceTimeoutSeconds];
    [request setDelegate:delegate];
    //request.shouldRedirect = NO;
    [request setDidFinishSelector:@selector(fetchMoodleResourceDidReturn:)];
    [request setDidFailSelector:@selector(fetchMoodleResourceFailed:)];
    [operationQueue addOperation:request];
    [request release];
}

- (void)dealloc
{
    [moodleCookie release];
    instance = nil;
    [super dealloc];
}

@end