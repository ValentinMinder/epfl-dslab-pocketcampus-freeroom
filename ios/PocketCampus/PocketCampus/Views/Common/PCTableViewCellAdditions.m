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




//  Created by Loïc Gardiol on 16.12.12.


#import "PCTableViewCellAdditions.h"

static CGFloat kCellHeightForDefaultPreferredContentSizeCategory = 44.0;

NSString* PCTableViewCellAdditionsDefaultTextLabelTextStyle;
NSString* PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle;

@interface PCTableViewCellAdditions ()

@property (nonatomic, strong) UIImageView* icon;
@property (nonatomic, strong) UIColor* originalBackgroundColor;
@property (nonatomic, strong) UIColor* originalTextLabelColor;
@property (nonatomic, strong) UIColor* originalDetailTextLabelColor;

@property (atomic) BOOL textObserversAdded;

@end

static __strong UIColor* kDefaultTextLabelDimmedColor ;
static __strong UIColor* kDefaultDetailTextLabelDimmedColor;

@implementation PCTableViewCellAdditions

#pragma mark - Init

//see NSObject doc
+ (void)initialize {
    PCTableViewCellAdditionsDefaultTextLabelTextStyle = UIFontTextStyleBody;
    PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle = UIFontTextStyleFootnote;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            kDefaultTextLabelDimmedColor = [UIColor grayColor];
            kDefaultDetailTextLabelDimmedColor = [UIColor grayColor];
        });
        self.icon = [[UIImageView alloc]  initWithImage:[UIImage imageNamed:@"DownloadedCorner"]];
        self.icon.frame = CGRectMake(self.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
        [self addSubview:self.icon];
        self.icon.hidden = YES; //no indication by default
        self.originalBackgroundColor = self.backgroundColor;
        self.originalTextLabelColor = self.textLabel.textColor;
        self.originalDetailTextLabelColor = self.detailTextLabel.textColor;
        self.textLabel.backgroundColor = [UIColor clearColor];
        self.detailTextLabel.backgroundColor = [UIColor clearColor];
        self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    }
    return self;
}

#pragma mark - Public properties

- (void)setDownloadedIndicationVisible:(BOOL)visible {
    _downloadedIndicationVisible = visible;
    [self setNeedsDisplay];
    [self updateCornerIcon];
}

- (void)setFavoriteIndicationVisible:(BOOL)visible {
    _favoriteIndicationVisible = visible;
    [self setNeedsDisplay];
    [self updateCornerIcon];
}

- (void)setDurablySelected:(BOOL)durablySelected {
    _durablySelected = durablySelected;
    if (durablySelected) {
        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.85 alpha:1.0];
    } else {
        self.backgroundView.backgroundColor = self.originalBackgroundColor;
    }
    [self updateCornerIcon];
    [self setNeedsDisplay];
}

- (void)setTextLabelHighlightedRegex:(NSRegularExpression *)textLabelHighlightedRegex {
    if (_textLabelHighlightedRegex == textLabelHighlightedRegex) {
        return;
    }
    _textLabelHighlightedRegex = textLabelHighlightedRegex;
    [self updateTextLabelHighlighting];
}

- (void)setTextLabelDimmedColor:(UIColor *)textLabelDimmedColor {
    _textLabelDimmedColor = textLabelDimmedColor;
    [self updateTextLabelHighlighting];
}

- (void)setDetailTextLabelHighlightedRegex:(NSRegularExpression *)detailTextLabelHighlightedRegex {
    if (_detailTextLabelHighlightedRegex == detailTextLabelHighlightedRegex) {
        return;
    }
    _detailTextLabelHighlightedRegex = detailTextLabelHighlightedRegex;
    [self updateDetailTextLabelHighlighting];
}

- (void)setDetailTextLabelDimmedColor:(UIColor *)detailTextLabelDimmedColor {
    _detailTextLabelDimmedColor = detailTextLabelDimmedColor;
    [self updateDetailTextLabelHighlighting];
}

