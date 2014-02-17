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

//  Created by Loïc Gardiol on 09.06.13.

#import "CamiproTransactionCell.h"

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
        self.priceLabel.isAccessibilityElement = NO;
        self.dateLabel.isAccessibilityElement = NO;
        self.priceLabel.isAccessibilityElement = NO;
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

#pragma mark - Accessibility

- (NSString*)accessibilityLabel {
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"CamiproTransactionDescriptionWithFormat", @"CamiproPlugin", nil), self.transaction.iPlace, self.transaction.iDate, self.priceLabel.text];
}

@end
