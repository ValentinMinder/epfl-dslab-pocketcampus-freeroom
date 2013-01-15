//
//  ObjectArchiver.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ObjectArchiver : NSObject

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
