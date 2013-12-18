//
//  myAnswerViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/23/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyAnswerViewController.h"

@interface MyAnswerViewController ()

@end

@implementation MyAnswerViewController

@synthesize data, tableContents, sortedKeys, tableview;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    NSArray *arrTemp1 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"content"],nil];
    NSArray *arrTemp2 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"topicid"],nil];
    NSArray *arrTemp3 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"tags"],nil];
    NSArray *arrTemp4 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"time"],nil];
    NSArray *arrTemp5 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"asker"],nil];
    NSArray *arrTemp6 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"answer"],nil];
    NSArray *arrTemp7 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"answertime"],nil];
    NSArray *arrTemp8 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"feedback"],nil];
    NSArray *arrTemp9 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"rate"],nil];
    NSArray *arrTemp10 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"feedbacktime"],nil];

    
    
    
    NSDictionary *temp =[[NSDictionary alloc]
                         initWithObjectsAndKeys:arrTemp1,@"Question",arrTemp2,
                         @"Topicid",arrTemp3,@"Tags",arrTemp4,@"Time",arrTemp5,@"Asker",arrTemp6,@"Answer",arrTemp7,
                         @"Answer time",arrTemp8,@"Feedback",arrTemp9,@"Rate",arrTemp10,@"Feedback time",nil];
    self.tableContents =temp;
    [temp release];
    self.sortedKeys =[[self.tableContents allKeys] sortedArrayUsingComparator:^(NSString* s1, NSString* s2) {
        int s1_int = [self StringToInt:s1];
        int s2_int = [self StringToInt:s2];
        if (s1_int<s2_int) {
            return (NSComparisonResult)NSOrderedAscending;
        } else {
            return (NSComparisonResult)NSOrderedDescending;
        }
    }];
    [arrTemp1 release];
    [arrTemp2 release];
    [arrTemp3 release];
    [arrTemp4 release];
    [arrTemp5 release];
    [arrTemp6 release];
    [arrTemp7 release];
    [arrTemp8 release];
    [arrTemp9 release];
    [arrTemp10 release];
}

- (int)StringToInt:(NSString* )ss {
    if ([ss isEqualToString:@"Question"]) {
        return 1;
    } else if ([ss isEqualToString:@"Topicid"]) {
        return 2;
    } else if ([ss isEqualToString:@"Tags"]) {
        return 3;
    } else if ([ss isEqualToString:@"Time"]) {
        return 4;
    } else if ([ss isEqualToString:@"Asker"]) {
        return 5;
    } else if ([ss isEqualToString:@"Answer"]) {
        return 6;
    } else if ([ss isEqualToString:@"Answer time"]) {
        return 7;
    } else if ([ss isEqualToString:@"Feedback"]) {
        return 8;
    } else if ([ss isEqualToString:@"Rate"]) {
        return 9;
    } else if ([ss isEqualToString:@"Feedback time"]) {
        return 10;
    } else {
        return 0;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return [self.sortedKeys count];
}

- (NSString *)tableView:(UITableView *)tableView
titleForHeaderInSection:(NSInteger)section
{
    return [self.sortedKeys objectAtIndex:section];
}

- (NSInteger)tableView:(UITableView *)table
 numberOfRowsInSection:(NSInteger)section {
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:section]];
    return [listData count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *SimpleTableIdentifier = @"SimpleTableIdentifier";
    
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    
    UITableViewCell * cell = [tableView
                              dequeueReusableCellWithIdentifier: SimpleTableIdentifier];
    
    if(cell == nil) {
        
        cell = [[[UITableViewCell alloc]
                 initWithStyle:UITableViewCellStyleDefault
                 reuseIdentifier:SimpleTableIdentifier] autorelease];
        cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.font = [UIFont fontWithName:@"Helvetica" size:17.0];
    }
    
    NSUInteger row = [indexPath row];
    cell.textLabel.text = [listData objectAtIndex:row];
    
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    
    NSString *cellText = [listData objectAtIndex:[indexPath row]];
    UIFont *cellFont = [UIFont fontWithName:@"Helvetica" size:17.0];
    CGSize constraintSize = CGSizeMake(280.0f, MAXFLOAT);
    CGSize labelSize = [cellText sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
    
    return labelSize.height + 20;
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil)];
    tableview.hidden = YES;
}
- (void)error {
    [PCUtils showServerErrorAlert];
}

- (void)dealloc {
    [tableview release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setTableview:nil];
    [super viewDidUnload];
}
@end
