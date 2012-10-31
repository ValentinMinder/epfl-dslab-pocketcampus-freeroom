//
//  PCRefreshControl.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCRefreshControl.h"

#import "PCValues.h"

#import "PCUtils.h"

static CGFloat kHeight = 50.0;
static CGFloat kShowHideAnimationDuration = 0.3;


@interface PCRefreshControl ()

@property (nonatomic, weak, readwrite) UITableViewController* tableViewController;
@property (nonatomic) BOOL usesUIRefreshControl;
@property (nonatomic, weak) UIBarButtonItem* compatibilityBarButtonItem;
@property (nonatomic, weak) id target;
@property (nonatomic) SEL selector;
@property (nonatomic, strong) NSTimer* showHideTimer;
@property (nonatomic, strong) UILabel* messageLabel;
@property (nonatomic, strong) UIView* signView;
@property (nonatomic, readwrite) BOOL isVisible;
@property (nonatomic, strong) UIColor* problemColor;

@end

@implementation PCRefreshControl

- (id)initWithTableViewController:(UITableViewController*)tableViewController compatibilityRefreshBarButtonItem:(UIBarButtonItem*)barButtonItem {
    self = [super init];
    if (self) {
        self.tableViewController = tableViewController;
        self.compatibilityBarButtonItem = barButtonItem;
        _type = 0;
        self.problemColor = [UIColor colorWithRed:0.827451 green:0.000000 blue:0.000000 alpha:1.0];
        if ([self.tableViewController respondsToSelector:@selector(refreshControl)]) { //>= iOS 6
            self.usesUIRefreshControl = YES;
            self.compatibilityBarButtonItem.customView = [[UIView alloc] initWithFrame:CGRectNull];
            self.compatibilityBarButtonItem.enabled = NO;
            self.tableViewController.refreshControl = [[UIRefreshControl alloc] init];
        } else { //<= iOS 6
            self.usesUIRefreshControl = NO;
            self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
            self.backgroundColor = [PCValues backgroundColor1];
            self.messageLabel = [[UILabel alloc] initWithFrame:CGRectMake(48.0, 9.0, self.tableViewController.tableView.frame.size.width-55, kHeight-20.0)];
            self.messageLabel.numberOfLines = 0;
            self.messageLabel.font = [UIFont boldSystemFontOfSize:14.0];
            self.messageLabel.shadowOffset = [PCValues shadowOffset1];
            self.messageLabel.shadowColor = [PCValues shadowColor1];
            self.messageLabel.textAlignment = UITextAlignmentLeft;
            self.messageLabel.backgroundColor = [UIColor clearColor];
            self.messageLabel.adjustsFontSizeToFitWidth = YES;
        }
    }
    return self;
}

- (void)drawRect:(CGRect)rect {
    if (self.usesUIRefreshControl) {
        //nothing
    } else {
        [self addSubview:self.messageLabel];
        UIImage* shadowImage = [[UIImage imageNamed:@"TopDown1pxShadow"] resizableImageWithCapInsets:UIEdgeInsetsMake(0, 0, 0, 0)];
        UIImageView* shadowImageView = [[UIImageView alloc] initWithImage:shadowImage];
        shadowImageView.frame = CGRectMake(0, kHeight, self.tableViewController.tableView.frame.size.width, 10.0);
        shadowImageView.alpha = 0.3;
        //[self addSubview:shadowImageView];
        UIView* darkLine = [[UIView alloc] initWithFrame:CGRectMake(0.0, kHeight-1, self.tableViewController.tableView.frame.size.width, 2.0)];
        darkLine.backgroundColor = [UIColor blackColor];
        darkLine.alpha = 0.2;
        [self addSubview:darkLine];
        [self setType:RefreshControlTypeRefreshing];
    }
}

- (void)uiRefreshControlValueChanged {
    [self.target performSelectorOnMainThread:self.selector withObject:nil waitUntilDone:YES];
}

- (void)setTarget:(id)target selector:(SEL)selector {
    if (self.usesUIRefreshControl) {
        [self.tableViewController.refreshControl removeTarget:self.target action:self.selector forControlEvents:UIControlEventValueChanged];
        [self.tableViewController.refreshControl addTarget:self action:@selector(uiRefreshControlValueChanged) forControlEvents:UIControlEventValueChanged];
    }
    self.target = target;
    self.selector = selector;
    self.compatibilityBarButtonItem.target = target;
    self.compatibilityBarButtonItem.action = selector;
}

- (void)startRefreshingWithMessage:(NSString*)message {
    [self setMessage:message];
    [self setType:RefreshControlTypeRefreshing];
    if (self.usesUIRefreshControl) {
        [self.tableViewController.refreshControl beginRefreshing];
        [self.tableViewController.tableView scrollRectToVisible:CGRectMake(0, -1, 1, 1) animated:YES];
    } else {
        [self show];
    }
}

- (void)endRefreshing {
    if (self.usesUIRefreshControl) {
        self.message = nil;
        [self.tableViewController.refreshControl endRefreshing];
    } else {
        [self hide];
    }
}

