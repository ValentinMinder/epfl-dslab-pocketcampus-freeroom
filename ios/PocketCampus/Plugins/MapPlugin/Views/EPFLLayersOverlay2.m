//
//  EPFLLayersOverlay2.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.05.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "EPFLLayersOverlay2.h"

@implementation EPFLLayersOverlay2

#pragma mark - MKTileOverlay

- (NSURL*)URLForTilePath:(MKTileOverlayPath)path {
    
}

#pragma mark - Private



/*- (NSString*)urlForEpflLayerWithCH1903StartX:(double)startX startY:(double)startY endX:(double)endX endY:(double)endY width:(double)width height:(double)height  {
    NSString* baseURLWithBBoxEmptyParameter = @"http://plan.epfl.ch/wms_themes?FORMAT=image/png&LOCALID=-1&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A21781&BBOX=";
    
    NSString* urlString = [NSString stringWithFormat:@"%@%lf,%lf,%lf,%lf&WIDTH=%.0lf&HEIGHT=%.0lf&LAYERS=locaux_labels_en%d,batiments_routes_labels,parkings_publicsall,informationall", baseURLWithBBoxEmptyParameter, startY, endX, endY, startX, width, height, (int)currentLayerLevel, (int)currentLayerLevel];
    return urlString;
}*/

@end
