
#import "Service.h"

#import "moodle.h"

#import "ASIHTTPRequest.h"


@interface MoodleService : Service<ServiceProtocol> {
    NSString* moodleCookie;
}

@property(nonatomic, retain) NSString *moodleCookie;
//@property(nonatomic, assign) int value;

- (NSString*)getLocalPath:(NSString*)url;

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest withDelegate:(id)delegate;

- (void)fetchMoodleResource:(NSString*)cookie :(NSString*)url withDelegate:(id)delegate;

@end


@protocol MoodleServiceDelegate <ServiceDelegate>

@optional
- (void)getCoursesList:(MoodleRequest*)aMoodleRequest didReturn:(CoursesListReply*)coursesListReply;
- (void)getCoursesListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest didReturn:(EventsListReply*)eventsListReply;
- (void)getEventsListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest didReturn:(SectionsListReply*)sectionsListReply;
- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest;

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request;
- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request;

@end