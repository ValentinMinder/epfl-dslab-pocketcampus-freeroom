//
//  MyEduService.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduService.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

#pragma mark - MyEduMaterialData implementation

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

#pragma mark - MyEduDownloadObserver implementation

@implementation MyEduDownloadObserver

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMyEduDownloadObserver:object];
}

- (BOOL)isEqualToMyEduDownloadObserver:(MyEduDownloadObserver*)downloadObserver {
    return self.observer == downloadObserver.observer && [self.downloadIdentifier isEqual:downloadObserver.downloadIdentifier];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.observer hash];
    hash += [self.downloadIdentifier hash];
    return hash;
}

@end

#pragma mark - MyEduService implementation

@interface MyEduService ()

@property (strong) MyEduSession* session;
@property (strong) NSMutableDictionary* downloadsObserversForVideoKey; //key: [self keyForVideoOfModule:] value: NSArray of MyEduDowloadObserver

@end

static NSString* kMyEduSessionIdentifier = @"myEduSession";

static NSString* kServiceDelegateKey = @"ServiceDelegate";
static NSString* kMaterialKey = @"Material";
static NSString* kVideoKey = @"Video";


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

#pragma mark - Session

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

#pragma mark - Thrift service

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


#pragma mark - Utilities

- (NSString*)keyForMaterial:(MyEduMaterial*)material {
    NSString* key = [NSString stringWithFormat:@"material-%u", [material.iURL hash]];
    return key;
}

- (NSString*)keyForVideoOfModule:(MyEduModule*)module {
    NSString* key = [NSString stringWithFormat:@"video-%d-%@", module.iId, module.iVideoID];
    return key;
}

- (NSString*)localPathForVideoOfModule:(MyEduModule*)module {
    return [self localPathOfVideoForModule:module nilIfNoFile:NO];
}

- (NSString*)localPathOfVideoForModule:(MyEduModule*)module nilIfNoFile:(BOOL)nilIfNoFile { //if onlyIfFileExists is YES, nil is returned if file does not exist
    NSString* filename = [self keyForVideoOfModule:module];
    NSString* fullPath = [ObjectArchiver pathForKey:filename pluginName:@"myedu" customFileExtension:@"mp4"];
    
    if (nilIfNoFile) {
        NSFileManager* fileManager = [NSFileManager defaultManager];
        NSError* error = nil;
        NSDictionary* fileAttributes = [fileManager attributesOfItemAtPath:fullPath error:&error];
        if (error) {
            return nil;
        }
        unsigned long long fileSize = [fileAttributes[NSFileSize] unsignedLongLongValue];
        if (fileSize == 0) { //empty file, likely because killed download request
            return nil;
        }
    }
    
    return fullPath;
}

+ (NSString*)videoHTMLCodeForMyEduModule:(MyEduModule*)module videoWidth:(int)width videoHeight:(int)height {
    if ([[module.iVideoSourceProvider lowercaseString] isEqualToString:@"vimeo"]) {
        return [NSString stringWithFormat:kVimeoEmbedHTMLFormat, module.iVideoID, width, height];
    } else {
        NSLog(@"!! Unsupported video provider");
        //TODO
    }
    return nil;
}

#pragma mark - Material Download

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


#pragma mark - Video download

