//
//  CamiproTransactionCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.06.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "camipro.h"

@interface CamiproTransactionCell : UITableViewCell

- (id)initWithRuseIdentifier:(NSString *)reuseIdentifier;

@property (nonatomic, strong) Transaction* transaction;

@end
