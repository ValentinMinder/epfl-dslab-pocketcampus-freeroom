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






//  Created by Loïc Gardiol on 11.10.13.



#import "EPFLTileOverlay2.h"

@implementation EPFLTileOverlay2

- (NSURL *)URLForTilePath:(MKTileOverlayPath)path {
    return [self urlForEpflTilesWithX:path.x andY:path.y andZoom:path.z];
}

- (NSURL*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* urlString = [NSString stringWithFormat:@"http://plan-epfl-tile1.epfl.ch/batiments%d-merc/%d/%@/%@%@", 1, zoom, [self createCoordString:x], [self createCoordString:y], @".png"];
    return [NSURL URLWithString:urlString];
}

- (NSString*)createCoordString:(NSInteger)coord {
    NSString* coordString = [NSString stringWithFormat:@"%09d",coord];
    NSString* firstSubString = [[coordString substringToIndex:3] stringByAppendingString:@"/"];
    NSString* secondSubString = [[[coordString substringFromIndex:3] substringToIndex:3] stringByAppendingString:@"/"];
    NSString* thirdSubString = [coordString substringFromIndex:6];
    NSString* returnString = [NSString stringWithFormat:@"%@%@%@", firstSubString, secondSubString, thirdSubString];
    return returnString;
}

@end
