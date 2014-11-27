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

#import "CloudPrintMultiPageLayoutCell.h"

#import "CloudPrintMultiPageLayoutButton.h"

static CGFloat const kButtonsLeftMargin = 12.0;
static CGFloat const kButtonsTopMargin = 36.0;
static CGFloat const kButtonsWidth = 60.0;
static CGFloat const kButtonsHeight = 70.0;

@interface CloudPrintMultiPageLayoutCell ()

@property (nonatomic, copy) NSString* _reuseIdentifier;

@property (nonatomic, strong) CloudPrintMultiPageLayoutButton* leftRightTopBottomButton;
@property (nonatomic, strong) CloudPrintMultiPageLayoutButton* topBottomLeftRightButton;
@property (nonatomic, strong) CloudPrintMultiPageLayoutButton* rightLeftTopBottomButton;
@property (nonatomic, strong) CloudPrintMultiPageLayoutButton* topBottomRightLeftButton;

@end

@implementation CloudPrintMultiPageLayoutCell

#pragma mark - Init

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.leftRightTopBottomButton = [[CloudPrintMultiPageLayoutButton alloc] initWithMultiPageLayout:CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM];
        self.topBottomLeftRightButton = [[CloudPrintMultiPageLayoutButton alloc] initWithMultiPageLayout:CloudPrintMultiPageLayout_TOP_TO_BOTTOM_LEFT_TO_RIGHT];
        self.rightLeftTopBottomButton = [[CloudPrintMultiPageLayoutButton alloc] initWithMultiPageLayout:CloudPrintMultiPageLayout_RIGHT_TO_LEFT_TOP_TO_BOTTOM];
        self.topBottomRightLeftButton = [[CloudPrintMultiPageLayoutButton alloc] initWithMultiPageLayout:CloudPrintMultiPageLayout_TOP_TO_BOTTOM_RIGHT_TO_LEFT];
        
        self.leftRightTopBottomButton.frame = CGRectMake(15.0, kButtonsTopMargin, kButtonsWidth, kButtonsHeight);
        self.topBottomLeftRightButton.frame = CGRectMake(self.leftRightTopBottomButton.frame.origin.x + kButtonsWidth + kButtonsLeftMargin, kButtonsTopMargin, kButtonsWidth, kButtonsHeight);
        self.rightLeftTopBottomButton.frame = CGRectMake(self.topBottomLeftRightButton.frame.origin.x + kButtonsWidth + + kButtonsLeftMargin, kButtonsTopMargin, kButtonsWidth, kButtonsHeight);
        self.topBottomRightLeftButton.frame = CGRectMake(self.rightLeftTopBottomButton.frame.origin.x + kButtonsWidth + + kButtonsLeftMargin, kButtonsTopMargin, kButtonsWidth, kButtonsHeight);
        
        [self.leftRightTopBottomButton addTarget:self action:@selector(layoutButtonTapped:) forControlEvents:UIControlEventTouchDown];
        [self.topBottomLeftRightButton addTarget:self action:@selector(layoutButtonTapped:) forControlEvents:UIControlEventTouchDown];
        [self.rightLeftTopBottomButton addTarget:self action:@selector(layoutButtonTapped:) forControlEvents:UIControlEventTouchDown];
        [self.topBottomRightLeftButton addTarget:self action:@selector(layoutButtonTapped:) forControlEvents:UIControlEventTouchDown];
        
        [self.contentView addSubview:self.leftRightTopBottomButton];
        [self.contentView addSubview:self.topBottomLeftRightButton];
        [self.contentView addSubview:self.rightLeftTopBottomButton];
        [self.contentView addSubview:self.topBottomRightLeftButton];
    }
    return self;
}

#pragma mark - UITableViewCell overrides

- (NSString*)reuseIdentifier {
    return self._reuseIdentifier;
}

#pragma mark - Public

- (void)setSelectedMultiPageLayout:(NSInteger)selectedMultiPageLayout {
    _selectedMultiPageLayout = selectedMultiPageLayout;
    self.leftRightTopBottomButton.selected = NO;
    self.topBottomLeftRightButton.selected = NO;
    self.rightLeftTopBottomButton.selected = NO;
    self.topBottomRightLeftButton.selected = NO;
    switch (selectedMultiPageLayout) {
        case CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM:
            self.leftRightTopBottomButton.selected = YES;
            break;
        case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_LEFT_TO_RIGHT:
            self.topBottomLeftRightButton.selected = YES;
            break;
        case CloudPrintMultiPageLayout_RIGHT_TO_LEFT_TOP_TO_BOTTOM:
            self.rightLeftTopBottomButton.selected = YES;
            break;
        case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_RIGHT_TO_LEFT:
            self.topBottomRightLeftButton.selected = YES;
            break;
        default:
            break;
    }
}

+ (CGFloat)preferredHeight {
    return 120.0;
}

#pragma mark - Actions

- (void)layoutButtonTapped:(id)sender {
    CloudPrintMultiPageLayoutButton* button = (CloudPrintMultiPageLayoutButton*)sender;
    self.leftRightTopBottomButton.selected = NO;
    self.topBottomLeftRightButton.selected = NO;
    self.rightLeftTopBottomButton.selected = NO;
    self.topBottomRightLeftButton.selected = NO;
    button.selected = YES;
    
    NSInteger config = 0;
    if (sender == self.leftRightTopBottomButton) {
        config = CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM;
    } else if (sender == self.topBottomLeftRightButton) {
        config = CloudPrintMultiPageLayout_TOP_TO_BOTTOM_LEFT_TO_RIGHT;
    } else if (sender == self.rightLeftTopBottomButton) {
        config = CloudPrintMultiPageLayout_RIGHT_TO_LEFT_TOP_TO_BOTTOM;
    } else if (sender == self.topBottomRightLeftButton) {
        config = CloudPrintMultiPageLayout_TOP_TO_BOTTOM_RIGHT_TO_LEFT;
    }
    
    if (self.userSelectedMultiPageLayout) {
        self.userSelectedMultiPageLayout(config);
    }
}

@end
