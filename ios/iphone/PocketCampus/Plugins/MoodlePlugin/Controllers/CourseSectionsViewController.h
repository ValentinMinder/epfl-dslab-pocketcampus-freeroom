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

@interface CourseSectionsViewController : UIViewController<UITableViewDelegate, UIDocumentInteractionControllerDelegate, MoodleServiceDelegate, AuthenticationCallbackDelegate> {
    IBOutlet UIActivityIndicatorView* centerActivityIndicator;
    IBOutlet UILabel* centerMessageLabel;
    IBOutlet UITableView* sectionsList;
    IBOutlet UIWebView* webView;
    MoodleService* moodleService;
    NSArray* iSections;
    int courseId;
    int current;
    NSString* courseTitle;
    AuthenticationController* authController;
    TequilaToken* tequilaKey;
}

@property (nonatomic, retain) UIDocumentInteractionController *docController;

- (id)initWithCourseId:(int)aCourseId andCourseTitle:(NSString*)aCourseTitle;

@end