- (void)setAccessoryViewViaContentView:(UIView *)view {
    if (view && view == _accessoryViewViaContentView && self.contentView == _accessoryViewViaContentView.superview) {
        return;
    }
    [_accessoryViewViaContentView removeFromSuperview];
    _accessoryViewViaContentView = view;
    if (view) {
        [self.contentView addSubview:view];
        [self.contentView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.contentView forView:view edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, kNoInsetConstraint, kNoInsetConstraint, -15.0)]];
        [self.contentView addConstraint:[NSLayoutConstraint constraintForCenterYtoSuperview:self.contentView forView:view constant:0.0]];
    }
}

#pragma mark - Public methods

+ (CGFloat)preferredHeightForStyle:(UITableViewCellStyle)style textLabelTextStyle:(NSString*)textLabelTextStyle detailTextLabelTextStyle:(NSString*)detailTextLabelTextStyle {
    UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, FLT_MAX, FLT_MAX)];
    label.text = @"test";
    CGFloat textLabelHeight = 0.0;
    if (textLabelTextStyle) {
        [PCUtils throwExceptionIfObject:textLabelTextStyle notKindOfClass:[NSString class]];
        label.font = [UIFont preferredFontForTextStyle:textLabelTextStyle];
        [label sizeToFit];
        textLabelHeight = label.frame.size.height;
    }
    CGFloat detailTextLabelHeight = 0.0;
    if (detailTextLabelTextStyle) {
        [PCUtils throwExceptionIfObject:detailTextLabelTextStyle notKindOfClass:[NSString class]];
        label.font = [UIFont preferredFontForTextStyle:detailTextLabelTextStyle];
        [label sizeToFit];
        detailTextLabelHeight = label.frame.size.height;
    }
    CGFloat totalHeight = style == UITableViewCellStyleSubtitle ? textLabelHeight + detailTextLabelHeight : MAX(textLabelHeight, detailTextLabelHeight);
    CGFloat preferredHeight = totalHeight * 1.0f;
    CGFloat minHeight = [self minHeightForCurrentPreferredContentSizeCategory];
    preferredHeight = preferredHeight < minHeight ? minHeight : preferredHeight;
    preferredHeight = floorf(preferredHeight);
    return preferredHeight;
}

