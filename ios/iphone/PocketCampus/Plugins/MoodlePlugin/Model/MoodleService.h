
#import "Service.h"

#import "moodle.h"

#import "ASIHTTPRequest.h"


@interface MoodleService : Service<ServiceProtocol> {
    NSString* moodleCookie;
}

@property(nonatomic, retain) NSString* moodleCookie;

+ (BOOL)deleteMoodleCookie;
- (BOOL)saveMoodleCookie:(NSString*)moodleCookie;
+ (NSString*)fileTypeForURL:(NSString*)urlString;
+ (NSString*)localPathForURL:(NSString*)urlString; //same as next with second argument NO
+ (NSString*)localPathForURL:(NSString*)urlString createIntermediateDirectories:(BOOL)createIntermediateDirectories;
+ (BOOL)isFileCached:(NSString*)localPath;
+ (BOOL)deleteFileAtPath:(NSString*)localPath;

- (void)getTequilaTokenForMoodleDelegate:(id)delegate;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate;

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;

- (void)fetchMoodleResourceWithURL:(NSString*)url cookie:(NSString*)cookie delegate:(id)delegate;

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

@end