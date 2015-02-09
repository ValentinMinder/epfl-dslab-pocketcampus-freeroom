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

#import "MoodleModelAdditions.h"

#import "MoodleService.h"

#import <objc/runtime.h>

NSString* const kMoodleSaveDocsPositionGeneralSettingBoolKey = @"SaveDocsPositionGeneralSettingBool";

NSString* const kMoodleDocsAutomaticallyHideNavBarSettingBoolKey = @"DocsAutomaticallyHideNavBarSettingBool";

NSString* const kMoodleDocsHideMasterWithNavBarSettingBoolKey = @"DocsHideMasterWithNavBarSettingBool";

@implementation MoodleCourseSection2 (Additions)

- (BOOL)isCurrent {
    if (self.title) {
        return NO;
    }
    NSTimeInterval currentTimestamp = [[NSDate date] timeIntervalSince1970];
    if (currentTimestamp < self.startDate/1000.0 || currentTimestamp > self.endDate/1000.0) {
        return NO;
    }
    return YES;
}

- (NSString*)titleOrDateRangeString {
    if (self.title) {
        return self.title;
    }
    NSString* string = nil;
    static NSDateFormatter* formatter = nil;
    static NSString* startDateFormat = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
        formatter.locale = [NSLocale currentLocale];
        formatter.timeZone = [NSTimeZone timeZoneWithName:@"Europe/Zurich"];
        formatter.dateStyle = NSDateFormatterMediumStyle;
        formatter.timeStyle = NSDateFormatterNoStyle;
        startDateFormat = [NSDateFormatter dateFormatFromTemplate:@"MMMd" options:0 locale:[NSLocale currentLocale]];
    });
    
    formatter.dateFormat = startDateFormat;
    NSString* startDateString = self.startDate != 0 ? [formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:self.startDate/1000.0]] : nil;
    formatter.dateFormat = nil;
    NSString* endDateString = self.endDate != 0 ? [formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:self.endDate/1000.0]] : nil;
    
    if (startDateString && endDateString) {
        string = [NSString stringWithFormat:@"%@ - %@", startDateString, endDateString];
    } else if (startDateString) {
        string = startDateString;
    } else if (endDateString) {
        string = endDateString;
    }
    return string;
}

- (NSString*)webViewReadyDetails {
    if (!self.details) {
        return nil;
    }
    static NSString* const kStartingTags = @"<html><style type=\"text/css\">body {font-family: \"Helvetica Neue\";}</style><body>";
    static NSString* const kEndingTags = @"</body></html>";
    return [NSString stringWithFormat:@"%@%@%@", kStartingTags, self.details, kEndingTags];
}

- (id)copyWithZone:(NSZone *)zone {
    MoodleCourseSection2* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.resources = self.resources;
    newInstance.title = self.title;
    newInstance.startDate = self.startDate;
    newInstance.endDate = self.endDate;
    newInstance.details = self.details;
    return newInstance;
}

@end

@implementation MoodleResource2 (Additions)

- (id)item {
    if (self.file) {
        return self.file;
    }
    if (self.folder) {
        return self.folder;
    }
    if (self.url) {
        return self.url;
    }
    return nil;
}

- (NSString*)name {
    if (self.file) {
        return self.file.name;
    }
    if (self.folder) {
        return self.folder.name;
    }
    if (self.url) {
        return self.url.name;
    }
    return nil;
}

- (UIImage*)systemIcon {
    if (self.file) {
        return self.file.systemIcon;
    }
    if (self.folder) {
        return self.folder.systemIcon;
    }
    if (self.url) {
        return self.url.systemIcon;
    }
    return nil;
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleResource:object];
}

- (BOOL)isEqualToMoodleResource:(MoodleResource2*)moodleResource {
    return [self.item isEqual:moodleResource.item];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.item hash];
    return hash;
}

- (id)copyWithZone:(NSZone *)zone {
    MoodleResource2* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.file = self.file;
    newInstance.folder = self.folder;
    newInstance.url = self.url;
    return newInstance;
}


@end

