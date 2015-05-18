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

#import "PCPersistenceManager.h"

#import "MainController.h"

#import "NSUserDefaults+Additions.h"

NSString* PCPersistenceManagerPocketCampusPlatformPluginName = @"pocketcampus";

static NSString* const kPCUserDefaultsSharedAppGroupName = @"group.org.pocketcampus";

@interface PCUserDefaults : NSUserDefaults

@property (nonatomic, strong) NSString* pluginName;
@property (nonatomic) BOOL cache;

@end

@implementation PCUserDefaults

+ (NSUserDefaults*)sharedDefaults {
    static NSUserDefaults* defaults = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if ([PCUtils isOSVersionSmallerThan:8.0]) {
            defaults = [NSUserDefaults standardUserDefaults];
            [defaults addSuiteNamed:kPCUserDefaultsSharedAppGroupName];
        } else {
            defaults = [[NSUserDefaults alloc] initWithSuiteName:kPCUserDefaultsSharedAppGroupName];
        }
        [defaults synchronize];
    });
    return defaults;
}

+ (instancetype)userDefaultsForPluginName:(NSString*)pluginName isCache:(BOOL)cache {
    PCUserDefaults* instance = [self new];
    instance.pluginName = pluginName;
    instance.cache = cache;
    return instance;
}

- (id)objectForKey:(NSString*)key {
    [PCUtils throwExceptionIfObject:key notKindOfClass:[NSString class]];
    NSUserDefaults* sharedDefaults = [self.class sharedDefaults];
    NSDictionary* pluginDic = [sharedDefaults objectForKey:[self pluginDicKey]];
    return pluginDic[key];
}

- (void)setObject:(id)value forKey:(NSString*)key {
    [PCUtils throwExceptionIfObject:key notKindOfClass:[NSString class]];
    NSUserDefaults* sharedDefaults = [self.class sharedDefaults];
    NSString* pluginDicKey = [self pluginDicKey];
    NSMutableDictionary* pluginDic = [[sharedDefaults objectForKey:pluginDicKey] mutableCopy];
    if (!pluginDic) {
        pluginDic = [NSMutableDictionary dictionary];
    }
    if (value) {
        pluginDic[key] = value;
    } else {
        [pluginDic removeObjectForKey:key];
    }
    [sharedDefaults setObject:pluginDic forKey:pluginDicKey];
}

- (void)removeObjectForKey:(NSString *)key {
    [PCUtils throwExceptionIfObject:key notKindOfClass:[NSString class]];
    [self setObject:nil forKey:key];
}

- (void)removeAllObjects {
    [[self.class sharedDefaults] removeObjectForKey:[self pluginDicKey]];
}

- (NSString*)pluginDicKey {
    static NSString* const kDicPostfix = @"PCPluginDictionary";
    return [NSString stringWithFormat:@"%@-%@-%@", kDicPostfix, self.pluginName, self.cache ? @"Cache" : @""];
}

@end

@implementation PCPersistenceManager

#pragma mark - Persistence migration

