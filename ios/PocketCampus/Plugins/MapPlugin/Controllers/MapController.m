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

//  Created by Loïc Gardiol on 12.04.12.

#import "MapController.h"

#import "MapViewController.h"

#import "MapService.h"

static MapController* instance __weak = nil;

@interface MapController ()

@property (nonatomic, strong) MapViewController* mapViewController;

@end

@implementation MapController

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MapController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            self.mapViewController = [[MapViewController alloc] init];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:self.mapViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

#pragma mark - PluginController

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MapPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Map";
}

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    if ([action isEqualToString:@"search"]) {
        NSString* query = parameters[@"q"];
        if (query) {
            if ([self.mapViewController isViewLoaded]) {
                [self.mapViewController startSearchForQuery:query];
                return self.mapViewController;
            } else {
                return [[self class] viewControllerWithInitialSearchQuery:query];
            }
        }
    }
    return nil;
}

- (BOOL)handleURLQueryAction:(NSString *)action parameters:(NSDictionary *)parameters {
    if ([action isEqualToString:@"search"]) {
        NSString* query = parameters[@"q"];
        if (query) {
            if ([self.mapViewController isViewLoaded]) {
                [self.mapViewController startSearchForQuery:query];
            } else {
                self.mapViewController.initialQueryWithFullControls = query;
            }
            return YES;
        }
    } else if ([action isEqualToString:@"showLayer"]) {
        NSString* layerIdString = parameters[@"layerId"];
        long layerId = (long)[layerIdString longLongValue];
        if (layerId != 0) {
            MapService* mapService = [MapService sharedInstanceToRetain];
            mapService.selectedMapLayerIds = [NSSet setWithObject:@(layerId)];
            return YES;
        }
    }
    return NO;
}

+ (UIViewController*)viewControllerForWebURL:(NSURL *)webURL {
    if (![webURL.host isEqualToString:@"plan.epfl.ch"] && ![webURL.host isEqualToString:@"map.epfl.ch"]) {
        return nil;
    }
    NSDictionary* params = [PCUtils parametersDictionaryForURLString:webURL.absoluteString];
    NSString* query = params[@"room"];
    if (![query isKindOfClass:[NSString class]]) {
        return nil;
    }
    return [self viewControllerWithInitialSearchQuery:query];
}

#pragma mark - Public

+ (UIViewController*)viewControllerWithMapLayerIdsToDisplay:(NSSet*)layerIds {
    return [[MapViewController alloc] initWithMapLayerIdsToDisplay:layerIds];
}

+ (UIViewController*)viewControllerWithInitialMapItem:(MapItem*)mapItem {
    return [[MapViewController alloc] initWithInitialMapItem:mapItem];
}

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query {
    return [[MapViewController alloc] initWithInitialQuery:query];
}

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query pinLabelText:(NSString*)pinLabelText {
    return [[MapViewController alloc] initWithInitialQuery:query pinTextLabel:pinLabelText];
}

#pragma mark - dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
