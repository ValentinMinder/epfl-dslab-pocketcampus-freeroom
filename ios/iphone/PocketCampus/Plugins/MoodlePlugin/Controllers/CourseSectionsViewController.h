//
//  CourseSectionsViewController.h
//  PocketCampus
//
//  Created by Amer C on 5/3/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MoodleService.h"

#import "AuthenticationService.h"

#import "AuthenticationController.h"

@interface CourseSectionsViewController : UIViewController<UITableViewDelegate, MoodleServiceDelegate, AuthenticationCallbackDelegate> {
    UIActivityIndicatorView* centerActivityIndicator;
    UILabel* centerMessageLabel;
    UITableView* sectionsList;
    MoodleService* moodleService;
    NSArray* iSections;
    int courseId;
    int currentWeek;
    NSString* courseTitle;
    AuthenticationController* authController;
    TequilaToken* tequilaKey;
}

@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, assign) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, assign) IBOutlet UITableView* sectionsList;

- (id)initWithCourseId:(int)aCourseId andCourseTitle:(NSString*)aCourseTitle;

@end
