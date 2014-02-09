//
//  LGRefreshControl.m
//
//  Created by Loïc Gardiol on 30.10.12.
//  Copyright (c) 2013. All rights reserved.
//

#import "LGRefreshControl.h"

#import "AFNetworkReachabilityManager.h"

@interface LGRefreshControl ()

@property (nonatomic, strong) UITableViewController* strongTableViewController; //used when init with tableview, should retain it
@property (nonatomic, weak, readwrite) UITableViewController* tableViewController;

@property (nonatomic, weak) UITableView* tableView;
@property (nonatomic, strong) UIRefreshControl* refreshControl;
@property (nonatomic, weak) id target;
@property (nonatomic) SEL selector;
@property (nonatomic, copy) NSString* refreshedDataIdentifier;
@property (nonatomic, readwrite, strong) NSDate* lastSuccessfulRefreshDate;
@property (nonatomic, strong) NSTimer* showHideTimer;
@property (nonatomic, readwrite) BOOL isVisible;

@end

@implementation LGRefreshControl


- (id)initWithTableViewController:(UITableViewController*)tableViewController refreshedDataIdentifier:(NSString*)dataIdentifier {
    self = [super init];
    if (self) {
        if (!tableViewController) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"tableviewcontroller cannot be nil" userInfo:nil];
        }
        if (dataIdentifier && dataIdentifier.length == 0) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"refreshedDataIdentifier cannot be not nil with length 0" userInfo:nil];
        }
        self.tableViewController = tableViewController;
        self.refreshedDataIdentifier = dataIdentifier;
        
        self.tableView = self.tableViewController.tableView;
        _showsDefaultRefreshingMessage = YES;
        self.errorMessageColor = [UIColor colorWithRed:0.827451 green:0.000000 blue:0.000000 alpha:1.0];
        if ([self.tableViewController respondsToSelector:@selector(refreshControl)]) { //>= iOS 6
            self.refreshControl = [[UIRefreshControl alloc] init];;
        } else {
            [NSException raise:@"Unsupported platform" format:@"LGRefreshControl requires iOS 6 or higher"];
        }
        self.message = nil;
        self.tableViewController.refreshControl = self.refreshControl;
    }
    return self;
}

/*- (id)initWithTableView:(UITableView*)tableView parentViewController:(UIViewController*)viewController refreshedDataIdentifier:(NSString*)dataIdentifier {
    if (!tableView) {
        [NSException raise:@"Illegal argument" format:@"tableView cannot be nil"];
    }
    if (!viewController) {
        [NSException raise:@"Illegal argument" format:@"viewController cannot be nil. Pass the viewcontroller that hosts your tableview"];
    }
    if (!dataIdentifier || dataIdentifier.length == 0) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"refreshedDataIdentifier cannot be nil or length 0" userInfo:nil];
    }
    self = [super init];
    if (self) {
        _showsDefaultRefreshingMessage = YES;
        self.errorMessageColor = [UIColor colorWithRed:0.827451 green:0.000000 blue:0.000000 alpha:1.0];
        self.strongTableViewController = [[UITableViewController alloc] initWithStyle:tableView.style];
        self.tableViewController = self.strongTableViewController;
        [viewController addChildViewController:self.tableViewController];
        self.tableViewController.refreshControl = [UIRefreshControl new];
        self.refreshControl = self.tableViewController.refreshControl;
        @try {
            self.tableViewController.tableView = tableView;
        }
        @catch (NSException *exception) {
            [NSException exceptionWithName:@"Illegal usage" reason:[NSString stringWithFormat:@"you must use initWithTableViewController if you are using a UITableViewController. (Original Exception: %@)", exception] userInfo:nil];
        }
        self.tableView = tableView;
        self.refreshedDataIdentifier = dataIdentifier;
        if (self.lastSuccessfulRefreshDate) {
            self.message = nil;
        }
    }
    return self;
}*/

- (void)uiRefreshControlValueChanged {
    [self.target performSelectorOnMainThread:self.selector withObject:nil waitUntilDone:YES];
}

- (void)setTarget:(id)target selector:(SEL)selector {
    
    if (!target) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"target cannot be nil" userInfo:nil];
    }
    
    [self.refreshControl removeTarget:self.target action:self.selector forControlEvents:UIControlEventValueChanged];
    [self.refreshControl addTarget:self action:@selector(uiRefreshControlValueChanged) forControlEvents:UIControlEventValueChanged];
    self.target = target;
    self.selector = selector;
}

- (void)startRefreshing {
    if (self.showsDefaultRefreshingMessage) {
        [self startRefreshingWithMessage:NSLocalizedStringFromTable(@"Refreshing", @"LGRefreshControl", nil)];
    } else {
        [self startRefreshingWithMessage:@""];
    }
}

- (void)startRefreshingWithMessage:(NSString*)message {
    [self.showHideTimer invalidate];
    self.message = message;
    if (!self.refreshControl.isRefreshing) {
        [self.refreshControl beginRefreshing];
        [self.tableView setContentOffset:CGPointMake(0, -90.0) animated:YES];
    }
}

- (void)endRefreshing {
    [self.showHideTimer invalidate];
    self.message = nil;
    [self.refreshControl endRefreshing];
}

- (void)endRefreshingAndMarkSuccessful {
    [self endRefreshing];
    [self markRefreshSuccessful];
}

- (void)endRefreshingWithDelay:(NSTimeInterval)delay indicateErrorWithMessage:(NSString*)message {
    [self setProblemMessage:message];
    [self.showHideTimer invalidate];
    self.showHideTimer = [NSTimer scheduledTimerWithTimeInterval:delay target:self selector:@selector(endRefreshing) userInfo:nil repeats:NO];
}

