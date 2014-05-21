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

//  Created by Loïc Gardiol on 12.05.14.

@import MapKit;

@protocol PCScreenTileOverlay <NSObject>

@property (nonatomic, weak, readonly) MKMapView* mapView;

@required

- (instancetype)initWithMapView:(MKMapView*)mapView;

/**
 * Returns wether NOW mapMap is in a state where fetching/drawing mapRect with zoomScale is possible
 * Typically NO if mapRect is outside visibleMapRect or zoomScale not equal to mapView current's zoomScale
 */
- (BOOL)shouldDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;

- (NSURL*)URLForCurrentlyVisibleMapRectAndZoomScale;

/**
 * Should return empty image if zoomScale is not equal to mapView current zoom scale, partial or empty image if mapRect is partially or completely outside
 * or mapView visibleMapRect respectively
 */
- (UIImage*)croppedImageFromCurrentlyVisibleMapRectImage:(UIImage*)image forMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;

@optional

/**
 * No implementation assusmes that all headings are supported
 */
- (BOOL)supportsCameraHeading:(CLLocationDirection)heading;

@end
