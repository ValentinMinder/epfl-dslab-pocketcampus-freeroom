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


@interface PCRefreshControl ()

@property (nonatomic, weak, readwrite) UITableViewController* tableViewController;
@property (nonatomic, weak) UITableView* tableView;
@property (nonatomic, strong) UIRefreshControl* refreshControl;
@property (nonatomic) BOOL usesUIRefreshControl;
@property (nonatomic, strong) MSPullToRefreshController* msPtRController;
@property (nonatomic, weak) id target;
@property (nonatomic) SEL selector;
@property (nonatomic) BOOL engagedRefreshProgrammatically;
@property (nonatomic, strong) NSTimer* showHideTimer;
@property (nonatomic, strong) UIView* containerView;
@property (nonatomic, strong) UILabel* messageLabel;
@property (nonatomic, strong) UIView* signView;
@property (nonatomic, readwrite) BOOL isVisible;
@property (nonatomic, strong) UIColor* problemColor;

@end

@implementation PCRefreshControl

- (id)initWithTableViewController:(UITableViewController*)tableViewController {
    self = [super init];
    if (self) {
        self.tableViewController = tableViewController;
        self.tableView = self.tableViewController.tableView;
        _type = -1;
        self.problemColor = [UIColor colorWithRed:0.827451 green:0.000000 blue:0.000000 alpha:1.0];
        if ([self.tableViewController respondsToSelector:@selector(refreshControl)]) { //>= iOS 6
            self.usesUIRefreshControl = YES;
            self.tableViewController.refreshControl = [[UIRefreshControl alloc] init];
            self.refreshControl = self.tableViewController.refreshControl;
            self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:@""];
        } else { //< iOS 6
            self.usesUIRefreshControl = NO;
            self.msPtRController = [[MSPullToRefreshController alloc] initWithScrollView:self.tableView delegate:self];
            self.messageLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, self.tableView.frame.size.height-25.0, self.tableView.frame.size.width-20.0, 20.0)];
            self.messageLabel.numberOfLines = 0;
            self.messageLabel.font = [UIFont boldSystemFontOfSize:13.0];
            self.messageLabel.shadowOffset = [PCValues shadowOffset1];
            self.messageLabel.shadowColor = [PCValues shadowColor1];
            self.messageLabel.textAlignment = UITextAlignmentCenter;
            self.messageLabel.backgroundColor = [UIColor clearColor];
            self.messageLabel.adjustsFontSizeToFitWidth = YES;
            
            self.containerView = [[UIView alloc] initWithFrame:CGRectMake(0, -self.tableView.frame.size.height, self.tableView.frame.size.width, self.tableView.frame.size.height)];
            [self.containerView addSubview:self.messageLabel];
            
            //self.containerView.backgroundColor = [UIColor yellowColor];
            
            self.type = RefreshControlTypeDefault;
            [self.tableView addSubview:self.containerView];
        }
    }
    return self;
}

/*- (void)drawRect:(CGRect)rect {
    if (self.usesUIRefreshControl) {
        //nothing
    } else {
        [self addSubview:self.messageLabel];
        UIImage* shadowImage = [[UIImage imageNamed:@"TopDown1pxShadow"] resizableImageWithCapInsets:UIEdgeInsetsMake(0, 0, 0, 0)];
        UIImageView* shadowImageView = [[UIImageView alloc] initWithImage:shadowImage];
        shadowImageView.frame = CGRectMake(0, kHeight, self.tableView.frame.size.width, 10.0);
        shadowImageView.alpha = 0.3;
        //[self addSubview:shadowImageView];
        UIView* darkLine = [[UIView alloc] initWithFrame:CGRectMake(0.0, kHeight-1, self.tableView.frame.size.width, 2.0)];
        darkLine.backgroundColor = [UIColor blackColor];
        darkLine.alpha = 0.2;
        [self addSubview:darkLine];
        [self setType:RefreshControlTypeRefreshing];
    }
}*/

- (void)uiRefreshControlValueChanged {
    [self.target performSelectorOnMainThread:self.selector withObject:nil waitUntilDone:YES];
}

- (void)setTarget:(id)target selector:(SEL)selector {
    if (self.usesUIRefreshControl) {
        [self.refreshControl removeTarget:self.target action:self.selector forControlEvents:UIControlEventValueChanged];
        [self.refreshControl addTarget:self action:@selector(uiRefreshControlValueChanged) forControlEvents:UIControlEventValueChanged];
    }
    self.target = target;
    self.selector = selector;
}

- (void)startRefreshingWithMessage:(NSString*)message {
    [self setMessage:message];
    [self setType:RefreshControlTypeRefreshing];
    [self show];
}

- (void)endRefreshing {
    if (self.usesUIRefreshControl) {
        self.message = nil;
        [self.refreshControl endRefreshing];
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
            self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:message attributes:[NSDictionary dictionaryWithObject:self.problemColor forKey:NSForegroundColorAttributeName]];
        } else {
            self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:message];
        }
    } else {
        self.messageLabel.text = message;
    }
}

