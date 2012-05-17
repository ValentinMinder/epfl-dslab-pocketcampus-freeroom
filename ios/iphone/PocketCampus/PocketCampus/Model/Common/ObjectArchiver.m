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
        
        return [NSKeyedArchiver archiveRootObject:object toFile:[self pathForKey:key pluginName:pluginName]];
    }
    @catch (NSException *exception) {
        NSLog(@"-> Save object exception : impossible to save object");
        return NO;
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
    NSDictionary* fileAttributes = [self fileAttributesForKey:key andPluginName:pluginName];
    NSDate* modifDate = [fileAttributes objectForKey:@"NSFileModificationDate"];
    if ((double)(abs([modifDate timeIntervalSinceNow])) > interval) {
        return nil;
    }
    return [self objectForKey:key andPluginName:pluginName];
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
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* path = [[paths objectAtIndex:0] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@.save", pluginName, key]];
    return path;
}

+ (void) createComponentsForPath:(NSString*)path {
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

@end
