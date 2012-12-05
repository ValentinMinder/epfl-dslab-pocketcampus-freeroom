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
    return [self saveObject:object forKey:key andPluginName:pluginName isCache:NO];
}

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString *)key andPluginName:(NSString *)pluginName isCache:(BOOL)isCache {
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
            NSString* path = [self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:isCache];
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
    return [self objectForKey:key andPluginName:pluginName isCache:NO];
}

+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName isCache:(BOOL)isCache {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    @try {
        return [NSKeyedUnarchiver unarchiveObjectWithFile:[self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:isCache]];
    }
    @catch (NSException *exception) {
        NSLog(@"-> Object for key %@ exception : impossible to retrieve archived object", key);
        return nil;
    }
}

+ (id<NSCoding>)objectForKey:(NSString*)key andPluginName:(NSString*)pluginName nilIfDiffIntervalLargerThan:(NSTimeInterval)interval isCache:(BOOL)isCache {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    
    id<NSCoding> object = [self objectForKey:key andPluginName:pluginName isCache:isCache];
    
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
    return [self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:NO];
}

+ (NSString*)pathForKey:(NSString*)key pluginName:(NSString*)pluginName customFileExtension:(NSString*)customFileExtension isCache:(BOOL)isCache {
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
    if (isCache) {
        path = [path stringByAppendingPathComponent:@"cache"];
    }
    NSString* ext = @"archive";
    if (customFileExtension) {
        ext = customFileExtension;
    }
    path = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@", key, ext]];
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

+ (BOOL)deleteAllCachedObjectsForPluginName:(NSString*)pluginName {
    @synchronized(self) {
        if (![pluginName isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"bad pluginName argument" reason:@"pluginName is either nil or not kind of class NSString*" userInfo:nil];
        }
        NSString* dir = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
        NSString* path = [dir stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
        path = [path stringByAppendingPathComponent:pluginName];
        path = [path stringByAppendingPathComponent:@"cache"];
        NSFileManager* fileManager = [[NSFileManager alloc] init];
        NSError* error = nil;
        [fileManager removeItemAtPath:path error:&error];
        if (error) {
            return NO;
        }
        return YES;
    }
}

@end
