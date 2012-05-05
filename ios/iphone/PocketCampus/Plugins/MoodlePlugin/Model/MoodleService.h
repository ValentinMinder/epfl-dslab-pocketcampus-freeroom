
#import "Service.h"

#import "moodle.h"

#import "ASIHTTPRequest.h"


@interface MoodleService : Service<ServiceProtocol> {
    NSString* moodleCookie;
}

@property(nonatomic, retain) NSString *moodleCookie;
//@property(nonatomic, assign) int value;

- (NSString*)getLocalPath:(NSString*)url;

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest WithDelegate:(id)delegate;

- (void)fetchMoodleResource:(NSString*)cookie :(NSString*)url withDelegate:(id)delegate;

@end


@protocol MoodleServiceDelegate <ServiceDelegate>

@optional
- (void)getCoursesList:(MoodleRequest*)aMoodleRequest DidReturn:(CoursesListReply*)coursesListReply;
- (void)getCoursesListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getEventsList:(MoodleRequest*)aMoodleRequest DidReturn:(EventsListReply*)eventsListReply;
- (void)getEventsListFailed:(MoodleRequest*)aMoodleRequest;
- (void)getCourseSections:(MoodleRequest*)aMoodleRequest DidReturn:(SectionsListReply*)sectionsListReply;
- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest;

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request;
- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request;

@end