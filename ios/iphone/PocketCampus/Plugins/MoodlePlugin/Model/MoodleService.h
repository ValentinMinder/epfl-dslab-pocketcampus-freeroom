
#import "Service.h"

#import "moodle.h"

#import "ASIHTTPRequest.h"

#pragma mark - MoodleResourceObserver definition

typedef enum {
    MoodleResourceEventDownloaded,
    MoodleResourceEventDeleted
} MoodleResourceEvent;

typedef void (^MoodleResourceEventBlock)(MoodleResourceEvent event);

@interface MoodleResourceObserver : NSObject

@property (nonatomic, unsafe_unretained) id observer;
@property (nonatomic, strong) MoodleResource* resource;
@property (nonatomic, copy) MoodleResourceEventBlock eventBlock;

@end

#pragma mark - MoodleService definition

@interface MoodleService : Service<ServiceProtocol>

//@property(nonatomic, retain) MoodleSession* moodleCookie; //NOT USED, TO REMOVE

#pragma mark - Session

- (MoodleRequest*)createMoodleRequestWithCourseId:(int)courseId; //pass courseId = 0 to ignore it
- (MoodleSession*)lastSession;
- (BOOL)saveSession:(MoodleSession*)session;
- (BOOL)deleteSession;

#pragma mark - Resources file management

- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource;
- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource createIntermediateDirectories:(BOOL)createIntermediateDirectories;
- (BOOL)isMoodleResourceDownloaded:(MoodleResource*)moodleResource;
- (BOOL)deleteDownloadedMoodleResource:(MoodleResource*)moodleResource;
- (BOOL)deleteAllDownloadedResources;

#pragma mark - Service methods

- (void)getTequilaTokenForMoodleDelegate:(id)delegate;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate;
- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;

#pragma mark - Saved elements

- (CoursesListReply*)getFromCacheCourseListReply;
- (BOOL)saveToCacheCourseListReply:(CoursesListReply*)courseListReply;
- (SectionsListReply*)getFromCacheSectionsListReplyForCourse:(MoodleCourse*)course;
- (BOOL)saveToCacheSectionsListReply:(SectionsListReply*)sectionsListReply forCourse:(MoodleCourse*)course;

#pragma mark - MoodleResources files observation

- (void)addMoodleResourceObserver:(id)observer forResource:(MoodleResource*)resource eventBlock:(MoodleResourceEventBlock)eventBlock;
- (void)removeMoodleResourceObserver:(id)observer;
- (void)removeMoodleResourceObserver:(id)observer forResource:(MoodleResource*)resource;

#pragma mark - Fetch resources

- (void)downloadMoodleResource:(MoodleResource*)moodleResource progressView:(UIProgressView*)progressView delegate:(id)delegate;
- (void)cancelDownloadOfMoodleResourceForDelegate:(id)delegate;

@end

#pragma mark - MoodleServiceDelegate definition

@protocol MoodleServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForMoodleDidReturn:(TequilaToken*)tequilaKey;
- (void)getTequilaTokenForMoodleFailed;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)aTequilaKey didReturn:(MoodleSession*)aSessionId;
- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)aTequilaKey;

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest didReturn:(CoursesListReply*)coursesListReply;
- (void)getCoursesListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest didReturn:(EventsListReply*)eventsListReply;
- (void)getEventsListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest didReturn:(SectionsListReply*)sectionsListReply;
- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest;

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request;
- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request;

- (void)downloadOfMoodleResource:(MoodleResource*)moodleResource didFinish:(NSURL*)localFileURL;
- (void)downloadFailedForMoodleResource:(MoodleResource*)moodleResource responseStatusCode:(int)statusCode;

@end