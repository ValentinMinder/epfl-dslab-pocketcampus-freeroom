

//  Created by Loïc Gardiol on 06.06.12.


#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

@protocol OverlayWithURLs <MKOverlay>

- (NSString *)urlForMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale;
- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;
- (NSString*)identifier;

@end
