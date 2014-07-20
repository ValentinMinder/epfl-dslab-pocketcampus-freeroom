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

//  Created by Loïc Gardiol on 13.01.13.

#import "moodle.h"

extern NSString* const kMoodleSaveDocsPositionGeneralSettingBoolKey;

#pragma mark - Old

@interface MoodleResource (Additions)<NSCopying>

/*
 * Returns last path component of iUrl
 * E.g. homework1.pdf
 */
@property (nonatomic, readonly) NSString* filename;

/*
 * Returns filename's extension in capitals
 * E.g. PDF, ZIP, ...
 */
@property (nonatomic, readonly) NSString* fileExtension;

- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToMoodleResource:(MoodleResource*)moodleResource;
- (NSUInteger)hash;

/*
 * Returns empty dictionary if setDefaultsDictionary:forMoodleResource: never called before
 */
+ (NSDictionary*)defaultsDictionaryForMoodleResource:(MoodleResource*)resource;
+ (void)setDefaultsDictionary:(NSDictionary*)defaultsDic forMoodleResource:(MoodleResource*)resource;

@end

@interface MoodleSection (Additions)<NSCopying>

@end

#pragma mark - New

@protocol MoodleItemDefaults <NSObject>

/**
 * @param item must be of the type of the class implementing the protocol
 * Should empty dictionary if setDefaultsDictionary:forMoodleItem: never called before
 */
@required
+ (NSDictionary*)defaultsDictionaryForMoodleItem:(id)item;
+ (void)setDefaultsDictionary:(NSDictionary*)defaultsDic forMoodleItem:(id)item;

@end

@interface MoodleCourseSection2 (Additions)<NSCopying> // Shallow copy (no property is deep copied, just pointing to same things)

/**
 * @return YES if current date is between startDate and endDate
 */
@property (nonatomic, readonly) BOOL isCurrent;

@end

@interface MoodleResource2 (Additions)<NSCopying>

/**
 * @return first non-nil among self.file, self.folder, self.url, or nil if all are nil
 */
@property (nonatomic, readonly) id item;

/**
 * @return name of self.item if exists
 */
@property (nonatomic, readonly) NSString* name;

- (BOOL)isEqual:(id)object;

/**
 * Propagates equality to self.item
 */
- (BOOL)isEqualToMoodleResource:(MoodleResource2*)otherResource;

/**
 * Propagates hash to self.item
 */
- (NSUInteger)hash;

@end

@interface MoodleFile2 (Additions)<MoodleItemDefaults>

/**
 * @return last path component of url
 * E.g. homework1.pdf
 */
@property (nonatomic, readonly) NSString* filename;

/*
 * Returns filename's extension in capitals
 * E.g. PDF, ZIP, ...
 */
@property (nonatomic, readonly) NSString* fileExtension;

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

/**
 * @return YES if urls are equal
 */
- (BOOL)isEqualToMoodleFile:(MoodleFile2*)otherFile;

@end

@interface MoodleFolder2 (Additions)

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

/**
 * @return YES if name and all files are equal
 */
- (BOOL)isEqualToMoodleFolder:(MoodleFolder2*)otherFolder;

@end

@interface MoodleUrl2 (Additions)

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

/**
 * @return YES if name and all files are equal
 */
- (BOOL)isEqualToMoodleUrl:(MoodleUrl2*)otherUrl;

@end