



//  Created by Loïc Gardiol on 09.06.13.



#import <UIKit/UIKit.h>

#import "camipro.h"

@interface CamiproTransactionCell : UITableViewCell

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier;

@property (nonatomic, strong) Transaction* transaction;

@end
