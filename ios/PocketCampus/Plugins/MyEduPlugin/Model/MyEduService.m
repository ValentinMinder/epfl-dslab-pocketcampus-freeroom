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

#pragma mark - MyEduModuleVideoDownloadObserver implementation

@implementation MyEduModuleVideoDownloadObserver

- (void)initWithModule:(MyEduModule*)module {
    self.downloadIdentifier = [MyEduService keyForVideoOfModule:module];
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

static MyEduService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MyEduService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"myedu"];
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
    self.session = (MyEduSession*)[ObjectArchiver objectForKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
    return self.session;
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

#pragma mark - playback time persistence

- (NSTimeInterval)lastPlaybackTimeForVideoForModule:(MyEduModule*)module {
    NSNumber* number = (NSNumber*)[ObjectArchiver objectForKey:[NSString stringWithFormat:@"lastPlayback-%@", [self.class keyForVideoOfModule:module]] andPluginName:@"myedu"];
    if (!number) {
        return 0.0;
    }
    return (NSTimeInterval)[number doubleValue];
}

- (BOOL)saveLastPlaybackTime:(NSTimeInterval)time forVideoOfModule:(MyEduModule*)module {
    return [ObjectArchiver saveObject:[NSNumber numberWithDouble:time] forKey:[NSString stringWithFormat:@"lastPlayback-%@", [self.class keyForVideoOfModule:module]] andPluginName:@"myedu"];
}

#pragma mark - Class utilities

+ (NSString*)keyForMaterial:(MyEduMaterial*)material {
    NSString* key = [NSString stringWithFormat:@"material-%u", [material.iURL hash]];
    return key;
}

+ (NSString*)keyForVideoOfModule:(MyEduModule*)module {
    NSString* key = [NSString stringWithFormat:@"video-%d-%@", module.iId, module.iVideoID];
    return key;
}

+ (NSString*)localPathForVideoOfModule:(MyEduModule*)module {
    return [self localPathOfVideoForModule:module nilIfNoFile:NO];
}

+ (NSString*)localPathOfVideoForModule:(MyEduModule*)module nilIfNoFile:(BOOL)nilIfNoFile { //if onlyIfFileExists is YES, nil is returned if file does not exist
    NSString* filename = [self keyForVideoOfModule:module];
    NSString* fullPath = [ObjectArchiver pathForKey:filename pluginName:@"myedu" customFileExtension:@"mp4" isCache:YES];
    
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
    request.timeOutSeconds = 5.0;
    [request addRequestHeader:@"Cookie" value:[self lastSession].iMyEduCookie];
    [operationQueue addOperation:request];
}

- (MyEduMaterialData*)materialDataIfExistsForMaterial:(MyEduMaterial*)material {
    return (MyEduMaterialData*)[ObjectArchiver objectForKey:[MyEduService keyForMaterial:material] andPluginName:@"myedu" isCache:YES];
}


#pragma mark - Video download

- (void)addDownloadObserver:(MyEduDownloadObserver*)downloadObserver forVideoOfModule:(MyEduModule*)module startDownload:(BOOL)startDownload {
    @synchronized(self) {
        
        NSString* key = [MyEduService keyForVideoOfModule:module];
        
        if (!self.downloadsObserversForVideoKey) {
            self.downloadsObserversForVideoKey = [NSMutableDictionary dictionary];
        }
        
        NSMutableSet* currentObservers = self.downloadsObserversForVideoKey[key];
        if (!currentObservers) { //start video download
            currentObservers = [NSMutableSet set];
            self.downloadsObserversForVideoKey[key] = currentObservers;
        }
        
        [currentObservers addObject:downloadObserver];
        
        /* should call startBlock on observer if:
         1. download starts now because observer asked for it (startDownload)
         2. download was already started previously is still in progress (all new observers are called with start)
        */
        if (startDownload) {
            BOOL downloadWasStarted = [self downloadVideoOfModule:module]; //startBlock called in method
            if (!downloadWasStarted) {
                if (downloadObserver.startBlock) {
                    downloadObserver.startBlock();
                }
            } 
        } else if ([self videoOfModuleIsDownloading:module]) {
            if (downloadObserver.startBlock) {
                downloadObserver.startBlock();
            }
        } else {
            //not calling startBlock
        }

    }

}

- (void)addDownloadObserver:(id)observer forVideoOfModule:(MyEduModule*)module startDownload:(BOOL)startDownload startBlock:(DownloadDidStartBlock)startBlock finishBlock:(DownloadDidFinishBlock)finishBlock progressBlock:(DownloadDidProgressBlock)progressBlock cancelledBlock:(DownloadWasCancelledBlock)cancelledBlock failureBlock:(DownloadDidFailBlock)failureBlock deletedBlock:(DownloadWasDeletedBlock)deletedBlock {
    
    MyEduDownloadObserver* downloadObserver = [[MyEduDownloadObserver alloc] init];
    downloadObserver.observer = observer;
    NSString* key = [MyEduService keyForVideoOfModule:module];
    downloadObserver.downloadIdentifier = key;
    downloadObserver.startBlock = startBlock;
    downloadObserver.finishBlock = finishBlock;
    downloadObserver.progressBlock = progressBlock;
    downloadObserver.cancelledBlock = cancelledBlock;
    downloadObserver.failureBlock = failureBlock;
    downloadObserver.deletedBlock = deletedBlock;
    [self addDownloadObserver:downloadObserver forVideoOfModule:module startDownload:startDownload];
}

- (void)removeDownloadObserver:(id)observer {
    @synchronized(self) {
        [[self.downloadsObserversForVideoKey copy] enumerateKeysAndObjectsUsingBlock:^(id key, NSMutableSet* observers, BOOL *stop) {
            for (MyEduDownloadObserver* downloadObserver in [observers copy]) {
                if (downloadObserver.observer == observer) {
                    [observers removeObject:downloadObserver];
                }
                if (observers.count == 0) {
                    [self.downloadsObserversForVideoKey removeObjectForKey:key];
                }
            }
        }];
    }
}

- (void)removeDownloadObserver:(id)observer forVideoModule:(MyEduModule*)module {
    @synchronized(self) {
        NSString* key = [MyEduService keyForVideoOfModule:module];
        NSMutableSet* observers = self.downloadsObserversForVideoKey[key];
        for (MyEduDownloadObserver* downloadObserver in [observers copy]) {
            if (downloadObserver.observer == observer && [downloadObserver.downloadIdentifier isEqualToString:key]) {
                [observers removeObject:downloadObserver];
            }
            if (observers.count == 0) {
                [self.downloadsObserversForVideoKey removeObjectForKey:key];
            }
        }
    }
}

- (BOOL)videoOfModuleIsDownloading:(MyEduModule*)module {
    BOOL isDownloading __block = NO;
    [[self.operationQueue.operations copy] enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        if ([obj isKindOfClass:[ASIHTTPRequest class]]) {
            ASIHTTPRequest* request = (ASIHTTPRequest*)obj;
            if ([request.userInfo[kVideoKey] isEqualToString:[MyEduService keyForVideoOfModule:module]]) {
                isDownloading = YES;
                *stop = YES;
            }
        }
    }];
    return isDownloading;
}

