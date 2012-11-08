//
//  MyEduCourseInfoViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "MyEduCourseInfoViewController.h"

#import "PCValues.h"

@interface MyEduCourseInfoViewController ()

@end

@implementation MyEduCourseInfoViewController

- (id)initWithCourse:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduCourseInfoView" bundle:nil];
    if (self) {
        _course = course;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    //self.view.backgroundColor = [PCValues backgroundColor1];
    
    /*self.titleLabel.textColor = [PCValues textColor1];
    self.titleLabel.shadowColor = [PCValues shadowColor1];
    self.titleLabel.shadowOffset = [PCValues shadowOffset1];
    
    self.descriptionTextView.textColor = [UIColor colorWithRed:0.478431 green:0.478431 blue:0.478431 alpha:1.0];
    
    self.bottomLabel.textColor = [UIColor colorWithRed:0.000000 green:0.231373 blue:0.733333 alpha:1.0];
    self.bottomLabel.shadowColor = [UIColor whiteColor];
    self.bottomLabel.shadowOffset = [PCValues shadowOffset1];*/
    self.navBar.tintColor = [PCValues pocketCampusRed];
    self.containerView.layer.masksToBounds = NO;
    self.containerView.layer.shadowOffset = CGSizeMake(0.0, 2.00);
    self.containerView.layer.shadowRadius = 3.0;
    self.containerView.layer.shadowOpacity = 0.5;
    self.containerView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.4].CGColor;
    self.containerView.layer.borderWidth = 1.0f;
    
    /*UIImage* shadowImage = [UIImage imageNamed:@"StretchableInnerShadow"];
    shadowImage = [shadowImage stretchableImageWithLeftCapWidth:floorf(shadowImage.size.width/2.0) topCapHeight:floorf(shadowImage.size.height/2.0)];
    
    UIImageView *shadowImageView = [[UIImageView alloc] initWithImage:shadowImage];
    shadowImageView.contentMode = UIViewContentModeScaleToFill;
    shadowImageView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    shadowImageView.frame = self.view.bounds;
    [self.view insertSubview:shadowImageView belowSubview:self.containerView];*/
    
    self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"LightGrayTextureBackground"]];
    
    self.course = _course; //force refresh
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setCourse:(MyEduCourse *)course {
    _course = course;
    self.titleLabel.text = self.course.iTitle;
    self.descriptionTextView.text = self.course.iDescription;
    
    CGRect newFrameDescription = self.descriptionTextView.frame;
    CGFloat contentHeight = self.descriptionTextView.contentSize.height+20;
    if (contentHeight > 400.0) {
        contentHeight = 400.0;
    }
    if (contentHeight > self.descriptionTextView.frame.size.height) {
        self.descriptionTextView.showsVerticalScrollIndicator = YES;
    }
    newFrameDescription.size.height = contentHeight+20.0;
    
    self.descriptionTextView.frame = newFrameDescription;
    //self.descriptionTextView.backgroundColor = [UIColor yellowColor];
    
    CGRect newFrameContainer = self.containerView.frame;
    CGFloat newHeight = [PCValues totalSubviewsHeight:self.containerView]+70.0;
    newFrameContainer.size.height = newHeight;
    self.containerView.frame = newFrameContainer;
    self.containerView.center = CGPointMake(self.view.center.x, self.view.center.y+40.0);
    
    
    
    NSDate* lastUpdateDate = [NSDate dateWithTimeIntervalSince1970:self.course.iLastUpdateTimestamp/1000.0];
    NSString* lastUpdateDateString = [NSDateFormatter localizedStringFromDate:lastUpdateDate dateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterShortStyle];
    NSString* lastUpdateOn = [[NSBundle mainBundle] localizedStringForKey:@"LastUpdateOn" value:@"" table:@"MyEduPlugin"];
    self.bottomLabel1.text = [NSString stringWithFormat:@"%@ %@", lastUpdateOn, lastUpdateDateString];
    
    NSDate* creationDate = [NSDate dateWithTimeIntervalSince1970:self.course.iCreationTimestamp/1000.0];
    NSString* creationDateString = [NSDateFormatter localizedStringFromDate:creationDate dateStyle:NSDateFormatterLongStyle timeStyle:NSDateFormatterNoStyle];
    NSString* createdOn = [[NSBundle mainBundle] localizedStringForKey:@"CreatedOn" value:@"" table:@"MyEduPlugin"];
    self.bottomLabel2.text = [NSString stringWithFormat:@"%@ %@", createdOn, creationDateString];
    
}

@end
