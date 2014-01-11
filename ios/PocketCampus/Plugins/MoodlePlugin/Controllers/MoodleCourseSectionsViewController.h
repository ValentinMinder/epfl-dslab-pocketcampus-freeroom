

//  Created by Loïc Gardiol on 04.12.12.


#import <UIKit/UIKit.h>

#import "MoodleService.h"

@interface MoodleCourseSectionsViewController : UITableViewController<MoodleServiceDelegate>

- (id)initWithCourse:(MoodleCourse*)course;

@end
