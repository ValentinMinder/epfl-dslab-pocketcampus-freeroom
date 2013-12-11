//
//  NSOperationWithDelegate_Protected.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.12.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "NSOperationWithDelegate.h"

@interface NSOperationWithDelegate ()

@property (nonatomic) BOOL executing;
@property (nonatomic) BOOL finished;
@property (nonatomic) BOOL cancelled;

@end
