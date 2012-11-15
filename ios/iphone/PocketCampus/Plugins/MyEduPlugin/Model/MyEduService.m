//
//  MyEduService.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduService.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

@interface MyEduMaterialData ()

@property (strong) MyEduMaterial* material;
@property (strong) NSURL* localURL;
@property (strong) NSData* data;
@property (copy) NSString* mimeType;
@property (copy) NSString* textEncoding;

@end

@implementation MyEduMaterialData

- (id)initWithCoder:(NSCoder *)decoder {
    self = [super init];
    if (self) {
        self.material = [decoder decodeObjectForKey:@"material"];
        self.localURL = [decoder decodeObjectForKey:@"localURL"];
        self.data = [decoder decodeObjectForKey:@"data"];
        self.mimeType = [decoder decodeObjectForKey:@"mimeType"];
        self.textEncoding = [decoder decodeObjectForKey:@"textEncoding"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)encoder {
    [encoder encodeObject:self.material forKey:@"material"];
    [encoder encodeObject:self.localURL forKey:@"localURL"];
    [encoder encodeObject:self.data forKey:@"data"];
    [encoder encodeObject:self.mimeType forKey:@"mimeType"];
    [encoder encodeObject:self.textEncoding forKey:@"textEncoding"];
}

@end

@interface MyEduService ()

@property (strong) MyEduSession* session;

@end

static NSString* kMyEduSessionIdentifier = @"myEduSession";

static NSString* kServiceDelegateKey = @"ServiceDelegate";
static NSString* kMaterialKey = @"Material";


static NSString* kVimeoEmbedHTMLFormat = @"<iframe src=\"http://player.vimeo.com/video/%@\" width=\"%d\" height=\"%d\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>";

@implementation MyEduService

static MyEduService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"myedu"];
        }
    }
    return instance;
}

