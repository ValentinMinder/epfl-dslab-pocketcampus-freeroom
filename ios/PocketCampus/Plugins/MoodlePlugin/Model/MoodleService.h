/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */



#import "Service.h"

#import "moodle.h"

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

#pragma mark - MoodleServiceDelegate definition

@protocol MoodleServiceDelegate <ServiceDelegate>

@optional

//dummy HAS to be there because of how delegate abstraction is built in ServiceRequest
//simply ignore it
- (void)getCoursesListForDummy:(NSString*)dummy didReturn:(CoursesListReply*)reply;
- (void)getCoursesListFailedForDummy:(NSString*)dummy;
- (void)getCourseSectionsForCourseId:(NSString*)courseId didReturn:(SectionsListReply*)reply;
- (void)getCourseSectionsFailedForCourseId:(NSString*)courseId;

- (void)downloadOfMoodleResource:(MoodleResource*)moodleResource didFinish:(NSURL*)localFileURL;
- (void)downloadFailedForMoodleResource:(MoodleResource*)moodleResource responseStatusCode:(int)statusCode;

@end

#pragma mark - MoodleService definition

/*
 * Posted by self on NSNotificationCenter defaultCenter if a resource is added or removed
 * from favorite resources
 */
extern NSString* const kMoodleFavoritesMoodleResourcesUpdatedNotification;

/*
 * Key of NSNotifiation.userInfo
 * Value: MoodleResource that was added/removed from favorites
 */
extern NSString* const kMoodleFavoriteStatusMoodleResourceUpdatedUserInfoKey;

@interface MoodleService : Service<ServiceProtocol>

#pragma mark - Resources favorites and file management

- (void)addFavoriteMoodleResource:(MoodleResource*)moodleResource;
- (void)removeFavoriteMoodleResource:(MoodleResource*)moodleResource;
- (BOOL)isFavoriteMoodleResource:(MoodleResource*)moodleResource;

- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource;
- (NSString*)localPathForMoodleResource:(MoodleResource*)moodleResource createIntermediateDirectories:(BOOL)createIntermediateDirectories;
- (BOOL)isMoodleResourceDownloaded:(MoodleResource*)moodleResource;
- (BOOL)deleteDownloadedMoodleResource:(MoodleResource*)moodleResource;
- (BOOL)deleteAllDownloadedResources;

#pragma mark - Service methods

/*
 - (CoursesListReply *) getCoursesListAPI: (NSString *) dummy;  // throws TException
 - (SectionsListReply *) getCourseSectionsAPI: (NSString *) courseId;  // throws TException
 */

- (void)getCoursesListWithDelegate:(id<MoodleServiceDelegate>)delegate;
- (void)getCoursesSectionsForCourseId:(NSString*)courseId delegate:(id<MoodleServiceDelegate>)delegate;

#pragma mark - Saved elements

- (CoursesListReply*)getFromCacheCoursesList;
- (SectionsListReply*)getFromCacheCoursesSectionsForCourseId:(NSString*)courseId;

#pragma mark - MoodleResources files observation

- (void)addMoodleResourceObserver:(id)observer forResource:(MoodleResource*)resource eventBlock:(MoodleResourceEventBlock)eventBlock;
- (void)removeMoodleResourceObserver:(id)observer;
- (void)removeMoodleResourceObserver:(id)observer forResource:(MoodleResource*)resource;

#pragma mark - Fetch resources

- (void)downloadMoodleResource:(MoodleResource*)moodleResource progressView:(UIProgressView*)progressView delegate:(id)delegate;
- (void)cancelDownloadOfMoodleResourceForDelegate:(id)delegate;

@end