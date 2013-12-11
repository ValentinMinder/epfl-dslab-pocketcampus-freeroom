//
//  NSOperationWithDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.12.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

@import Foundation;

@interface NSOperationWithDelegate : NSOperation

@property (weak) id delegate;
@property SEL delegateDidReturnSelector;
@property SEL delegateDidFailSelector;

- (id)initWithDelegate:(id)delegate;
- (BOOL)delegateRespondsToSelector:(SEL)selector __attribute__((deprecated));

@end
