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

@interface CoursesListViewController : UIViewController<UITableViewDelegate, MoodleServiceDelegate, AuthenticationCallbackDelegate> {
    IBOutlet UIActivityIndicatorView* centerActivityIndicator;
    IBOutlet UILabel* centerMessageLabel;
    IBOutlet UITableView* coursesList;
    MoodleService* moodleService;
    bool pingedAuthPlugin;
    NSArray* iCourses;
}

@end