- (NSDate*)lastSuccessfulRefreshDate {
    return [[NSUserDefaults standardUserDefaults] objectForKey:[self keyForLastRefresh]];
}

- (void)setLastSuccessfulRefreshDate:(NSDate*)date {
    [[NSUserDefaults standardUserDefaults] setObject:date forKey:[self keyForLastRefresh]];
}

+ (NSString*)keyForLastRefreshForDataIdentifier:(NSString*)dataIdentifier {
    return [NSString stringWithFormat:@"LGRefreshControlLastRefreshDate-%ud", (unsigned int)[dataIdentifier hash]];
}

- (NSString*)keyForLastRefresh {
    return [self.class keyForLastRefreshForDataIdentifier:self.refreshedDataIdentifier];
}

- (void)markRefreshSuccessful {
    if (!self.refreshedDataIdentifier) {
        @throw [NSException exceptionWithName:@"Illegal operation" reason:@"PCRefreshControl does not support markRefreshSuccessful without being initilized with a nil refreshedDataIdentifier" userInfo:nil];
    }
    self.lastSuccessfulRefreshDate = [NSDate date]; //now
    self.message = nil; //will set last message to default => last refresh message
}

- (BOOL)shouldRefreshDataForValidity:(NSTimeInterval)validitySeconds {
    if (!self.refreshedDataIdentifier) {
        return [PCUtils hasDeviceInternetConnection];
    }
    NSTimeInterval diffWithLastRefresh = [[NSDate date] timeIntervalSinceDate:self.lastSuccessfulRefreshDate];
    if (diffWithLastRefresh > validitySeconds) {
        return [PCUtils hasDeviceInternetConnection];
    }
    return NO;
}

+ (void)deleteRefreshDateInfoForDataIdentifier:(NSString*)dataIdentifier {
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:[self keyForLastRefreshForDataIdentifier:dataIdentifier]];
}

- (void)deleteRefreshDateInfo {
    [self.class deleteRefreshDateInfoForDataIdentifier:[self keyForLastRefresh]];
}

- (UIFont*)fontForMessage {
    return [UIFont preferredFontForTextStyle:UIFontTextStyleFootnote];
}

- (NSMutableAttributedString*)attributedTimeStringForLastRefresh {
    NSMutableAttributedString* attrString = nil;
    if (!self.lastSuccessfulRefreshDate) {
        attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"LastUpdateNever", @"LGRefreshControl", nil)];
    } else if (fabs([self.lastSuccessfulRefreshDate timeIntervalSinceNow]) < 60.0) {
        attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"LastUpdateJustNow", @"LGRefreshControl", nil)];
    } else {
        NSString* lastUpdateLocalized = NSLocalizedStringFromTable(@"LastUpdate", @"LGRefreshControl", nil);
        NSDateFormatter* dateFormatter = [NSDateFormatter new];
        dateFormatter.timeStyle = NSDateFormatterNoStyle;
        dateFormatter.dateStyle = NSDateFormatterShortStyle;
        [dateFormatter setDoesRelativeDateFormatting:YES];
        NSString* dateString = [dateFormatter stringFromDate:self.lastSuccessfulRefreshDate];
        dateFormatter.timeStyle = NSDateFormatterShortStyle;
        dateFormatter.dateStyle = NSDateFormatterNoStyle;
        [dateFormatter setDoesRelativeDateFormatting:NO];
        NSString* timeString = [dateFormatter stringFromDate:self.lastSuccessfulRefreshDate];
       
        attrString = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"%@ %@ %@", lastUpdateLocalized, dateString, timeString]];
    }
    if (self.tintColor) {
        [attrString addAttribute:NSForegroundColorAttributeName value:self.tintColor range:NSMakeRange(0, attrString.string.length)];
    }
    return attrString;
}

- (NSMutableAttributedString*)attributedStringForMessage:(NSString*)message {
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:message];
    if (self.tintColor) {
        [attrString addAttribute:NSForegroundColorAttributeName value:self.tintColor range:NSMakeRange(0, attrString.string.length)];
    }
    return attrString;
}

- (void)setTintColor:(UIColor *)tintColor {
    _tintColor = tintColor;
    //self.refreshControl.tintColor = tintColor;
    NSMutableAttributedString* attrString = ((NSMutableAttributedString*)self.refreshControl.attributedTitle); //we can assume that because UIRefreshControl retains it and we never set a non-mutable instance
    [attrString removeAttribute:NSForegroundColorAttributeName range:NSMakeRange(0, attrString.length)];
    [attrString addAttribute:NSForegroundColorAttributeName value:tintColor range:NSMakeRange(0, attrString.length)];
}

- (void)setMessage:(NSString *)message {
    _message = [message copy];
    NSMutableAttributedString* attrMessage = nil;
    if (message) {
        attrMessage = [self attributedStringForMessage:message];
    } else if (self.refreshedDataIdentifier) {
        attrMessage = [self attributedTimeStringForLastRefresh];
    }
    self.refreshControl.attributedTitle = attrMessage;
}

- (void)setProblemMessage:(NSString *)message {
    self.message = message;
    NSMutableAttributedString* attrString = ((NSMutableAttributedString*)self.refreshControl.attributedTitle); //we can assume that because UIRefreshControl retains it and we never set a non-mutable instance
    [attrString removeAttribute:NSForegroundColorAttributeName range:NSMakeRange(0, attrString.length)];
    [attrString addAttribute:NSForegroundColorAttributeName value:self.errorMessageColor range:NSMakeRange(0, attrString.length)];
    self.refreshControl.attributedTitle = [attrString mutableCopy];
}

- (BOOL)isVisible {
    return self.refreshControl.refreshing;
}

@end
