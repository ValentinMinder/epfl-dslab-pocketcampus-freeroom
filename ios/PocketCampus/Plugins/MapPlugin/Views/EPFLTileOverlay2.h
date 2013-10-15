//
//  EPFLTileOverlay2.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <MapKit/MapKit.h>

/*
 * This overlay is an attempt to use the MKTileOverlay class to simplify the download of tiles (instead of using RemoveOverlayRenderer).
 * This is not working yet, the implementation is not complete.
 */

@interface EPFLTileOverlay2 : MKTileOverlay

@end