+ (void)migrateDataOnceToSharedAppGroupPersistence {
#ifdef TARGET_IS_MAIN_APP
    static NSString* const kDefaultsMigrationDoneBoolKey = @"PCPersistenceManagerDefaultsMigrationToAppGroupPersistenceDoneBool";
    static NSString* const kBundleIdentifierMigrationDoneBoolKey = @"PCPersistenceManagerBundleIdentifierMigrationToAppGroupPersistenceDoneBool";
    // Ok, not in extension, so let's migrate containing (main) app standardDefaults
    // to share defaults. TARGET_IS_EXENSION is defined in preprocessor macros of the extensions targets.
    // http://stackoverflow.com/a/25048440/1423774
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        
        NSUserDefaults* defaults = [PCUserDefaults sharedDefaults];
        
        // Step 1: migrating standardUserDefaults to app group user defaults
        
        CLSNSLog(@"-> Migration to app group persistence...");
        if ([PCUtils isOSVersionGreaterThanOrEqualTo:8.0]) {
            if (![defaults boolForKey:kDefaultsMigrationDoneBoolKey]) {
                // copying data from standardUserDefaults to shared user defaults
                @try {
                    NSUserDefaults* oldStandardDefaults = [NSUserDefaults standardUserDefaults];
                    [defaults replaceKeyValuesWithOnesFromDictionary:oldStandardDefaults.dictionaryRepresentation];
                    [defaults setBool:YES forKey:kDefaultsMigrationDoneBoolKey];
                    [defaults synchronize];
                    CLSNSLog(@"   1. Standard defaults successfully migrated to app group defaults.");
                }
                @catch (NSException *exception) {
                    [defaults setBool:NO forKey:kDefaultsMigrationDoneBoolKey];
                    [defaults synchronize];
                    CLSNSLog(@"   1. EXCEPTION while migrating defaults from standard defaults to app group defaults: %@", exception);
                }
            } else {
                CLSNSLog(@"   1. (ALREADY DONE) Standard defaults migration to app group defaults.");
            }
        } else {
            CLSNSLog(@"   1. Will NOT migrate standard defaults to app group defaults because still iOS 7 (only required for iOS 8).");
        }
        
        
        // Step 2: migrating files
        
        NSString* oldBundleIdentifierPath = [self classicBundleIdentifierPersistencePath];
        NSFileManager* fileManager = [NSFileManager defaultManager];
        if (![fileManager fileExistsAtPath:oldBundleIdentifierPath]) {
            CLSNSLog(@"   2. No old bundle identifier folder to migrate to app group container folder.");
            [defaults setBool:YES forKey:kBundleIdentifierMigrationDoneBoolKey];
            [defaults synchronize];
        } else if (![defaults boolForKey:kBundleIdentifierMigrationDoneBoolKey]) {
            NSString* newBundleIdentifierPath = [self appGroupBundleIdentifierPersistencePath];
            
            [self createComponentsForPath:oldBundleIdentifierPath];
            [self createComponentsForPath:newBundleIdentifierPath];
            
            NSError* error = nil;
            
            [fileManager moveItemAtPath:oldBundleIdentifierPath toPath:newBundleIdentifierPath error:&error];
            if (!error) {
                [defaults setBool:YES forKey:kBundleIdentifierMigrationDoneBoolKey];
                [defaults synchronize];
                CLSNSLog(@"   2. Old bundle identifier folder successfully moved to new app group container folder.");
            } else {
                [defaults setBool:NO forKey:kBundleIdentifierMigrationDoneBoolKey];
                [defaults synchronize];
                CLSNSLog(@"   2. ERROR while migrating old bundle identifier folder to new app group container folder: %@", error);
            }
        } else {
            CLSNSLog(@"   2. (ALREADY DONE) Old bundle identifier folder move to new app group container folder.");
        }
    });
#endif
}

#pragma mark - Standard persistence

+ (NSUserDefaults*)sharedDefaults {
    return [PCUserDefaults sharedDefaults];
}

+ (NSUserDefaults*)userDefaultsForPluginName:(NSString*)pluginName {
    return [self _userDefaultsForPluginName:pluginName];
}

+ (NSUserDefaults*)cacheUserDefaultsForPluginName:(NSString *)pluginName {
    return [self _cacheUserDefaultsForPluginName:pluginName];
}

#pragma mark Private

+ (PCUserDefaults*)_userDefaultsForPluginName:(NSString*)pluginName {
    [self checkPluginName:pluginName];
    return [PCUserDefaults userDefaultsForPluginName:pluginName isCache:NO];
}

+ (PCUserDefaults*)_cacheUserDefaultsForPluginName:(NSString*)pluginName {
    [self checkPluginName:pluginName];
    return [PCUserDefaults userDefaultsForPluginName:pluginName isCache:YES];
}

