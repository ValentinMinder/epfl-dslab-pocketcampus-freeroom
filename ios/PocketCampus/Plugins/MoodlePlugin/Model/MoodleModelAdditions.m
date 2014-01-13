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

#import <objc/runtime.h>

@implementation MoodleResource (Comparison)

- (NSString*)filename {
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        value = [[self.iUrl pathComponents] lastObject];
        value = [value stringByRemovingPercentEncoding];
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return value;
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

- (BOOL)isEqualToMoodleResource:(MoodleResource*)moodleResource {
    return [self.iUrl isEqualToString:moodleResource.iUrl];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.iUrl hash];
    return hash;
}

- (id)copyWithZone:(NSZone *)zone {
    MoodleResource* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.iName = self.iName;
    newInstance.iUrl = self.iUrl;
    return newInstance;
}

@end

@implementation MoodleSection (Additions)

- (id)copyWithZone:(NSZone *)zone {
    MoodleSection* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.iResources = self.iResources;
    newInstance.iText = self.iText;
    newInstance.iStartDate = self.iStartDate;
    newInstance.iEndDate = self.iEndDate;
    newInstance.iCurrent = self.iCurrent;
    return newInstance;
}

@end