- (BOOL)downloadVideoOfModule:(MyEduModule*)module {
    
    if ([self videoOfModuleIsDownloading:module]) {
        return NO;
    }
    
    NSString* key = [MyEduService keyForVideoOfModule:module];
    
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:module.iVideoDownloadURL]];
    request.userInfo = [NSDictionary dictionaryWithObject:key forKey:kVideoKey];
    request.delegate = self;
    request.downloadProgressDelegate = self;
    request.showAccurateProgress = YES;
    request.shouldRedirect = YES;
    request.downloadDestinationPath = [MyEduService localPathForVideoOfModule:module];
    request.didFinishSelector = @selector(downloadVideoRequestFinished:);
    request.didFailSelector = @selector(downloadVideoRequestFailed:);
    [operationQueue addOperation:request];
    
    NSMutableArray* observers = self.downloadsObserversForVideoKey[key];
    [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
        if (downloadObserver.startBlock) {
            downloadObserver.startBlock();
        }
    }];
    
    return YES;
}

- (void)cancelVideoDownloadForModule:(MyEduModule*)module {
    NSString* key = [MyEduService keyForVideoOfModule:module];
    BOOL didCancelDownload __block = NO;
    
    [[self.operationQueue.operations copy] enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
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
        [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            if (downloadObserver.cancelledBlock) {
                downloadObserver.cancelledBlock();
            }
        }];
    }
}

