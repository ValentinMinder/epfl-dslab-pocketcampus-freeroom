//
//  CourseSectionsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MoodleService.h"

@interface CourseSectionsViewController : UITableViewController<MoodleServiceDelegate>

- (id)initWithCourseId:(int)courseId andCourseTitle:(NSString*)courseTitle;

@end