- (void)setType:(RefreshControlType)type {
    if (self.type == type) {
        return;
    }
    if (type == RefreshControlTypeDefault) {
        if (self.usesUIRefreshControl) {
            //nothing special
        } else {            
            [self.signView removeFromSuperview];
            self.signView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ArrowDownCircle"]];
            self.signView.center = CGPointMake(self.containerView.center.x, self.containerView.frame.size.height-25.0);
            [self.containerView addSubview:self.signView];
            self.messageLabel.hidden = YES;
        }
        self.message = @"";
    } else if (type == RefreshControlTypeRefreshing) {
        if (self.usesUIRefreshControl) {
            self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:self.message];
        } else {
            UIActivityIndicatorView* activityIndicator __block = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            activityIndicator.center = CGPointMake(self.containerView.center.x, self.containerView.frame.size.height-40.0);
            activityIndicator.alpha = 0.0;
            [activityIndicator startAnimating];
            [self.containerView addSubview:activityIndicator];
            self.messageLabel.alpha = 0.0;
            self.messageLabel.hidden = NO;
            self.messageLabel.textColor = [UIColor colorWithRed:0.521569 green:0.521569 blue:0.521569 alpha:1.0];
            
            NSTimeInterval duration = 0.0;
            
            if (self.type == RefreshControlTypeDefault) {
                duration = 0.2;
            }
            
            [UIView animateWithDuration:duration animations:^{
                self.signView.alpha = 0.0;
                activityIndicator.alpha = 1.0;
                self.messageLabel.alpha = 1.0;
            } completion:^(BOOL finished) {
                [self.signView removeFromSuperview];
                self.signView = activityIndicator;
            }];
            
        }
    } else if (type == RefreshControlTypeProblem) {
        if (self.usesUIRefreshControl) {
            self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:self.message attributes:[NSDictionary dictionaryWithObject:self.problemColor forKey:NSForegroundColorAttributeName]];
        } else {
            self.messageLabel.textColor = self.problemColor;
            [self.signView removeFromSuperview];
            self.signView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"WarningSignRed"]];
            self.signView.center = CGPointMake(self.containerView.center.x, self.containerView.frame.size.height-40.0);
            [self.containerView addSubview:self.signView];
            self.messageLabel.hidden = NO;
        }
    } else {
        @throw [NSException exceptionWithName:@"bad argument" reason:@"imageType must be of enum type RefreshControlImageType" userInfo:nil];
    }
    _type = type;
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
        return self.refreshControl.refreshing;
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
        [self.refreshControl beginRefreshing];
        [self.tableView scrollRectToVisible:CGRectMake(0.0, -1.0, 1.0, 1.0) animated:YES];
    } else {
        self.engagedRefreshProgrammatically = YES;
        [self.msPtRController startRefreshingDirection:MSRefreshDirectionTop animated:YES];
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
        [self.refreshControl endRefreshing];
        self.message = nil;
        self.type = RefreshControlTypeDefault;
    } else {
        [self.msPtRController finishRefreshingDirection:MSRefreshDirectionTop animated:YES];
        [UIView animateWithDuration:0.2 animations:^{
            self.messageLabel.transform = CGAffineTransformMakeScale(0.1, 0.1);
            self.signView.transform = CGAffineTransformMakeScale(0.1, 0.1);
        } completion:^(BOOL finished) {
            self.message = nil;
            self.type = RefreshControlTypeDefault;
            self.messageLabel.transform = CGAffineTransformIdentity;
            self.signView.transform = CGAffineTransformIdentity;
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

#pragma mark - MSPullToRefreshDelegate

- (BOOL) pullToRefreshController:(MSPullToRefreshController *) controller canRefreshInDirection:(MSRefreshDirection)direction {
    return direction == MSRefreshDirectionTop;
}

- (CGFloat) pullToRefreshController:(MSPullToRefreshController *) controller refreshableInsetForDirection:(MSRefreshDirection) direction {
    return 70.0;
}

- (CGFloat) pullToRefreshController:(MSPullToRefreshController *)controller refreshingInsetForDirection:(MSRefreshDirection)direction {
    return 60.0;
}

- (void) pullToRefreshController:(MSPullToRefreshController *)controller canEngageRefreshDirection:(MSRefreshDirection)direction {
    if (self.type == RefreshControlTypeDefault) {
        [UIView animateWithDuration:0.2 animations:^{
            self.signView.transform = CGAffineTransformMakeRotation(M_PI);
        }];
    }
}

- (void) pullToRefreshController:(MSPullToRefreshController *) controller didDisengageRefreshDirection:(MSRefreshDirection) direction {
    if (self.type == RefreshControlTypeDefault) {
        [UIView animateWithDuration:0.2 animations:^{
            self.signView.transform = CGAffineTransformIdentity;
        }];
    }
}

- (void) pullToRefreshController:(MSPullToRefreshController *) controller didEngageRefreshDirection:(MSRefreshDirection) direction {
    self.type = RefreshControlTypeRefreshing;
    self.isVisible = YES;
    if (self.engagedRefreshProgrammatically) {
        self.engagedRefreshProgrammatically = NO; //library user is excepted to call it's refresh logic himself if he has started refresh animation programmatically
    } else {
        [self.target performSelectorOnMainThread:self.selector withObject:nil waitUntilDone:YES];
    }
}

@end
