//
//  PCTableViewSectionHeader.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCTableViewSectionHeader.h"

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
    if (self) {
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.userInteractionEnabled = NO; //otherwise intercepts scrolling touches in tableview
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

+ (CGFloat)preferredHeight {
    static CGFloat height = 0.0;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:[UIApplication sharedApplication] queue:nil usingBlock:^(NSNotification *note) {
            height = 0.0;
        }];
    });
    if (height == 0.0) {
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, FLT_MAX, FLT_MAX)];
        label.text = @"test";
        label.font = [self fontForTitleLabel];
        [label sizeToFit];
        height = (CGFloat)((int)(label.frame.size.height * 1.4f));
    }
    return height;
}

@end
