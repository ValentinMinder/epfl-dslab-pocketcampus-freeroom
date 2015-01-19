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


#import "EventsController.h"

#import "EventPoolViewController.h"

#import "EventItemViewController.h"

#import "PCUtils.h"

#import "EventsSplashDetailViewController.h"

#import "EventsService.h"

#import "PushNotifController.h"

static EventsController* instance __weak = nil;

@interface EventsController ()<UISplitViewControllerDelegate>

@property (nonatomic, strong) EventsService* eventsService;

@end

@implementation EventsController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"EventsController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
        }
        self = [super init];
        if (self) {
            EventPoolViewController* rootPoolViewController = [[EventPoolViewController alloc] initAndLoadRootPool];
            
            if ([PCUtils isIdiomPad]) {
                PCNavigationController* navController =  [[PCNavigationController alloc] initWithRootViewController:rootPoolViewController];
                EventsSplashDetailViewController* splashDetailViewController = [[EventsSplashDetailViewController alloc] init];
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:[[PCNavigationController alloc] initWithRootViewController:splashDetailViewController]];
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:rootPoolViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
            
            self.eventsService = [EventsService sharedInstanceToRetain];
            /*#warning TO REMOVE
            [self.eventsService addUserTicket:@"d3f760d257605db44b40ea81fb69040e"]; //Loïc Privacy Congress 2013
            [self.eventsService addUserTicket:@"amer"]; //Loïc Privacy Congress 2013
            [self.eventsService addUserTicket:@"2c79be552072699bd4abac2af98efeac"]; //Loïc EDIC Open House 2014
            [self.eventsService addUserTicket:@"6298eb264f3cb42f6faa7b6a7f5c5482"]; //Loïc IC Reasearch Day 2013*/
            
            /*#warning REMOVE
            [self.eventsService addUserTicket:@"test_push_notif"];
            
            [[PushNotifController sharedInstance] registerDeviceForPushNotificationsWithPluginLowerIdentifier:@"events" reason:@"Test push notif" success:^(BOOL alertAllowed, BOOL badgeAllowed, BOOL soundAllowed) {
                NSLog(@"Success: %d %d %d", alertAllowed, badgeAllowed, soundAllowed);
            } failure:^(PushNotifDeviceRegistrationError error) {
                NSLog(@"Failure: %d", error);
            }];
            
            [[PushNotifController sharedInstance] addPushNotificationObserver:self forPluginLowerIdentifier:@"events" newNotificationBlock:^(NSString *notifMessage, NSDictionary *notifFullDictionary) {
                NSLog(@"new notif: %@, %@", notifMessage, notifFullDictionary);
            }];*/
            
            instance = self;
        }
        return self;
    }
}

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

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    return [self viewControllerForURLQueryAction:action parameters:parameters handleSilent:YES];
}