- (void)removeDownloadedVideoOfModule:(MyEduModule*)module {
    NSError* error = nil;
    [[NSFileManager defaultManager] removeItemAtPath:[MyEduService localPathForVideoOfModule:module] error:&error];
    if (!error) {
        NSArray* observers = self.downloadsObserversForVideoKey[[MyEduService keyForVideoOfModule:module]];
        [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            if (downloadObserver.deletedBlock) {
                downloadObserver.deletedBlock();
            }
        }];
    }
}

#pragma mark - ASIHTTRequestDelegate

#pragma mark Material downloads callbacks

- (void)downloadMaterialRequestFinished:(ASIHTTPRequest *)request {
    id<MyEduServiceDelegate> delegate = [request.userInfo objectForKey:kServiceDelegateKey];
    MyEduMaterial* material = [request.userInfo objectForKey:kMaterialKey];
    
    if (request.responseStatusCode != 200 && [delegate respondsToSelector:@selector(downloadFailedForMaterial:responseStatusCode:)]) {
        [delegate downloadFailedForMaterial:material responseStatusCode:request.responseStatusCode];
        return;
    }
    
    MyEduMaterialData* materialData = [[MyEduMaterialData alloc] init];
    materialData.material = material;
    materialData.localURL = [NSURL fileURLWithPath:[ObjectArchiver pathForKey:[MyEduService keyForMaterial:material] pluginName:@"myedu" customFileExtension:nil isCache:YES]];
    materialData.data = request.responseData;
    NSString* contentType = [request.responseHeaders objectForKey:@"Content-Type"];
    NSArray* parts = [contentType componentsSeparatedByString:@";"];
    NSString* mimeType = parts[0]; //if contentType does not contain ";" NSString API assures that parts contains at least string itself as first element
    materialData.mimeType = mimeType;
    materialData.textEncoding = (__bridge NSString *)(CFStringGetNameOfEncoding(CFStringConvertNSStringEncodingToEncoding(request.responseEncoding)));
    
    if ([delegate respondsToSelector:@selector(downloadOfMaterial:didFinish:)]) {
        [delegate downloadOfMaterial:material didFinish:materialData];
    }
    
    [ObjectArchiver saveObject:materialData forKey:[MyEduService keyForMaterial:material] andPluginName:@"myedu" isCache:YES];
}

- (void)downloadMaterialRequestFailed:(ASIHTTPRequest *)request {
    id<MyEduServiceDelegate> delegate = [request.userInfo objectForKey:kServiceDelegateKey];
    if ([delegate respondsToSelector:@selector(downloadFailedForMaterial:responseStatusCode:)]) {
        MyEduMaterial* material = [request.userInfo objectForKey:kMaterialKey];
        [delegate downloadFailedForMaterial:material responseStatusCode:request.responseStatusCode];
    }
}

#pragma mark Video downloads callbacks

- (void)downloadVideoRequestFinished:(ASIHTTPRequest *)request {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            if (downloadObserver.observer && downloadObserver.finishBlock) {
                downloadObserver.finishBlock([NSURL fileURLWithPath:request.downloadDestinationPath]);
            }
        }];
    }
}

- (void)downloadVideoRequestFailed:(ASIHTTPRequest *)request {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            if (downloadObserver.failureBlock) {
                downloadObserver.failureBlock(request.responseStatusCode);
            }
        }];
    }
}

#pragma mark - ASIProgressDelegate

- (void)request:(ASIHTTPRequest *)request didReceiveBytes:(long long)bytes {
    NSString* key = request.userInfo[kVideoKey];
    NSArray* observers = self.downloadsObserversForVideoKey[key];
    if (key) {
        [[observers copy] enumerateObjectsUsingBlock:^(MyEduDownloadObserver* downloadObserver, NSUInteger idx, BOOL *stop) {
            if (downloadObserver.progressBlock) {
                if (request.contentLength == 0) {
                    downloadObserver.progressBlock(0, 0, 0.0);
                } else {
                    downloadObserver.progressBlock(request.totalBytesRead, request.contentLength, request.totalBytesRead/(double)request.contentLength);
                }
            }
        }];
    }
}

- (void)request:(ASIHTTPRequest *)request incrementDownloadSizeBy:(long long)newLength {
    [self request:request didReceiveBytes:1]; //number of bytes is not important, as not used in request:didReceiveBytes:
}

#pragma mark - dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