@implementation MoodleFile2 (Additions)

- (NSString*)filename {
    if (!self.url) {
        return nil;
    }
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        //Trick to remove url query paramters if any (we don't want them for the filename)
        //http://stackoverflow.com/a/4272070/1423774
        NSURL* url = [NSURL URLWithString:self.url];
        url = [[NSURL alloc] initWithScheme:url.scheme host:url.host path:url.path];
        value = [[[url absoluteString] pathComponents] lastObject];
        value = [value stringByRemovingPercentEncoding];
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return value;
}

- (NSString*)fileExtension {
    return [self.filename pathExtension];
}

- (UIImage*)systemIcon {
    return [PCUtils iconForFileExtension:self.extension];
}

- (NSURL*)iconURLForMinimalSquareSideLength:(CGFloat)length {
    if (!self.icon) {
        return nil;
    }
    if (length < 24.0) {
        length = 24.0;
    } else if (length < 32.0) {
        length = 32.0;
    } else if (length < 64.0) {
        length = 64.0;
    } else if (length < 128.0) {
        length = 128.0;
    } else if (length < 256.0) {
        length = 256.0;
    } else {
        length = 256.0;
    }
    NSString* urlString = [self.icon stringByReplacingOccurrencesOfString:@"{size}" withString:[NSString stringWithFormat:@"%d", (int)length]];
    return [NSURL URLWithString:urlString];
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleFile:object];
}

- (BOOL)isEqualToMoodleFile:(MoodleFile2 *)otherFile {
    return [self.url isEqual:otherFile.url];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.url hash];
    return hash;
}

#pragma mark MoodleItemDefaults protocol

+ (NSDictionary*)defaultsDictionaryForMoodleItem:(id)item {
    [PCUtils throwExceptionIfObject:item notKindOfClass:[MoodleFile2 class]];
    MoodleFile2* file = (MoodleFile2*)item;
    NSUserDefaults* moodleDefaults = [PCPersistenceManager userDefaultsForPluginName:@"moodle"];
    NSMutableDictionary* resourceDic = [[moodleDefaults objectForKey:[self keyForDefaultsDictionaryForMoodleFile:file]] mutableCopy];
    if (!resourceDic) {
        resourceDic = [NSMutableDictionary dictionary];
    }
    return resourceDic;
}

+ (void)setDefaultsDictionary:(NSDictionary*)defaultsDic forMoodleItem:(id)item; {
    [PCUtils throwExceptionIfObject:item notKindOfClass:[MoodleFile2 class]];
    MoodleFile2* file = (MoodleFile2*)item;
    NSUserDefaults* moodleDefaults = [PCPersistenceManager userDefaultsForPluginName:@"moodle"];
    [moodleDefaults setObject:defaultsDic forKey:[self keyForDefaultsDictionaryForMoodleFile:file]];
}

#pragma mark Private

+ (NSString*)keyForDefaultsDictionaryForMoodleFile:(MoodleFile2*)file {
    static NSString* const kDicPostfix = @"MoodleFileDictionary";
    return [kDicPostfix stringByAppendingFormat:@"%u", [file.url hash]];
}

@end

@implementation MoodleFolder2 (Additions)

- (UIImage*)systemIcon {
    return [PCUtils iconForFileExtension:kPCUtilsExtensionFolder];
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleFolder:object];
}

- (BOOL)isEqualToMoodleFolder:(MoodleFolder2 *)otherFolder {
    if (![self.name isEqual:otherFolder.name]) {
        return NO;
    }
    return [self.files isEqualToArray:otherFolder.files];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    for (MoodleFile2* file in self.files) {
        hash += [file hash];
    }
    return hash;
}

@end

@implementation MoodleUrl2 (Additions)

- (UIImage*)systemIcon {
    return [PCUtils iconForFileExtension:kPCUtilsExtensionLink];
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleUrl:object];
}

- (BOOL)isEqualToMoodleUrl:(MoodleUrl2 *)otherUrl {
    return [self.url isEqual:otherUrl.url];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.url hash];
    return hash;
}

@end
