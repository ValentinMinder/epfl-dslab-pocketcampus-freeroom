//
//  HomeIcon.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "HomeIcon.h"

#import "PCUtils.h"

@implementation HomeIcon

static int LEFTMOST_MARGIN = 3;
static int LEFT_MARGIN = 0;
static int TOPMOST_MARGIN = 25;
static int TOP_MARGIN = 30;
static int TOP_MARGIN_4_INCH = 30; //same as normal margin, but could be changed in the future
static int ICON_FRAME_WIDTH = 105;
static int ICON_FRAME_HEIGHT = 80;

//images should be of size 65 x 65 pixels (130 x 130 for @2x versions)

- (id)initWithController:(HomeViewController*)controller_ index:(NSUInteger)index_ title:(NSString*)title normalStateImageName:(NSString*)normalImage_ andHighlightedStateImageName:(NSString*)highlightedImage_
{
    self = [super initWithFrame:[[self class] frameForIndex:index_]];
    if (self) {
        controller = controller_;
        index = index_;
        normalImage = [[UIImage imageNamed:normalImage_] retain];
        highlightedImage = [[UIImage imageNamed:highlightedImage_] retain];
        
        imageView = [[UIImageView alloc] initWithImage:normalImage];
        imageView.center = CGPointMake(ICON_FRAME_WIDTH / 2, normalImage.size.height / 2);
        imageView.alpha = 0.8;
        [self addTarget:self action:@selector(pressed) forControlEvents:UIControlEventTouchUpInside];
        [self addTarget:self action:@selector(touchDown) forControlEvents:UIControlEventTouchDown];
        [self addTarget:self action:@selector(touchCancel) forControlEvents:UIControlEventTouchCancel];
        [self addTarget:self action:@selector(touchCancel) forControlEvents:UIControlEventTouchUpOutside];
        [self addSubview:imageView];
        
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, normalImage.size.height+5, self.bounds.size.width, 20)];
        label.text = title;
        label.font = [UIFont boldSystemFontOfSize:16.0];
        label.textColor = [UIColor colorWithWhite:0.0 alpha:0.8];
        label.textAlignment = UITextAlignmentCenter;
        label.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.0];
        label.shadowColor = [UIColor whiteColor];
        label.shadowOffset = CGSizeMake(0.0, 1.0);
        label.backgroundColor = [UIColor clearColor];
        [self addSubview:label];
        [label release];
        [self setIsAccessibilityElement:YES];
        self.accessibilityLabel = title;
        self.tag = index_;
        
    }
    return self;
}


+ (CGRect)frameForIndex:(NSUInteger)index {
    int row;
    
    if (index <= 2) { //index is unsigned => cannot be negative
        row = 0;
    } else if (index >= 3 && index <= 5) {
        row = 1;
    } else if (index >= 6 && index <= 8) {
        row = 2;
    } else {
        @throw [NSException exceptionWithName:@"Outbounded icon index" reason:@"HomeIcon index must be between 0 and 8" userInfo:nil];
    }
    
    int col = index % 3;
    
    //col = 2;
    //row = 1;
    
    int x = LEFTMOST_MARGIN + col * (ICON_FRAME_WIDTH + LEFT_MARGIN);
    int y; 
    if ([PCUtils is4inchDevice]) {
        y = TOPMOST_MARGIN + row * (ICON_FRAME_HEIGHT + TOP_MARGIN_4_INCH);
    } else {
        y = TOPMOST_MARGIN + row * (ICON_FRAME_HEIGHT + TOP_MARGIN);
    }
    //NSLog(@"Frame : x : %d, y : %d, row : %d, col : %d, index : %d", x, y, row, col, index);
    return CGRectMake(x, y, ICON_FRAME_WIDTH, ICON_FRAME_HEIGHT);
}

- (void)touchDown {
    //imageView.image = highlightedImage;
    imageView.alpha = 1.0;
}

- (void)touchCancel {
    //imageView.image = normalImage;
    imageView.alpha = 0.8;
}

- (void)pressed {
    [controller iconPressedWithIndex:index];
    [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(touchCancel) userInfo:nil repeats:NO];
}

- (void)dealloc
{
    [imageView release];
    [normalImage release];
    [highlightedImage release];
    [super dealloc];
}

@end
