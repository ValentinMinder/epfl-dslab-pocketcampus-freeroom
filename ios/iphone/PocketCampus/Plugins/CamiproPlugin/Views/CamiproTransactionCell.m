//
//  CamiproTransactionCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.06.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "CamiproTransactionCell.h"

#import "PCValues.h"

@interface CamiproTransactionCell ()

@property (nonatomic, strong) IBOutlet UILabel* placeLabel;
@property (nonatomic, strong) IBOutlet UILabel* dateLabel;
@property (nonatomic, strong) IBOutlet UILabel* priceLabel;

@property (nonatomic, readwrite, copy) NSString* reuseIdentifier;

@end

@implementation CamiproTransactionCell

- (id)initWithRuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"CamiproTransactionCell" owner:self options:nil];
    self = (CamiproTransactionCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.placeLabel.textColor = [PCValues textColor1];
    }
    return self;
}

- (void)setTransaction:(Transaction *)transaction {
    _transaction = transaction;
    self.placeLabel.text = self.transaction.iPlace;
    self.dateLabel.text = self.transaction.iDate;
    self.priceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", self.transaction.iAmount];
    if (self.transaction.iAmount > 0.0) {
        self.priceLabel.textColor = [UIColor colorWithRed:0.09 green:0.79 blue:0 alpha:1.0]; //light green
    } else {
        self.priceLabel.textColor = [PCValues pocketCampusRed];
    }
}

@end
