//
//  ObjectArchiver.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ObjectArchiver : NSObject

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString*)key andPluginName:(NSString*)pluginName;
+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName;
+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName nilIfDiffIntervalLargerThan:(NSTimeInterval)interval; //nb seconds
+ (void)createComponentsForPath:(NSString*)path;
+ (NSDictionary*)fileAttributesForKey:(NSString*)key andPluginName:(NSString*)pluginName;
+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName;
+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName customFileExtension:(NSString*)customFileExtension; //extension without dot. Passing nil customExtension will result in extension .archive
@end
