//
//  MapViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@interface MapViewController : UIViewController

- (id)initWithInitialQuery:(NSString*)query;
- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel;

- (void)startSearchForQuery:(NSString*)query;

@property (nonatomic, strong) NSString* initialQueryWithFullControls;

@end