+ (CGFloat)preferredHeightForDefaultTextStylesForCellStyle:(UITableViewCellStyle)style {
    return [self preferredHeightForStyle:style textLabelTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle detailTextLabelTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
}

#pragma mark - UIView overrides

- (void)layoutSubviews {
    [super layoutSubviews];
    self.icon.frame = CGRectMake(self.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
}

#pragma mark - UITableViewCell overrides

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    [self updateCornerIcon];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    [self updateCornerIcon];
}

- (void)willTransitionToState:(UITableViewCellStateMask)state {
    [super willTransitionToState:state];
    if (state != UITableViewCellStateDefaultMask) {
        self.icon.alpha = 0.0;
    }
}

- (void)didTransitionToState:(UITableViewCellStateMask)state {
    [super didTransitionToState:state];
    if (state == UITableViewCellStateDefaultMask) {
        self.icon.alpha = 1.0;
    }
}

#pragma mark - NSObject overrides

- (NSString*)description {
    return [[super description] stringByAppendingString:self.textLabel.text];
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if ([keyPath isEqualToString:NSStringFromSelector(@selector(text))]) {
        if (object == self.textLabel) {
            [self updateTextLabelHighlighting];
        } else if (object == self.detailTextLabel) {
            [self updateDetailTextLabelHighlighting];
        }
    }
}

#pragma mark - Accessibility

- (BOOL)isAccessibilityElement {
    return self.accessibilityDisabled ? NO : [super isAccessibilityElement];
}

- (NSInteger)accessibilityElementCount {
    return self.accessibilityDisabled ? 0 : [super accessibilityElementCount];
}

- (NSString*)accessibilityLabel {
    return self.accessibilityLabelBlock ? self.accessibilityLabelBlock() : [super accessibilityLabel];
}

- (NSString*)accessibilityHint {
    return self.accessibilityHintBlock ? self.accessibilityHintBlock() : [super accessibilityHint];
}

- (NSString*)accessibilityValue {
    return self.accessibilityValueBlock ? self.accessibilityValueBlock() : [super accessibilityValue];
}

- (UIAccessibilityTraits)accessibilityTraits {
    return self.accessibilityTraitsBlock ? self.accessibilityTraitsBlock() : [super accessibilityTraits];
}

#pragma mark - Private

- (void)updateCornerIcon {
    if (self.selected || self.highlighted || self.durablySelected) {
        if (self.favoriteIndicationVisible) {
            self.icon.image = [UIImage imageNamed:@"FavoriteCornerSelected"];
        } else {
            self.icon.image = [UIImage imageNamed:@"DownloadedCornerSelected"];
        }
    } else {
        if (self.favoriteIndicationVisible) {
            self.icon.image = [UIImage imageNamed:@"FavoriteCorner"];
        } else {
            self.icon.image = [UIImage imageNamed:@"DownloadedCorner"];
        }
    }
    self.icon.hidden = !(self.downloadedIndicationVisible || self.favoriteIndicationVisible);
}

- (void)addObserversIfNecessary {
    if (!self.textObserversAdded) {
        self.textObserversAdded = YES;
        [self.textLabel addObserver:self forKeyPath:@"text" options:0 context:NULL];
        [self.detailTextLabel addObserver:self forKeyPath:@"text" options:0 context:NULL];
    }
}

- (void)removeObservers {
    if (self.textObserversAdded) {
        [self.textLabel removeObserver:self forKeyPath:@"text"];
        [self.detailTextLabel removeObserver:self forKeyPath:@"text"];
    }
}

- (void)updateTextLabelHighlighting {
    [self addObserversIfNecessary];
    if (self.textLabelHighlightedRegex) {
        [self.textLabel setHighlightedColor:self.originalTextLabelColor forMatchesOfRegex:self.textLabelHighlightedRegex dimmedColor:self.textLabelDimmedColor ?: kDefaultTextLabelDimmedColor];
    } else {
        self.textLabel.textColor = self.originalTextLabelColor;
    }
}

- (void)updateDetailTextLabelHighlighting {
    [self addObserversIfNecessary];
    if (self.detailTextLabelHighlightedRegex) {
        [self.detailTextLabel setHighlightedColor:self.originalTextLabelColor forMatchesOfRegex:self.detailTextLabelHighlightedRegex dimmedColor:self.detailTextLabelDimmedColor ?: kDefaultDetailTextLabelDimmedColor];
    } else {
        self.detailTextLabel.textColor = self.originalDetailTextLabelColor;
    }
}

+ (CGFloat)minHeightForCurrentPreferredContentSizeCategory {
    static CGFloat height = 0.0;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:nil queue:nil usingBlock:^(NSNotification *notif) {
            height = 0.0;
        }];
    });
    if (height == 0.0) {
        CGFloat coefficient;
#ifdef TARGET_IS_EXTENSION
        coefficient = 1.0;
#else
        NSString* contentSize = [[UIApplication sharedApplication] preferredContentSizeCategory];
        if ([contentSize isEqualToString:UIContentSizeCategoryLarge]) { //Default => common case first
            coefficient = 1.0;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryExtraSmall]) {
            coefficient = 0.78;
        } else if ([contentSize isEqualToString:UIContentSizeCategorySmall]) {
            coefficient = 0.85;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryMedium]) {
            coefficient = 0.92;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryExtraLarge]) {
            coefficient = 1.08;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryExtraExtraLarge]) {
            coefficient = 1.15;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryExtraExtraExtraLarge]) {
            coefficient = 1.22;
        } else {
            coefficient = 1.0; //Default
        }
#endif
        height = floorf(kCellHeightForDefaultPreferredContentSizeCategory * coefficient);
    }
    return height;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self removeObservers];
}

@end
