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
@end
