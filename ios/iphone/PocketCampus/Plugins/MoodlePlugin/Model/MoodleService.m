
#import "MoodleService.h"

@implementation MoodleService

@synthesize moodleCookie;
//@synthesize value;

static MoodleService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"moodle"];            
            instance.moodleCookie = (NSString*) [[NSUserDefaults standardUserDefaults] objectForKey:@"moodleCookie"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[MoodleServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (NSString*)getLocalPath:(NSString*)url {
    NSRange nsr = [url rangeOfString:@"/file.php/"];
    NSString* nss = [url substringFromIndex:(nsr.location + nsr.length)];
    NSString *documentsDirectory = [NSHomeDirectory()  stringByAppendingPathComponent:@"Documents/moodle"];
    NSString *filePath = [documentsDirectory   stringByAppendingPathComponent:nss];
    
    /*NSRange nsr = [url rangeOfString:@"/" options:NSBackwardsSearch];
    NSString* nss = [url substringFromIndex:(nsr.location + nsr.length)];
    NSString *documentsDirectory = [NSHomeDirectory()  stringByAppendingPathComponent:@"Documents"];
    NSString *filePath = [documentsDirectory   stringByAppendingPathComponent:nss];*/
    
    NSString* directory = [filePath substringToIndex:[filePath rangeOfString:@"/" options:NSBackwardsSearch].location];
    BOOL isDir = TRUE;
    NSFileManager *fileManager= [NSFileManager defaultManager]; 
    if(![fileManager fileExistsAtPath:directory isDirectory:&isDir])
        if(![fileManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:NULL])
            NSLog(@"Error: Create folder failed %@", directory);
    
    return filePath;
}

- (void)getTequilaTokenForMoodleDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMoodle);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMoodleDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMoodleFailed);
    //[operation addIntArgument:service];
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
    operation.keepInCache = YES;
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
    //ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    
    NSURL *nsurl = [NSURL URLWithString:url];
    ASIHTTPRequest *request = [[[ASIHTTPRequest alloc] initWithURL:nsurl] autorelease];
    
    NSString* filePath = [self getLocalPath:url];
    [request setDownloadDestinationPath:filePath];
    
    [request addRequestHeader:@"Cookie" value:cookie];
    
    [request setDelegate:delegate];
    //request.shouldRedirect = NO;
    [request setDidFinishSelector:@selector(fetchMoodleResourceDidReturn:)];
    [request setDidFailSelector:@selector(fetchMoodleResourceFailed:)];
    [operationQueue addOperation:request];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end