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

//  Created by Loïc Gardiol on 07.09.14.

#import "CloudPrintMultiPageLayoutButton.h"

@interface CloudPrintMultiPageLayoutButton ()

@property (nonatomic, weak) IBOutlet UIView* rect1;
@property (nonatomic, weak) IBOutlet UIView* rect2;
@property (nonatomic, weak) IBOutlet UIView* rect3;
@property (nonatomic, weak) IBOutlet UIView* rect4;

@property (nonatomic, weak) IBOutlet UILabel* label1;
@property (nonatomic, weak) IBOutlet UILabel* label2;
@property (nonatomic, weak) IBOutlet UILabel* label3;
@property (nonatomic, weak) IBOutlet UILabel* label4;

@property (nonatomic, readwrite) NSInteger multiPageLayout;

@end

@implementation CloudPrintMultiPageLayoutButton

#pragma mark - Init

- (instancetype)initWithMultiPageLayout:(NSInteger)multiPageLayout {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.multiPageLayout = multiPageLayout;
        self.autoresizingMask = UIViewAutoresizingNone;
        self.layer.borderWidth = 0.0;
        self.layer.cornerRadius = 3.0;
        self.bounds = CGRectMake(0, 0, 70.0, 60.0);
        CGFloat rectBorderWidth = 1.0;
        self.rect1.layer.borderWidth = rectBorderWidth;
        self.rect2.layer.borderWidth = rectBorderWidth;
        self.rect3.layer.borderWidth = rectBorderWidth;
        self.rect4.layer.borderWidth = rectBorderWidth;
        
        self.rect1.backgroundColor = self.rect2.backgroundColor = self.rect3.backgroundColor = self.rect4.backgroundColor = [UIColor clearColor];
        
        static NSString* one;
        static NSString* two;
        static NSString* three;
        static NSString* four;
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            one = [NSString stringWithFormat:@"%d", 1];
            two = [NSString stringWithFormat:@"%d", 2];
            three = [NSString stringWithFormat:@"%d", 3];
            four = [NSString stringWithFormat:@"%d", 4];
        });
        switch (multiPageLayout) {
            case CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM:
                self.label1.text = one;
                self.label2.text = two;
                self.label3.text = three;
                self.label4.text = four;
                break;
            case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_LEFT_TO_RIGHT:
                self.label1.text = one;
                self.label3.text = two;
                self.label2.text = three;
                self.label4.text = four;
                break;
            case CloudPrintMultiPageLayout_RIGHT_TO_LEFT_TOP_TO_BOTTOM:
                self.label2.text = one;
                self.label1.text = two;
                self.label4.text = three;
                self.label3.text = four;
                break;
            case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_RIGHT_TO_LEFT:
                self.label2.text = one;
                self.label4.text = two;
                self.label1.text = three;
                self.label3.text = four;
                break;
            default:
                break;
        }
    }
    return self;
}

#pragma mark - UIControl overrides

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    self.layer.borderWidth = selected ? 2.0 : 0.0;
}

#pragma mark - UIView overrides

- (void)tintColorDidChange {
    [super tintColorDidChange];
    self.layer.borderColor = self.tintColor.CGColor;
    self.rect1.layer.borderColor = self.tintColor.CGColor;
    self.rect2.layer.borderColor = self.tintColor.CGColor;
    self.rect3.layer.borderColor = self.tintColor.CGColor;
    self.rect4.layer.borderColor = self.tintColor.CGColor;
    self.label1.textColor = self.tintColor;
    self.label2.textColor = self.tintColor;
    self.label3.textColor = self.tintColor;
    self.label4.textColor = self.tintColor;
}

@end