- (void)setMessage:(NSString *)message {
    if (!message) {
        message = @"";
    }
    _message = [message copy];
    if (self.usesUIRefreshControl) {
        if (self.type == RefreshControlTypeProblem) {
            self.tableViewController.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:message attributes:[NSDictionary dictionaryWithObject:self.problemColor forKey:NSForegroundColorAttributeName]];
        } else {
            self.tableViewController.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:message];
        }
    } else {
        self.messageLabel.text = message;
    }
}

- (void)setType:(RefreshControlType)type {
    if (self.type == type) {
        return;
    }
    _type = type;
    if (type == RefreshControlTypeNone) {
        if (self.usesUIRefreshControl) {
            self.message = @"";
        } else {
            self.messageLabel.textColor = [UIColor colorWithRed:0.419608 green:0.419608 blue:0.419608 alpha:1.0];
            [self.signView removeFromSuperview];
            self.signView = nil;
        }
    } else if (type == RefreshControlTypeRefreshing) {
        if (self.usesUIRefreshControl) {
            self.tableViewController.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:self.message];
        } else {
            self.messageLabel.textColor = [UIColor colorWithRed:0.419608 green:0.419608 blue:0.419608 alpha:1.0];
            [self.signView removeFromSuperview];
            UIActivityIndicatorView* activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            self.signView = activityIndicator;
            self.signView.center = CGPointMake(25.0, kHeight/2.0);
            [self addSubview: self.signView];
            [activityIndicator startAnimating];
        }
    } else if (type == RefreshControlTypeProblem) {
        if (self.usesUIRefreshControl) {
            self.tableViewController.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:self.message attributes:[NSDictionary dictionaryWithObject:self.problemColor forKey:NSForegroundColorAttributeName]];
        } else {
            self.messageLabel.textColor = self.problemColor;
            [self.signView removeFromSuperview];
            self.signView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"WarningSignGray"]];
            [self addSubview:self.signView];
        }
    } else {
        @throw [NSException exceptionWithName:@"bad argument" reason:@"imageType must be of enum type RefreshControlImageType" userInfo:nil];
    }
}

/*- (UIActivityIndicatorView*)findActivityIndicatorSubview:(UIView*)view {
    for (UIView* subview in view.subviews) {
        if ([subview isKindOfClass:[UIActivityIndicatorView class]]) {
            return (UIActivityIndicatorView*)subview;
        }
        return [self findActivityIndicatorSubview:subview];

    }
    return nil;
}*/

- (BOOL)isVisible {
    if (self.usesUIRefreshControl) {
        return self.tableViewController.refreshControl.refreshing;
    } else {
        return _isVisible;
    }
}

- (void)show {
    if (self.isVisible) {
        return;
    }
    [self.showHideTimer invalidate];
    [self _show];
}

- (void)_show {
    if (self.isVisible) {
        return;
    }
    self.isVisible = YES;
    if (self.usesUIRefreshControl) {
        [self.tableViewController.refreshControl beginRefreshing];
    } else {
        CGRect prevFrame = self.tableViewController.tableView.frame;
        [[self.tableViewController.tableView superview] addSubview:self];
        self.frame = CGRectMake(0, -kHeight, prevFrame.size.width, kHeight);
        [UIView animateWithDuration:kShowHideAnimationDuration delay:0 options:UIViewAnimationCurveEaseInOut|UIViewAnimationOptionBeginFromCurrentState animations:^{
            self.frame = CGRectMake(0, 0, prevFrame.size.width, kHeight);
            self.tableViewController.tableView.frame = CGRectMake(0, kHeight, prevFrame.size.width, prevFrame.size.height-kHeight);
        } completion:^(BOOL finished) {
            //nothing
        }];
    }
}

- (void)showForTimeInterval:(NSTimeInterval)timeInterval {
    if (self.isVisible) {
        return;
    }
    [self show];
    [self.showHideTimer invalidate];
    self.showHideTimer = [NSTimer scheduledTimerWithTimeInterval:timeInterval target:self selector:@selector(_hide) userInfo:nil repeats:NO];
}

- (void)hide {
    if (!self.isVisible) {
        return;
    }
    [self.showHideTimer invalidate];
    [self _hide];
}

- (void)_hide {
    if (!self.isVisible) {
        return;
    }
    self.isVisible = NO;
    if (self.usesUIRefreshControl) {
        [self.tableViewController.refreshControl endRefreshing];
        self.message = nil;
    } else {
        CGRect prevFrame = self.tableViewController.tableView.frame;
        [[self.tableViewController.tableView superview] addSubview:self];
        [UIView animateWithDuration:kShowHideAnimationDuration delay:0 options:UIViewAnimationCurveEaseInOut|UIViewAnimationOptionBeginFromCurrentState animations:^{
            self.frame = CGRectMake(0, -kHeight, prevFrame.size.width, kHeight);
            self.tableViewController.tableView.frame = CGRectMake(0, 0, prevFrame.size.width, prevFrame.size.height+kHeight);
        } completion:^(BOOL finished) {
            [self setType:RefreshControlTypeNone];
            [self removeFromSuperview];
        }];
    }
}

- (void)hideInTimeInterval:(NSTimeInterval)timeInterval {
    if (!self.isVisible) {
        return;
    }
    [self.showHideTimer invalidate];
    self.showHideTimer = [NSTimer scheduledTimerWithTimeInterval:timeInterval target:self selector:@selector(_hide) userInfo:nil repeats:NO];
}

@end
