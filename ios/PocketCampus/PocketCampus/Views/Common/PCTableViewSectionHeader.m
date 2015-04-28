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

//  Created by Loïc Gardiol on 16.03.12.

#import "PCTableViewSectionHeader.h"

static UIColor* kDefaultTextColor;
static UIColor* kHighlightedTextColor;
static UIColor* kDefaultBarTintColor;
static UIColor* kHighlightedBarTintColor;

@interface PCTableViewSectionHeader ()

@property (nonatomic, readwrite) UITableView* tableView;
@property (nonatomic, weak) UINavigationBar* navBar;
@property (nonatomic, weak) UILabel* titleLabel;
@property (nonatomic, weak) UIButton* infoButton;

@end

@implementation PCTableViewSectionHeader

#pragma mark - Init

+ (void)initialize {
    if (self == [PCTableViewSectionHeader self]) {
        kDefaultTextColor = [UIColor colorWithWhite:0.0 alpha:0.8];
        kHighlightedTextColor = [UIColor colorWithRed:0 green:0.5 blue:1.0 alpha:1.0];
        kDefaultBarTintColor = nil;
        kHighlightedBarTintColor = nil;
    }
}

- (id)initWithSectionTitle:(NSString*)sectionTitle tableView:(UITableView*)tableView {
    return [self initWithSectionTitle:sectionTitle tableView:tableView showInfoButton:NO];
}

- (id)initWithSectionTitle:(NSString*)sectionTitle tableView:(UITableView*)tableView showInfoButton:(BOOL)showInfoButton {
    [PCUtils throwExceptionIfObject:sectionTitle notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:tableView notKindOfClass:[UITableView class]];
    CGFloat height = [self.class preferredHeightWithInfoButton:showInfoButton];
    self = [super initWithFrame:CGRectMake(0, 0, tableView.frame.size.width, height)];
    if (self) {
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        //self.userInteractionEnabled = NO; //otherwise intercepts scrolling touches in tableview
        self.tableView = tableView;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        
        UINavigationBar* navBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, -10, self.frame.size.width, self.frame.size.height+20)];
        navBar.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        navBar.translucent = YES;
        navBar.barTintColor = kDefaultBarTintColor;
        navBar.userInteractionEnabled = NO;
        self.navBar = navBar;
        
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(self.tableView.separatorInset.left, 0, self.frame.size.width - (showInfoButton ? 56.0 : 0.0), self.frame.size.height)];
        label.autoresizingMask = UIViewAutoresizingFlexibleHeight;
        label.text = sectionTitle;
        label.backgroundColor = [UIColor clearColor];
        label.font = [self.class fontForTitleLabel];
        label.textColor = kDefaultTextColor;
        self.titleLabel = label;
        
        [self addSubview:navBar];
        [self addSubview:label];
        
        if (showInfoButton) {
            UIButton* infoButton = [UIButton buttonWithType:UIButtonTypeSystem];
            [infoButton setAttributedTitle:[self.class attributedTitleForInfoButton] forState:UIControlStateNormal];
            infoButton.bounds = CGRectMake(0, 0, 56.0, self.frame.size.height);
            [infoButton addTarget:self action:@selector(infoButtonTapped) forControlEvents:UIControlEventTouchUpInside];
            infoButton.frame = CGRectMake(self.frame.size.width - infoButton.frame.size.width, (self.frame.size.height - infoButton.frame.size.height)/2.0, infoButton.frame.size.width, infoButton.frame.size.height);
            infoButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
            self.infoButton = infoButton;
            [self addSubview:infoButton];
        }
    }
    return self;
}

#pragma mark - Public

- (void)setHighlighted:(BOOL)highlighted {
    _highlighted = highlighted;
    self.navBar.barTintColor = highlighted ? kHighlightedBarTintColor : kDefaultBarTintColor;
    self.titleLabel.textColor = highlighted ? kHighlightedTextColor : kDefaultTextColor;
}

- (UIColor*)backgroundTintColor {
    return self.navBar.barTintColor;
}

- (void)setBackgroundTintColor:(UIColor *)backgroundTintColor {
    _highlighted = NO;
    self.navBar.tintColor = backgroundTintColor ?: kDefaultBarTintColor;
}

- (UIColor*)textColor {
    return self.titleLabel.textColor;
}

- (void)setTextColor:(UIColor *)textColor {
    _highlighted = NO;
    self.titleLabel.textColor = textColor ?: kDefaultTextColor;
}

+ (CGFloat)preferredHeight {
    return [self preferredHeightWithInfoButton:NO];
}

+ (CGFloat)preferredHeightWithInfoButton:(BOOL)withInfoButton {
    static CGFloat height = 0.0;
    static CGFloat heightWithInfoButton = 0.0;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:[UIApplication sharedApplication] queue:nil usingBlock:^(NSNotification *note) {
            height = 0.0;
            heightWithInfoButton = 0.0;
        }];
    });
    if (height == 0.0) {
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, FLT_MAX, FLT_MAX)];
        label.text = @"test";
        label.font = [self fontForTitleLabel];
        [label sizeToFit];
        height = (CGFloat)((int)(label.frame.size.height * 1.4f));
        heightWithInfoButton = height < 32.0 ? 32.0 : height;
    }
    return withInfoButton ? heightWithInfoButton : height;
}

#pragma mark - Actions

- (void)infoButtonTapped {
    if (self.infoButtonTappedBlock) {
        self.infoButtonTappedBlock();
    }
}

#pragma mark - Private

+ (UIFont*)fontForTitleLabel {
    static UIFont* font = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
            font = nil;
        }];
    });
    if (!font) {
        UIFontDescriptor* fontDescriptor = [UIFontDescriptor preferredFontDescriptorWithTextStyle:UIFontTextStyleFootnote];
        font = [UIFont boldSystemFontOfSize:fontDescriptor.pointSize];
    }
    return font;
}

+ (NSAttributedString*)attributedTitleForInfoButton {
    static NSAttributedString* attrTitle = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
            attrTitle = nil;
        }];
    });
    if (!attrTitle) {
        UIFontDescriptor* fontDescriptor = [UIFontDescriptor preferredFontDescriptorWithTextStyle:UIFontTextStyleFootnote];
        UIFont* font = [UIFont systemFontOfSize:fontDescriptor.pointSize+2.0];
        attrTitle = [[NSAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"Info", @"PocketCampus", nil) attributes:@{NSFontAttributeName:font}];
    }
    return attrTitle;
}


@end
