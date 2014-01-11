



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
