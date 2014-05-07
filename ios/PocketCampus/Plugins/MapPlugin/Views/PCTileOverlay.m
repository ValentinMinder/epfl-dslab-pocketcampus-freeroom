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

//  Created by Loïc Gardiol on 07.05.14.

#import "PCTileOverlay.h"

@implementation PCTileOverlay

#pragma mark - Init

- (instancetype)init {
    self = [super init];
    if (self) {
        self.floorLevel = self.defaultFloorLevel;
    }
    return self;
}

#pragma mark - Public

- (void)setFloorLevel:(NSInteger)floorLevel {
    if (floorLevel > self.maxFloorLevel) {
        _floorLevel = self.maxFloorLevel;
    } else if (floorLevel < self.minFloorLevel) {
        _floorLevel = self.minFloorLevel;
    } else {
        _floorLevel = floorLevel;
    }
}

- (void)increaseFloorLevel {
    self.floorLevel = (self.floorLevel+1);
}

- (void)decreaseFloorLevel {
    self.floorLevel = (self.floorLevel-1);
}

#pragma mark Default implementations

- (NSInteger)defaultFloorLevel {
    return 0;
}

- (NSInteger)maxFloorLevel {
    return 100;
}

- (NSInteger)minFloorLevel {
    return 100;
}

- (CLLocationDistance)floorLevelsMaxAltitude {
    return 10000.0;
}

- (CGFloat)desiredAlpha {
    return 1.0;
}

@end
