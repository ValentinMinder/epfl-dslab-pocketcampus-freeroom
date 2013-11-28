//
//  PCURLSchemeHandler.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCURLSchemeHandler.h"

#import "PCUtils.h"

#import "MainController.h"

#import "PluginController.h"

@interface PCURLSchemeHandler ()

@property (nonatomic, weak) MainController* mainController;

@property (nonatomic, strong) NSMutableSet* validURLsCache;

@end

@implementation PCURLSchemeHandler

- (id)init {
    @throw [NSException exceptionWithName:@"Illegal init" reason:@"PCURLSchemeHandler must be init with initWithMainController:. Please access instance via MainControllerPublic" userInfo:nil];
}

- (id)initWithMainController:(MainController*)mainController {
    self = [super init];
    if (self) {
        self.mainController = mainController;
        self.validURLsCache = [NSMutableSet set];
    }
    return self;
}

- (BOOL)isValidPocketCampusURL:(NSURL*)url {
    if ([self.validURLsCache containsObject:url]) {
        return YES;
    }
    NSString* pluginIdentifier = [self pluginLowerIdentifierIfValidURL:url];
    if (pluginIdentifier) {
        [self.validURLsCache addObject:url];
        return YES;
    }
    return NO;
}

- (NSString*)pluginIdentifierForPocketCampusURL:(NSURL*)url {
    return [self pluginLowerIdentifierIfValidURL:url];
}

- (NSString*)actionForPocketCampusURL:(NSURL*)url {
    if (![self isValidPocketCampusURL:url]) {
        return nil;
    }
    NSString* action = @"";
    if (url.relativePath.length > 0) {
        action = [[url.relativePath substringFromIndex:1] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]; //remove original slash and convert HTML entities
    }
    return action;
}

- (NSDictionary*)parametersForPocketCampusURL:(NSURL*)url {
    if (![self isValidPocketCampusURL:url]) {
        return nil;
    }
    NSMutableDictionary* params = [[PCUtils urlStringParameters:url.absoluteString] mutableCopy];
    
    if (!params) {
        params = [NSMutableDictionary dictionary]; //empty dictionary
    } else {
        for (NSString* param in [params copy]) {
            params[param] = [params[param] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]; // convert HTML entities
            params[param] = [params[param] stringByReplacingOccurrencesOfString:@"+" withString:@" "]; //sometimes + are used for spaces in URLs
        }
    }
    return [params copy]; //non-mutable copy
}

- (UIViewController*)viewControllerForPocketCampusURL:(NSURL*)url {
    
    NSString* pluginLowerIdentifier = [self pluginLowerIdentifierIfValidURL:url];
    
    if (!pluginLowerIdentifier) {
        return nil;
    }
    
    [self.validURLsCache addObject:url];
    
    PluginController<PluginControllerProtocol>* pluginController = [self.mainController pluginControllerForPluginIdentifier:pluginLowerIdentifier];
    
    if (!pluginController) {
        return nil;
    }
    
    if (![pluginController respondsToSelector:@selector(viewControllerForURLQueryAction:parameters:)]) {
        return nil;
    }
    
    NSString* action = [self actionForPocketCampusURL:url];
    
    NSDictionary* params = [self parametersForPocketCampusURL:url];
    
    return [pluginController viewControllerForURLQueryAction:action parameters:params];
}

#pragma mark - Utilities

- (NSString*)pluginLowerIdentifierIfValidURL:(NSURL*)url { //returns nil if not valid plugin identifier found or invalid URL
    [PCUtils throwExceptionIfObject:url notKindOfClass:[NSURL class]];
    
    if (![url.scheme isEqualToString:@"pocketcampus"]) {
        return nil;
    }
    
    NSError* error = nil;
    
    NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:@"^(\\D*)\\.plugin\\.pocketcampus\\.org$" options:NSRegularExpressionCaseInsensitive error:&error];
    if (error) {
        return nil;
    }
    
    NSTextCheckingResult* result = [regex firstMatchInString:url.host options:0 range:NSMakeRange(0, [url.host length])];
    if (result.numberOfRanges < 1) {
        return nil;
    }
    
    NSString* pluginLowerIdentifier = nil;
    
    NSRange range = [result rangeAtIndex:1];
    if (range.length != 0) {
        pluginLowerIdentifier = [[url.host substringWithRange:range] lowercaseString];
    }
    
    if (!pluginLowerIdentifier || ![self.mainController existsPluginWithIdentifier:pluginLowerIdentifier]) {
        return nil;
    }
    
    return pluginLowerIdentifier;
}

@end
