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

//  Created by Loïc Gardiol on 03.11.14.

#import "FoodMealTypeCell.h"

#import "UIImageView+AFNetworking.h"

#import "FoodService.h"

@interface FoodMealTypeCell ()

@property (nonatomic, weak) IBOutlet UIImageView* imageView;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;

@end

@implementation FoodMealTypeCell

#pragma mark - Init

- (instancetype)init {
    return [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
}

#pragma mark - UICollectionViewCell overrides

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    self.backgroundColor = selected ? [UIColor colorWithWhite:0.0 alpha:0.1] : [UIColor clearColor];
}

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    self.backgroundColor = highlighted ? [UIColor colorWithWhite:0.0 alpha:0.1] : [UIColor clearColor];
}

#pragma mark - Public

- (void)setMealType:(NSInteger)mealType {
    _mealType = mealType;
    [self.imageView cancelImageRequestOperation];
    self.imageView.image = nil;
    UIImage* mealTypeImage = [EpflMeal imageForMealType:mealType];
    
    if (mealTypeImage) {
        self.imageView.image = mealTypeImage;
    } else {
        NSString* urlString = [[FoodService sharedInstanceToRetain] pictureUrlForMealType][@(mealType)];
        if (urlString) {
            NSURLRequest* req = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10.0];
            __weak __typeof(self) welf = self;
            [self.imageView setImageWithURLRequest:req placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
                welf.imageView.image = image;
            } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
                NSLog(@"%@", error);
            }];
        } else {
            self.imageView.image = nil;
        }
    }
    self.titleLabel.text = [EpflMeal localizedNameForMealType:self.mealType];
}

+ (CGSize)preferredSize {
    return CGSizeMake(100.0, 125.0);
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.imageView cancelImageRequestOperation];
}

@end
