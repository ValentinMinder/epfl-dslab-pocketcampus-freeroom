



//  Created by Loïc Gardiol on 09.06.13.



#import "CamiproTransactionCell.h"

#import "PCValues.h"

@interface CamiproTransactionCell ()

@property (nonatomic, strong) IBOutlet UILabel* placeLabel;
@property (nonatomic, strong) IBOutlet UILabel* dateLabel;
@property (nonatomic, strong) IBOutlet UILabel* priceLabel;

@property (nonatomic, readwrite, copy) NSString* reuseIdentifier;

@end

@implementation CamiproTransactionCell

@synthesize reuseIdentifier = _reuseIdentifier;

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"CamiproTransactionCell" owner:self options:nil];
    self = (CamiproTransactionCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    return self;
}

- (void)setTransaction:(Transaction *)transaction {
    _transaction = transaction;
    self.placeLabel.text = self.transaction.iPlace;
    self.dateLabel.text = self.transaction.iDate;
    if (self.transaction.iAmount > 0.0) {
        self.priceLabel.textColor = [UIColor colorWithRed:0.09 green:0.79 blue:0 alpha:1.0]; //light green
        self.priceLabel.text = [NSString stringWithFormat:@"+ CHF %.2lf", self.transaction.iAmount];
    } else {
        self.priceLabel.textColor = [UIColor darkGrayColor];
        self.priceLabel.text = [NSString stringWithFormat:@"- CHF %.2lf", fabs(self.transaction.iAmount)];
    }
}

@end