#pragma mark - Complex objects persistence

static NSString* const kBundleIdentifier = @"org.pocketcampus";

+ (NSString*)appGroupBundleIdentifierPersistencePath {
    static NSString* appSupportBundlePath = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString* path = [[NSFileManager defaultManager] containerURLForSecurityApplicationGroupIdentifier:kPCUserDefaultsSharedAppGroupName].path;
        path = [path stringByAppendingPathComponent:@"Library"];
        path = [path stringByAppendingPathComponent:@"Application Support"];
        path = [path stringByAppendingPathComponent:kBundleIdentifier];
        appSupportBundlePath = path;
    });
    return appSupportBundlePath;
}

+ (NSString*)classicBundleIdentifierPersistencePath {
    static NSString* appSupportBundlePath = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString* dir = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
        appSupportBundlePath = [dir stringByAppendingPathComponent:kBundleIdentifier];
    });
    return appSupportBundlePath;
}

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString*)key pluginName:(NSString*)pluginName {
    return [self saveObject:object forKey:key pluginName:pluginName isCache:NO];
}

+ (BOOL)saveObject:(id<NSCoding>)object forKey:(NSString *)key pluginName:(NSString *)pluginName isCache:(BOOL)isCache {
    @synchronized(self) {
        if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
        }
        @try {
            NSString* path = [self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:isCache];
            if (object == nil) {
                NSError* error = NULL;
                NSFileManager* fileManager = [NSFileManager defaultManager];
                [fileManager removeItemAtPath:path error:&error];
                
                if (error && ![fileManager fileExistsAtPath:path]) { //then error is that file is already deleted
                    return YES;
                }
                return (error == NULL);
            }
            [[self class] createComponentsForPath:path];
            return [NSKeyedArchiver archiveRootObject:object toFile:path];
        }
        @catch (NSException *exception) {
            CLSNSLog(@"-> Save object exception : %@", exception);
            return NO;
        }
    }
}

+ (id<NSCoding>)objectForKey:(NSString*)key pluginName:(NSString*)pluginName {
    return [self objectForKey:key pluginName:pluginName isCache:NO];
}

+ (id<NSCoding>)objectForKey:(NSString*)key pluginName:(NSString*)pluginName isCache:(BOOL)isCache {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    @try {
        return [NSKeyedUnarchiver unarchiveObjectWithFile:[self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:isCache]];
    }
    @catch (NSException *exception) {
        CLSNSLog(@"-> Object for key %@ exception : impossible to retrieve archived object (%@)", key, exception);
        return nil;
    }
}

+ (id<NSCoding>)objectForKey:(NSString*)key pluginName:(NSString*)pluginName nilIfDiffIntervalLargerThan:(NSTimeInterval)interval isCache:(BOOL)isCache {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    
    id<NSCoding> object = [self objectForKey:key pluginName:pluginName isCache:isCache];
    
    if (!object) {
        return nil;
    }
    
    NSDictionary* fileAttributes = [self fileAttributesForKey:key pluginName:pluginName isCache:isCache];
    if (!fileAttributes) {
        CLSNSLog(@"!! ERROR in objectForKey:pluginName:nilIfDiffIntervalLargerThan:isCache: : could not read file attributes");
        return nil;
    }
    NSDate* modifDate = fileAttributes[NSFileModificationDate];
    if ((double)(fabs([modifDate timeIntervalSinceNow])) > interval) {
        return nil;
    }
    return object;
}

#pragma mark - Cache deletion

