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




//  Created by Loïc Gardiol on 23.03.12.


#import <Foundation/Foundation.h>

@interface PCObjectArchiver : NSObject

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString*)key andPluginName:(NSString*)pluginName; //same as next with isCache = NO. Passing nil objects delete files for corresponding key if it exists
+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString *)key andPluginName:(NSString *)pluginName isCache:(BOOL)isCache;
+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName; //same as next with isCache = NO
+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName isCache:(BOOL)isCache;
+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName nilIfDiffIntervalLargerThan:(NSTimeInterval)interval isCache:(BOOL)isCache; //nb seconds
+ (void)createComponentsForPath:(NSString*)path;
+ (NSDictionary*)fileAttributesForKey:(NSString*)key andPluginName:(NSString*)pluginName;
+ (NSDictionary*)fileAttributesForKey:(NSString*)key andPluginName:(NSString*)pluginName isCache:(BOOL)isCache;
+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName; //same as next with customExt = nil and isCache = NO
+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName customFileExtension:(NSString*)customFileExtension isCache:(BOOL)isCache; //extension without dot. Passing nil customExtension will result in extension .archive
+ (BOOL)deleteAllCachedObjectsForPluginName:(NSString*)pluginName; //will ONLY delete save objects that were saved with option isCache = YES
@end
