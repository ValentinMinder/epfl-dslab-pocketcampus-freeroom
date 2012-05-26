//
//  TransportController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportController.h"

#import "NextDeparturesListViewController.h"

@implementation TransportController

static NSString* name = nil;

- (id)initWithMainController:(MainController*)mainController_ {
    self = [super init];
    if (self) {
        mainController = mainController_;
        NextDeparturesListViewController* viewController = [[NextDeparturesListViewController alloc] init];
        viewController.title = [[self class] localizedName];
        mainViewController = viewController;
    }
    return self;
}

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"TransportPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Transport";
}

- (void)refresh {
    if (mainViewController == nil || ![mainViewController isKindOfClass:[NextDeparturesListViewController class]]) {
        return;
    }
    [(NextDeparturesListViewController*)mainViewController refresh];
}

- (void)dealloc {
    [name release];
    name = nil;
    [super dealloc];
}

@end
