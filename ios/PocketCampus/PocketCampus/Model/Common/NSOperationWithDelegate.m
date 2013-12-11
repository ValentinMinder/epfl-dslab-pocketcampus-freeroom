//
//  NSOperationWithDelegate.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.12.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "NSOperationWithDelegate.h"

#import "NSOperationWithDelegate_Protected.h"

@implementation NSOperationWithDelegate

- (id)initWithDelegate:(id)delegate {
    self = [super init];
    if (self) {
        self.delegate = delegate;
    }
    return self;
}

- (BOOL)delegateRespondsToSelector:(SEL)selector {
    if ([self isCancelled]) {
        return NO;
    }
    if (![self.delegate respondsToSelector:selector]) {
        return NO;
    }
    return YES;
}

- (BOOL)isExecuting {
    return self.executing;
}

- (void)setExecuting:(BOOL)executing {
    [self willChangeValueForKey:@"isExecuting"];
    _executing = executing;
    [self didChangeValueForKey:@"isExecuting"];
}

- (BOOL)isFinished {
    return self.finished;
}

- (void)setFinished:(BOOL)finished {
    [self willChangeValueForKey:@"isFinished"];
    _finished = finished;
    [self didChangeValueForKey:@"isFinished"];
}

- (BOOL)isCancelled {
    return self.cancelled;
}

- (void)setCancelled:(BOOL)cancelled {
    [self willChangeValueForKey:@"isCancelled"];
    _cancelled = cancelled;
    [self didChangeValueForKey:@"isCancelled"];
}

@end
