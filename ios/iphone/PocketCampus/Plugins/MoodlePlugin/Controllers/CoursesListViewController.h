//
//  CoursesListViewController.h
//  PocketCampus
//
//  Created by Amer C on 5/2/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MoodleService.h"

#import "AuthenticationService.h"
#import "AuthenticationController.h"

@interface CoursesListViewController : UIViewController<UITableViewDelegate, MoodleServiceDelegate, AuthenticationCallbackDelegate> {
    UIActivityIndicatorView* centerActivityIndicator;
    UILabel* centerMessageLabel;
    UITableView* coursesList;
    MoodleService* moodleService;
    NSArray* iCourses;
    AuthenticationController* authController;
    TequilaToken* tequilaKey;
}

@property (nonatomic, assign) IBOutlet UITableView* coursesList;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, assign) IBOutlet UILabel* centerMessageLabel;


@end
