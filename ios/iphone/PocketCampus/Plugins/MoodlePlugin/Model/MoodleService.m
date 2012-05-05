
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
            [instance setThriftClient:[[[MoodleServiceClient alloc] initWithProtocol:instance.thriftProtocol] autorelease]];
            
            instance.moodleCookie = (NSString*) [[NSUserDefaults standardUserDefaults] objectForKey:@"moodleCookie"];
        }
    }
    return [instance autorelease];
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

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getCoursesList:);
    operation.delegateDidReturnSelector = @selector(getCoursesList:DidReturn:);
    operation.delegateDidFailSelector = @selector(getCoursesListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getEventsList:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getEventsList:);
    operation.delegateDidReturnSelector = @selector(getEventsList:DidReturn:);
    operation.delegateDidFailSelector = @selector(getEventsListFailed:);
    [operation addObjectArgument:aMoodleRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getCourseSections:);
    operation.delegateDidReturnSelector = @selector(getCourseSections:DidReturn:);
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