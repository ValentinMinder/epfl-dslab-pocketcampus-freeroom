//
//  PluginController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

@implementation PluginController

@synthesize toolbarItems, mainViewController;

- (void)dealloc
{
    [toolbarItems release];
    [mainViewController release];
    [super dealloc];
}

@end
