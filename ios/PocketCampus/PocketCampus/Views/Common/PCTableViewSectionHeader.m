//
//  PCTableViewSectionHeader.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCTableViewSectionHeader.h"

static CGFloat cachedPreferredHeight = 0.0;

static BOOL addedObserver = NO;

@interface PCTableViewSectionHeader ()

@property (nonatomic, readwrite) UITableView* tableView;

@end

@implementation PCTableViewSectionHeader

- (id)initWithSectionTitle:(NSString*)sectionTitle tableView:(UITableView*)tableView
{
    [PCUtils throwExceptionIfObject:sectionTitle notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:tableView notKindOfClass:[UITableView class]];
    CGFloat height = [self.class preferredHeight];
    self = [super initWithFrame:CGRectMake(0, 0, tableView.frame.size.width, height)];
    self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    if (self) {
        self.tableView = tableView;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        
        UINavigationBar* navBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, -10, self.frame.size.width, self.frame.size.height+20)];
        navBar.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        navBar.translucent = YES;
        
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(self.tableView.separatorInset.left, 0, self.frame.size.width, self.frame.size.height)];
        label.text = sectionTitle;
        label.backgroundColor = [UIColor clearColor];
        label.font = [self.class fontForTitleLabel];
        label.textColor = [UIColor colorWithWhite:0.0 alpha:0.8];
        label.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        
        [self addSubview:navBar];
        [self addSubview:label];
    }
    return self;
}

+ (UIFont*)fontForTitleLabel {
    return [UIFont boldSystemFontOfSize:13.0];
}

+ (CGFloat)preferredHeight
{
    if (cachedPreferredHeight > 0.0) {
        return cachedPreferredHeight;
    }
    UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, FLT_MAX, FLT_MAX)];
    label.text = @"test";
    label.font = [self fontForTitleLabel];
    [label sizeToFit];
    cachedPreferredHeight = (CGFloat)((int)(label.frame.size.height * 1.5));
    if (!addedObserver) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(preferredContentSizeChanged:) name:UIContentSizeCategoryDidChangeNotification object:nil];
        addedObserver = YES;
    }
    return cachedPreferredHeight;
}

+ (void)preferredContentSizeChanged:(NSNotification *)notification
{
    cachedPreferredHeight = 0.0;
}

@end
