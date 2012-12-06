
#import "Service.h"

#import "moodle.h"

#import "ASIHTTPRequest.h"


@interface MoodleService : Service<ServiceProtocol>

@property(nonatomic, retain) MoodleSession* moodleCookie;

#pragma Session

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

#pragma mark - Fetch resources

- (void)downloadMoodleResource:(MoodleResource*)moodleResource progressView:(UIProgressView*)progressView delegate:(id)delegate;
@end


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