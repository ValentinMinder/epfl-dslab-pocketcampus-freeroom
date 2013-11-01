//
//  MapViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@class MapItem;

@interface MapViewController : UIViewController

- (instancetype)initWithInitialMapItem:(MapItem*)mapItem;
- (instancetype)initWithInitialQuery:(NSString*)query;
- (instancetype)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel;

- (void)startSearchForQuery:(NSString*)query;

//Should not be used but my MapController
@property (nonatomic, strong) NSString* initialQueryWithFullControls;

@end