+ (BOOL)deleteCacheForPluginName:(NSString*)pluginName {
    @synchronized(self) {
        [self checkPluginName:pluginName];
        
        // Cache defaults deletion
        PCUserDefaults* cacheDefaults = [self _cacheUserDefaultsForPluginName:pluginName];
        [cacheDefaults removeAllObjects];
        [cacheDefaults synchronize];
        
        // Complex objects deletion
        NSString* path = [self appGroupBundleIdentifierPersistencePath];
        path = [path stringByAppendingPathComponent:[self standardizedNameForPluginName:pluginName]];
        path = [path stringByAppendingPathComponent:@"cache"];
        NSFileManager* fileManager = [NSFileManager defaultManager];
        NSError* error = nil;
        [fileManager removeItemAtPath:path error:&error];
        if (error) {
            return NO;
        }
        return YES;
    }
}


#pragma mark - Public utils

+ (void)createComponentsForPath:(NSString*)path {
    @synchronized(self) {
        if (![path isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"bad path" reason:@"bad path argument" userInfo:nil];
        }
        NSFileManager* fileManager = [NSFileManager defaultManager];
        /*BOOL isDirectory = NO;
        [fileManager fileExistsAtPath:path isDirectory:&isDirectory];
        if (isDirectory) {
            [path stringByAppendingPathComponent:@"dummy"];
        }*/
        NSString* directoryPath = [path stringByDeletingLastPathComponent];
        if (![fileManager fileExistsAtPath:directoryPath]) {
            [fileManager createDirectoryAtPath:directoryPath withIntermediateDirectories:YES attributes:nil error:nil];
        }
    }
}

+ (NSDictionary*)fileAttributesForKey:(NSString*)key pluginName:(NSString*)pluginName {
    return [self fileAttributesForKey:key pluginName:pluginName isCache:NO];
}

+ (NSDictionary*)fileAttributesForKey:(NSString*)key pluginName:(NSString*)pluginName isCache:(BOOL)isCache {
    if (![key isKindOfClass:[NSString class]] || ![pluginName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument(s)" reason:@"bad key and/or pluginName argument" userInfo:nil];
    }
    NSString* filePath = [self pathForKey:key pluginName:pluginName customFileExtension:nil isCache:isCache];
    NSFileManager* fileManager = [[NSFileManager alloc] init];
    NSError* error = nil;
    NSDictionary* fileAttributes = [fileManager attributesOfItemAtPath:filePath error:&error];
    if (error != nil) {
        CLSNSLog(@"-> fileAttributeForKey:pluginName:isCache: impossible to get attribute of file %@", filePath);
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
    
    static NSString* appSupportBundlePath = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        appSupportBundlePath = [self appGroupBundleIdentifierPersistencePath];
    });
    
    NSString* path = [appSupportBundlePath stringByAppendingPathComponent:[self standardizedNameForPluginName:pluginName]];
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

#pragma mark - Private

+ (void)checkPluginName:(NSString*)pluginName {
#ifdef TARGET_IS_MAIN_APP
    if ([pluginName isEqualToString:PCPersistenceManagerPocketCampusPlatformPluginName]) {
        return;
    }
    if (![[MainController publicController] isPluginAnycaseIdentifierValid:pluginName]) {
        [NSException raise:@"Illegal argument" format:@"pluginName '%@' is not valid", pluginName];
    }
#else
#warning this is ugly, pluginName validity not checked when in extension.
    //pluginsList is not initialized if not main app.
    /*NSArray* pluginIdentifiersFromConfig = [[PCConfig defaults] objectForKey:PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY];
    for (NSString* pluginIdentifier in pluginIdentifiersFromConfig) {
        if ([[pluginIdentifier lowercaseString] isEqualToString:[pluginName lowercaseString]]) {
            return;
        }
    }
    [NSException raise:@"Illegal argument" format:@"pluginName '%@' is not valid", pluginName];*/
#endif
}

+ (NSString*)standardizedNameForPluginName:(NSString*)name {
    name = [[name lowercaseString] stringByReplacingOccurrencesOfString:@"plugin" withString:@""];
    name = [name stringByReplacingOccurrencesOfString:@" " withString:@""];
    return name;
}

@end
