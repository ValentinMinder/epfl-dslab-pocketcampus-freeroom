

//  Created by Loïc Gardiol on 06.06.12.


#import <UIKit/UIKit.h>

#import <MapKit/MapKit.h>

#import "OverlayWithURLs.h"

@interface EPFLLayersOverlay : NSObject<OverlayWithURLs> {
    NSInteger currentLayerLevel;
}

@property (nonatomic, assign) MKMapView* mapView;

- (void)increaseLayerLevel;
- (void)decreaseLayerLevel;
- (void)setLayerLevel:(NSInteger)newLevel;

@end
