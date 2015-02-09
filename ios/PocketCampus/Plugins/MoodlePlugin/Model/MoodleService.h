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



#import "PCService.h"

#import "moodle.h"

#import "MoodleModelAdditions.h"

#import <limits.h>

#pragma mark - MoodleResourceObserver definition

typedef enum {
    MoodleResourceEventDownloaded,
    MoodleResourceEventDeleted
} MoodleResourceEvent;

typedef void (^MoodleResourceEventBlock)(MoodleResourceEvent event);

#pragma mark - MoodleServiceDelegate definition

@protocol MoodleServiceDelegate <PCServiceDelegate>

@optional

- (void)getCoursesForRequest:(MoodleCoursesRequest2*)request didReturn:(MoodleCoursesResponse2*)response;
- (void)getCoursesFailedForRequest:(MoodleCoursesRequest2*)request;
- (void)getSectionsForRequest:(MoodleCourseSectionsRequest2*)request didReturn:(MoodleCourseSectionsResponse2*)response;
- (void)getSectionsFailedForRequest:(MoodleCourseSectionsRequest2*)request;
- (void)printFileForRequest:(MoodlePrintFileRequest2*)request didReturn:(MoodlePrintFileResponse2*)response;
- (void)printFileFailedForRequest:(MoodlePrintFileRequest2*)request;

- (void)downloadOfMoodleFile:(MoodleFile2*)moodleFile didFinish:(NSURL*)localFileURL;
- (void)downloadFailedForMoodleFile:(MoodleFile2*)moodleFile responseStatusCode:(int)statusCode;

@end

#pragma mark - MoodleService definition

/**
 * Posted by self on NSNotificationCenter defaultCenter if an item is added or removed
 * from favorite resources
 */
extern NSString* const kMoodleFavoritesMoodleItemsUpdatedNotification;

/**
 * Key of NSNotifiation.userInfo
 * Value: MoodleItem that was added/removed from favorites
 */
extern NSString* const kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey;

@interface MoodleService : PCService<PCServiceProtocol>

#pragma mark - Resources favorites and file management

/**
 * moodleItem can be MoodleFile2 or MoodleUrl2
 * throwns exception otherwise
 */
- (void)addFavoriteMoodleItem:(id)moodleItem;
- (void)removeFavoriteMoodleItem:(id)moodleItem;
- (BOOL)isFavoriteMoodleItem:(id)moodleItem;

- (NSString*)localPathForMoodleFile:(MoodleFile2*)moodleFile;
- (NSString*)localPathForMoodleFile:(MoodleFile2*)moodleFile createIntermediateDirectories:(BOOL)createIntermediateDirectories;
- (BOOL)isMoodleFileDownloaded:(MoodleFile2*)moodleFile;
- (BOOL)deleteDownloadedMoodleFile:(MoodleFile2*)moodleFile;
- (BOOL)deleteAllDownloadedMoodleFiles;

- (void)totalNbBytesAllDownloadedMoodleFilesWithCompletion:(void (^)(unsigned long long totalNbBytes, BOOL error))completion;

#pragma mark - Service methods

/*
 - (MoodleCoursesResponse2 *) getCourses: (MoodleCoursesRequest2 *) request;  // throws TException
 - (MoodleCourseSectionsResponse2 *) getSections: (MoodleCourseSectionsRequest2 *) request;  // throws TException
 - (MoodlePrintFileResponse2 *) printFile: (MoodlePrintFileRequest2 *) request;  // throws TException
 */

- (void)getCoursesWithRequest:(MoodleCoursesRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate;
- (void)getSectionsWithRequest:(MoodleCourseSectionsRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate;
- (void)printFileWithRequest:(MoodlePrintFileRequest2*)request delegate:(id<MoodleServiceDelegate>)delegate;

#pragma mark - Cached versions

- (MoodleCoursesResponse2*)getFromCacheCoursesWithRequest:(MoodleCoursesRequest2*)request;
- (MoodleCourseSectionsResponse2*)getFromCacheSectionsWithRequest:(MoodleCourseSectionsRequest2*)request;

#pragma mark - MoodleResources files observation

- (void)addMoodleFileObserver:(id)observer forFile:(MoodleFile2*)file eventBlock:(MoodleResourceEventBlock)eventBlock;
- (void)removeMoodleFileObserver:(id)observer;
- (void)removeMoodleFileObserver:(id)observer forFile:(MoodleFile2*)file;

#pragma mark - File downloads

- (void)downloadMoodleFile:(MoodleFile2*)file progressView:(UIProgressView*)progressView delegate:(id)delegate;
- (void)cancelDownloadOfMoodleFilesForDelegate:(id)delegate;

@end