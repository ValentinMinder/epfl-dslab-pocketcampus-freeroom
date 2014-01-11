



//  Created by Loïc Gardiol on 01.11.13.



#import <UIKit/UIKit.h>

/*
 * Posted by self everytime rateModeEnabled goes from NO to YES
 */
extern NSString* const kFoodMealCellDidEnableRateModeNotification;

/*
 * Posted by self when the user has rated a meal
 */
extern NSString* const kFoodMealCellUserSuccessfullyRatedMealNotification;


@class EpflMeal;

@interface FoodMealCell : UITableViewCell

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier;

@property (nonatomic, strong) EpflMeal* meal;

+ (CGFloat)preferredHeightForMeal:(EpflMeal*)meal;

/*
 * If YES, cell's content is shifted to the right and reveals rating controls
 * Default: NO
 */
@property (nonatomic, getter = isRateModeEnabled) BOOL rateModeEnabled;

@end
