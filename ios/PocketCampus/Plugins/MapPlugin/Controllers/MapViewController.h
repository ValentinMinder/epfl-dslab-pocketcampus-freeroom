

//  Created by Loïc Gardiol on 12.04.12.


@class MapItem;

@interface MapViewController : UIViewController

- (instancetype)initWithInitialMapItem:(MapItem*)mapItem;
- (instancetype)initWithInitialQuery:(NSString*)query;
- (instancetype)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel;

- (void)startSearchForQuery:(NSString*)query;

//Should not be used but my MapController
@property (nonatomic, strong) NSString* initialQueryWithFullControls;

@end
