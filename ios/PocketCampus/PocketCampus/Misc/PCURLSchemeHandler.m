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

//  Created by Loïc Gardiol on 03.03.13.

#import "PCURLSchemeHandler.h"

#import "PCUtils.h"

#import "MainController.h"

#import "PluginController.h"

NSString* const kPocketCampusURLNoPluginSpecified = @"no_plugin";

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
    
    return [params copy]; //non-mutable copy
}

- (UIViewController*)viewControllerForPocketCampusURL:(NSURL*)url {
    
    NSString* pluginLowerIdentifier = [self pluginLowerIdentifierIfValidURL:url];
    
    if (!pluginLowerIdentifier || [pluginLowerIdentifier isEqualToString:kPocketCampusURLNoPluginSpecified]) {
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
    
    if (!url.host) {
        return kPocketCampusURLNoPluginSpecified;
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
