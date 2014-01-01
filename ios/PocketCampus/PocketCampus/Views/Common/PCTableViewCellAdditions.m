//
//  PCTableViewCellWithDownloadIndication.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCTableViewCellAdditions.h"

@interface PCTableViewCellAdditions ()

@property (nonatomic, strong) UIImageView* icon;
@property (nonatomic, strong) UIColor* originalBackgroundColor;
@property (nonatomic, strong) UIColor* originalTextLabelColor;
@property (nonatomic, strong) UIColor* originalDetailTextLabelColor;

@end

static __strong UIColor* kDefaultTextLabelDimmedColor ;
static __strong UIColor* kDefaultDetailTextLabelDimmedColor;

@implementation PCTableViewCellAdditions

#pragma mark - Init

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

#pragma mark - Properties

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

#pragma mark - Others

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

- (void)updateTextLabelHighlighting {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self.textLabel addObserver:self forKeyPath:@"text" options:0 context:NULL];
    });
    if (self.textLabelHighlightedRegex) {
        [self.textLabel setHighlightedColor:self.originalTextLabelColor forMatchesOfRegex:self.textLabelHighlightedRegex dimmedColor:self.textLabelDimmedColor ?: kDefaultTextLabelDimmedColor];
    } else {
        self.textLabel.textColor = self.originalTextLabelColor;
    }
}

- (void)updateDetailTextLabelHighlighting {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self.detailTextLabel addObserver:self forKeyPath:@"text" options:0 context:NULL];
    });
    if (self.detailTextLabelHighlightedRegex) {
        [self.detailTextLabel setHighlightedColor:self.originalTextLabelColor forMatchesOfRegex:self.detailTextLabelHighlightedRegex dimmedColor:self.detailTextLabelDimmedColor ?: kDefaultDetailTextLabelDimmedColor];
    } else {
        self.detailTextLabel.textColor = self.originalDetailTextLabelColor;
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    @try {
        [self.textLabel removeObserver:self forKeyPath:@"text"];
        [self.detailTextLabel removeObserver:self forKeyPath:@"text"];
    }
    @catch (NSException *exception) {}
}

@end
