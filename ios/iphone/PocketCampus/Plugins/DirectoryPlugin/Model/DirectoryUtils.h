//
//  DirectoryUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "directory.h"

@interface DirectoryUtils : NSObject

+ (UIViewController*)viewControllerForPerson:(Person*)person;

@end
