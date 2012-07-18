//
//  ObjectArchiver.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "ObjectArchiver.h"

@implementation ObjectArchiver

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString*)key andPluginName:(NSString*)pluginName {
    @synchronized(self) {
        if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
        }
        @try {
            
            if (object == nil) {
                NSError* error = NULL;
                NSFileManager* fileManager = [[NSFileManager alloc] init];
                [fileManager removeItemAtPath:[self pathForKey:key pluginName:pluginName] error:&error];
                [fileManager release];
                return (error == NULL);
            }
            NSString* path = [self pathForKey:key pluginName:pluginName];
            [[self class] createComponentsForPath:path];
            return [NSKeyedArchiver archiveRootObject:object toFile:path];
        }
        @catch (NSException *exception) {
            NSLog(@"-> Save object exception : impossible to save object");
            return NO;
        }
    }
}

+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    @try {
        return [NSKeyedUnarchiver unarchiveObjectWithFile:[self pathForKey:key pluginName:pluginName]];
    }
    @catch (NSException *exception) {
        NSLog(@"-> Object for key %@ exception : impossible to retrieve archived object", key);
        return nil;
    }
}

+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName nilIfDiffIntervalLargerThan:(NSTimeInterval)interval {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    
    id<NSCoding> object = [self objectForKey:key andPluginName:pluginName];
    
    if (!object) {
        return nil;
    }
    
    NSDictionary* fileAttributes = [self fileAttributesForKey:key andPluginName:pluginName];
    if (!fileAttributes) {
        @throw [NSException exceptionWithName:@"-> objectForKey:andPluginName:nilIfDiffIntervalLargerThan: exception" reason:@"could not read file attributes" userInfo:nil];
    }
    NSDate* modifDate = [fileAttributes objectForKey:@"NSFileModificationDate"];
    if ((double)(abs([modifDate timeIntervalSinceNow])) > interval) {
        return nil;
    }
    return object;
}

+ (NSDictionary*)fileAttributesForKey:(NSString*)key andPluginName:(NSString*)pluginName {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    NSString* filePath = [self pathForKey:key pluginName:pluginName];
    NSFileManager* fileManager = [[NSFileManager alloc] init];
    NSError* error = nil;
    NSDictionary* fileAttributes = [fileManager attributesOfItemAtPath:filePath error:&error];
    [fileManager release];
    if (error != nil) {
        NSLog(@"-> fileAttributeForKey:andPluginName: impossible to get attribute of file %@", filePath);
        return nil;
    }
    return fileAttributes;
}

+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    /*NSArray* paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    NSString* path = [[paths objectAtIndex:0] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@.save", pluginName, key]];
    path = [path stringByAppendingPathComponent:@"Archived Objects"];
    return path;*/
    
    NSString* dir = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	NSString* path = [dir stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    path = [path stringByAppendingPathComponent:pluginName];
    path = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.archive", key]];
	return path;
    
}

+ (void)createComponentsForPath:(NSString*)path {
    @synchronized(self) {
        if (![path isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"bad path" reason:@"bad path argument" userInfo:nil];
        }
        NSFileManager* fileManager = [[NSFileManager alloc] init];
        NSString* directoryPath = [path stringByDeletingLastPathComponent];
        if (![fileManager fileExistsAtPath:directoryPath]) {
            [fileManager createDirectoryAtPath:directoryPath withIntermediateDirectories:YES attributes:nil error:nil];
        }
        [fileManager release];
    }
}

@end