- (BOOL)handleURLQueryAction:(NSString *)action parameters:(NSDictionary *)parameters {
    
    BOOL foundSilent = [self handleSilentParameters:parameters];
    
    UIViewController* viewController = [self viewControllerForURLQueryAction:action parameters:parameters handleSilent:NO];
    
    if (!viewController) {
        if (foundSilent) {
            return YES;
        }
        return NO;
    }
    
    UINavigationController* navController = nil;
    
    if ([PCUtils isIdiomPad]) {
        if ([viewController isKindOfClass:[EventPoolViewController class]] && [(EventPoolViewController*)viewController poolId] == [eventsConstants CONTAINER_EVENT_ID]) {
            navController = self.mainSplitViewController.viewControllers[0];
        } else {
            navController = self.mainSplitViewController.viewControllers[1];
            if (![navController isKindOfClass:[UINavigationController class]]) {
                navController = [[UINavigationController alloc] initWithRootViewController:viewController];
                self.mainSplitViewController.viewControllers = @[self.mainSplitViewController.viewControllers[0], navController];
            }
        }
    } else {
        navController = self.mainNavigationController;
    }
    
    if ([viewController isKindOfClass:[EventPoolViewController class]]) {
        EventPoolViewController* poolControllerNew = (EventPoolViewController*)viewController;
        if ([navController.topViewController isKindOfClass:[EventPoolViewController class]]) {
            EventPoolViewController* poolControllerCurrent = (EventPoolViewController*)navController.topViewController;
            if ([poolControllerCurrent poolId] == [poolControllerNew poolId]) {
                //already present, just refresh
                [poolControllerCurrent refresh];
                return YES;
            }
        }
    }
    
    if ([viewController isKindOfClass:[EventItemViewController class]]) {
        EventItemViewController* itemControllerNew = (EventItemViewController*)viewController;
        if ([navController.topViewController isKindOfClass:[EventItemViewController class]]) {
            EventItemViewController* itemControllerCurrent = (EventItemViewController*)navController.topViewController;
            if ([itemControllerCurrent itemId] == [itemControllerNew itemId]) {
                //already present, just refresh
                [itemControllerCurrent refresh];
                return YES;
            }
        }
    }
    
    if (!viewController.navigationController) {
        [navController pushViewController:viewController animated:YES];
    }
    return YES;
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Events";
}

#pragma mark - EventsServiceDelegate

- (void)exchangeContactsForRequest:(ExchangeRequest *)request didReturn:(ExchangeReply *)reply {
    switch (reply.status) {
        case 200:
            //perfect
            break;
        case 400:
            [self showExchangeContactError];
            break;
        case 500:
            [self exchangeContactsFailedForRequest:request];
        default:
            break;
    }
}

- (void)exchangeContactsFailedForRequest:(ExchangeRequest *)request {
    [PCUtils showServerErrorAlert];
}

#pragma mark - Private

- (void)showExchangeContactError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ExchangeContactErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}


- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters handleSilent:(BOOL)handleSilent {
    
    UIViewController* viewController = nil;
    
    if (handleSilent) {
        [self handleSilentParameters:parameters];
    }
    
    if ([action isEqualToString:kEventsURLActionShowEventPool]) {
        NSString* poolId = parameters[kEventsURLParameterEventPoolId];
        if (poolId) {
            viewController = [[EventPoolViewController alloc] initAndLoadEventPoolWithId:[poolId longLongValue]];
        }
    } else if ([action isEqualToString:kEventsURLActionShowEventItem]) {
        NSString* eventId = parameters[kEventsURLParameterEventItemId];
        if (eventId) {
            viewController = [[EventItemViewController alloc] initAndLoadEventItemWithId:[eventId longLongValue]];
        }
    } else if (action.length == 0) {
        viewController = [[EventPoolViewController alloc] initAndLoadRootPool];
    }
    
    NSString* eventItemIdToMarkFavorite = parameters[kEventsURLParameterMarkFavoriteEventItemId];
    if (eventItemIdToMarkFavorite) {
        int64_t itemId = [eventItemIdToMarkFavorite longLongValue];
        [self.eventsService addFavoriteEventItemId:itemId];
        viewController = [[EventItemViewController alloc] initAndLoadEventItemWithId:itemId];
        [(EventItemViewController*)viewController setShowFavoriteButton:YES];
    }
    
    return viewController;
}

- (BOOL)handleSilentParameters:(NSDictionary*)parameters {
    
    BOOL found = NO;
    NSString* userToken = parameters[kEventsURLParameterUserTicket];
    if (userToken) {
        found = YES;
        [self.eventsService addUserTicket:userToken];
    }
    
    NSString* exchangeToken = parameters[kEventsURLParameterExchangeToken];
    if (exchangeToken) {
        found = YES;
        ExchangeRequest* req = [[ExchangeRequest alloc] initWithExchangeToken:exchangeToken userToken:nil userTickets:[[self.eventsService allUserTickets] mutableCopy]];
        [self.eventsService exchangeContactsForRequest:req delegate:self];
    }
    
    return found;
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    return NO;
}

#pragma mark - Dealloc

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