- (void)addDownloadObserver:(id)observer forVideoOfModule:(MyEduModule*)module startDownload:(BOOL)startDownload finishBlock:(DownloadDidFinishBlock)finishBlock progressBlock:(DownloadDidProgressBlock)progressBlock cancelledBlock:(DownloadWasCancelledBlock)cancelledBlock failureBlock:(DownloadDidFailBlock)failureBlock {
    @synchronized(self) {
        MyEduDownloadObserver* downloadObserver = [[MyEduDownloadObserver alloc] init];
        downloadObserver.observer = observer;
        NSString* key = [self keyForVideoOfModule:module];
        downloadObserver.downloadIdentifier = key;
        downloadObserver.finishBlock = finishBlock;
        downloadObserver.progressBlock = progressBlock;
        downloadObserver.cancelledBlock = cancelledBlock;
        downloadObserver.failureBlock = failureBlock;
        
        if (!self.downloadsObserversForVideoKey) {
            self.downloadsObserversForVideoKey = [NSMutableDictionary dictionary];
        }
        
        NSMutableArray* currentObservers = self.downloadsObserversForVideoKey[key];
        if (!currentObservers) { //start video download
            currentObservers = [NSMutableArray array];
            self.downloadsObserversForVideoKey[key] = currentObservers;
        }
        
        [currentObservers addObject:downloadObserver];
        
        if (startDownload) {
            BOOL shouldCreateRequest __block = YES;
            [self.operationQueue.operations enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
                if ([obj isKindOfClass:[ASIHTTPRequest class]]) {
                    ASIHTTPRequest* request = (ASIHTTPRequest*)obj;
                    if ([request.userInfo[kVideoKey] isEqualToString:key]) {
                        shouldCreateRequest = NO; //download is already in progress
                        *stop = YES;
                    }
                }
            }];
            
            if (shouldCreateRequest) {
                ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:module.iVideoDownloadURL]];
                request.userInfo = [NSDictionary dictionaryWithObject:key forKey:kVideoKey];
                request.delegate = self;
                request.downloadProgressDelegate = self;
                request.showAccurateProgress = YES;
                request.shouldRedirect = YES;
                request.downloadDestinationPath = [self localPathForVideoOfModule:module];
                request.didFinishSelector = @selector(downloadVideoRequestFinished:);
                request.didFailSelector = @selector(downloadVideoRequestFailed:);
                [operationQueue addOperation:request];
            }
        }
        
    }
}

- (void)removeDownloadObserver:(id)observer forVideoModule:(MyEduModule*)module {
    NSString* key = [self keyForVideoOfModule:module];
    NSMutableArray* observers = self.downloadsObserversForVideoKey[key];
    [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
        if (downloadObserver.observer == observer && [downloadObserver.downloadIdentifier isEqual:key]) {
            [observers removeObject:downloadObserver];
        }
    }];
}

- (void)cancelVideoDownloadForModule:(MyEduModule*)module {
    NSString* key = [self keyForVideoOfModule:module];
    BOOL didCancelDownload __block = NO;
    
    [self.operationQueue.operations enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        if ([obj isKindOfClass:[ASIHTTPRequest class]]) {
            ASIHTTPRequest* request = (ASIHTTPRequest*)obj;
            if ([request.userInfo[kVideoKey] isEqualToString:key]) {
                [request cancel];
                request.delegate = nil;
                request.downloadProgressDelegate = nil;
                [self removeDownloadedVideoOfModule:module];
                *stop = YES;
                didCancelDownload = YES;
            }
        }
    }];
    
    if (didCancelDownload) {
        NSArray* observers = self.downloadsObserversForVideoKey[key];
        @synchronized(self) {
            [observers enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
                downloadObserver.cancelledBlock();
            }];
        }
    }
}

- (void)removeDownloadedVideoOfModule:(MyEduModule*)module {
    [[NSFileManager defaultManager] removeItemAtPath:[self localPathForVideoOfModule:module] error:NULL];
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

- (void)downloadVideoRequestFinished:(ASIHTTPRequest *)request {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        [observers enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            downloadObserver.finishBlock([NSURL fileURLWithPath:request.downloadDestinationPath]);
        }];
    }
}

- (void)downloadVideoRequestFailed:(ASIHTTPRequest *)request {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        @synchronized (self) {
            [observers enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
                downloadObserver.failureBlock(request.responseStatusCode);
            }];
        }
    }
}

#pragma mark - ASIProgressDelegate

- (void)request:(ASIHTTPRequest *)request didReceiveBytes:(long long)bytes {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        @synchronized (self) {
            [observers enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
                if (request.contentLength == 0) {
                    downloadObserver.progressBlock(0, 0, 0.0);
                } else {
                    downloadObserver.progressBlock(request.totalBytesRead, request.contentLength, request.totalBytesRead/(double)request.contentLength);
                }
            }];
        }
    }
}

- (void)request:(ASIHTTPRequest *)request incrementDownloadSizeBy:(long long)newLength {
    [self request:request didReceiveBytes:1]; //number of bytes is not important, as not used in request:didReceiveBytes:
}

#pragma mark - dealloc

- (void)dealloc
{
    instance = nil;
}

@end