- (id)thriftServiceClientInstance {
    return [[MyEduServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
}

- (MyEduRequest*)createMyEduRequest {
    return [[MyEduRequest alloc] initWithIMyEduSession:[self lastSession] iLanguage:[PCUtils userLanguageCode]];
}

- (MyEduSession*)lastSession {
    if (self.session) {
        return self.session;
    }
    return (MyEduSession*)[ObjectArchiver objectForKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (BOOL)saveSession:(MyEduSession*)session {
    self.session = session;
    return [ObjectArchiver saveObject:session forKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (BOOL)deleteSession {
    self.session = nil;
    return [ObjectArchiver saveObject:nil forKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMyEdu);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMyEduDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMyEduFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMyEduSession:);
    operation.delegateDidReturnSelector = @selector(getMyEduSessionForTequilaToken:didReturn:);
    operation.delegateDidFailSelector = @selector(getMyEduSessionFailedForTequilaToken:);
    [operation addObjectArgument:tequilaToken];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSubscribedCoursesList:);
    operation.delegateDidReturnSelector = @selector(getSubscribedCoursesListForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSubscribedCoursesListFailedForRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduSubscribedCoursesListReply*)getFromCacheSubscribedCoursesListForRequest:(MyEduRequest*)request {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getSubscribedCoursesList:);
    operation.delegateDidReturnSelector = @selector(getSubscribedCoursesListForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSubscribedCoursesListFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCourseDetails:);
    operation.delegateDidReturnSelector = @selector(getCourseDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseDetailsFailedForRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduCourseDetailsReply*)getFromCacheCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getCourseDetails:);
    operation.delegateDidReturnSelector = @selector(getCourseDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseDetailsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSectionDetails:);
    operation.delegateDidReturnSelector = @selector(getSectionDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionDetailsFailedForRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduCourseDetailsReply*)getFromCacheSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getSectionDetails:);
    operation.delegateDidReturnSelector = @selector(getSectionDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionDetailsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getModuleDetails:);
    operation.delegateDidReturnSelector = @selector(getModuleDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getModuleDetailsFailedForRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduModuleDetailsReply*)getFromCacheModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getModuleDetails:);
    operation.delegateDidReturnSelector = @selector(getModuleDetailsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getModuleDetailsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)submitFeedbackWithRequest:(MyEduSubmitFeedbackRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(submitFeedback:);
    operation.delegateDidReturnSelector = @selector(submitFeedbackForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(submitFeedbackFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

/* Not from Thrift */

- (NSString*)keyForMaterial:(MyEduMaterial*)material {
    NSString* key = [NSString stringWithFormat:@"%u", [material.iURL hash]];
    return key;
}

- (void)downloadMaterial:(MyEduMaterial*)material progressView:(UIProgressView*)progressView delegate:(id)delegate {
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:material.iURL]];
    NSMutableDictionary* infos = [NSMutableDictionary dictionary];
    [infos setObject:delegate forKey:kServiceDelegateKey];
    [infos setObject:material forKey:kMaterialKey];
    request.userInfo = infos;
    request.delegate = self;
    request.didFinishSelector = @selector(downloadMaterialRequestFinished:);
    request.didFailSelector = @selector(downloadMaterialRequestFailed:);
    request.downloadProgressDelegate = progressView;
    request.showAccurateProgress = YES;
    request.shouldRedirect = NO;
    [request addRequestHeader:@"Cookie" value:[self createMyEduRequest].iMyEduSession.iMyEduCookie];
    [operationQueue addOperation:request];
}

- (MyEduMaterialData*)materialDataIfExistsForMaterial:(MyEduMaterial*)material {
    return (MyEduMaterialData*)[ObjectArchiver objectForKey:[self keyForMaterial:material] andPluginName:@"myedu"];
}

#pragma mark - ASIHTTRequestDelegate

- (void)downloadMaterialRequestFinished:(ASIHTTPRequest *)request {
    id<MyEduServiceDelegate> delegate = [request.userInfo objectForKey:kServiceDelegateKey];
    MyEduMaterial* material = [request.userInfo objectForKey:kMaterialKey];
    
    if (request.responseStatusCode != 200 && [delegate respondsToSelector:@selector(downloadFailedForMaterial:responseStatusCode:)]) {
        [delegate downloadFailedForMaterial:material responseStatusCode:request.responseStatusCode];
        return;
    }
    
    MyEduMaterialData* materialData = [[MyEduMaterialData alloc] init];
    materialData.material = material;
    materialData.localURL = [NSURL fileURLWithPath:[ObjectArchiver pathForKey:[self keyForMaterial:material] pluginName:@"myedu"]];
    materialData.data = request.responseData;
    NSString* contentType = [request.responseHeaders objectForKey:@"Content-Type"];
    NSArray* parts = [contentType componentsSeparatedByString:@";"];
    NSString* mimeType = parts[0]; //if contentType does not contain ";" NSString API assures that parts contains at least string itself as first element
    materialData.mimeType = mimeType;
    materialData.textEncoding = (__bridge NSString *)(CFStringGetNameOfEncoding(CFStringConvertNSStringEncodingToEncoding(request.responseEncoding)));
    
    if ([delegate respondsToSelector:@selector(downloadOfMaterial:didFinish:)]) {
        [delegate downloadOfMaterial:material didFinish:materialData];
    }
    
    [ObjectArchiver saveObject:materialData forKey:[self keyForMaterial:material] andPluginName:@"myedu"];
}

- (void)downloadMaterialRequestFailed:(ASIHTTPRequest *)request {
    id<MyEduServiceDelegate> delegate = [request.userInfo objectForKey:kServiceDelegateKey];
    if ([delegate respondsToSelector:@selector(downloadFailedForMaterial:)]) {
        MyEduMaterial* material = [request.userInfo objectForKey:kMaterialKey];
        [delegate downloadFailedForMaterial:material responseStatusCode:request.responseStatusCode];
    }
}

#pragma mark - Utility methods

+ (NSString*)videoHTMLCodeForMyEduModule:(MyEduModule*)module videoWidth:(int)width videoHeight:(int)height {
    if ([[module.iVideoSourceProvider lowercaseString] isEqualToString:@"vimeo"]) {
        return [NSString stringWithFormat:kVimeoEmbedHTMLFormat, module.iVideoURL, width, height];
    } else {
        NSLog(@"!! Unsupported video provider");
        //TODO
    }
    return nil;
}

- (void)dealloc
{
    [self cancelAllOperations];
    instance = nil;
}

@end
