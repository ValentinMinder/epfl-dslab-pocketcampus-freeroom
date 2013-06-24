//
//  myQuestionViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/21/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyQuestionViewController.h"

@interface MyQuestionViewController ()

@end

@implementation MyQuestionViewController

@synthesize data, tableContents, sortedKeys, content, tableview;

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
    content =[deserializedData objectForKey:@"content"];
    NSArray *arrTemp2 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"topicid"],nil];
    NSArray *arrTemp3 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"tags"],nil];
    NSArray *arrTemp4 = [[NSArray alloc]
                         initWithObjects:[deserializedData objectForKey:@"time"],nil];
    //NSArray *arrTemp5 = [[NSArray alloc]
    //                     initWithObjects:[deserializedData objectForKey:@"answerlist"],nil];
    NSMutableArray* arrTemp5 = [[NSMutableArray alloc] init];
    NSArray* answerlist = [deserializedData objectForKey:@"answerlist"];
    for (NSDictionary* answer in answerlist) {
        /*NSString* answerString = [answer objectForKey:@"content"];
        answerString = [answerString stringByAppendingFormat:@"\r\n%@\r\n%@",[answer objectForKey:@"name"],[answer objectForKey:@"time"]];
        */
        [arrTemp5 addObject:[answer objectForKey:@"content"]];
        
    }
    NSDictionary *temp =[[NSDictionary alloc]
                         initWithObjectsAndKeys:arrTemp1,@"content",arrTemp2,
                         @"topicid",arrTemp3,@"tags",arrTemp5,@"answer",arrTemp4,@"time",nil];
    self.tableContents =temp;
    [temp release];
    self.sortedKeys =[[self.tableContents allKeys] sortedArrayUsingComparator:^(NSString* s1, NSString* s2) {
        if ([s1 isEqualToString:@"content"]) {
            return (NSComparisonResult)NSOrderedAscending;
        } else if ([s1 isEqualToString:@"topicid"]) {
            if ([s2 isEqualToString:@"content"]) {
                return (NSComparisonResult)NSOrderedDescending;
            } else {
                return (NSComparisonResult)NSOrderedAscending;
            }
        } else if ([s1 isEqualToString:@"tags"]) {
            if ([s2 isEqualToString:@"content"]&&[s2 isEqualToString:@"topicid"]) {
                return (NSComparisonResult)NSOrderedDescending;
            } else {
                return (NSComparisonResult)NSOrderedAscending;
            }
        } else if ([s1 isEqualToString:@"time"]) {
            if ([s2 isEqualToString:@"answer"]) {
                return (NSComparisonResult)NSOrderedAscending;
            } else {
                return (NSComparisonResult)NSOrderedDescending;
            }
        } else {
            return (NSComparisonResult)NSOrderedDescending;
        }
    }];
    [arrTemp1 release];
    [arrTemp2 release];
    [arrTemp3 release];
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

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    /*
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    NSUInteger row = [indexPath row];
    NSString *rowValue = [listData objectAtIndex:row];
    
    NSString *message = [[NSString alloc] initWithString:row];
    UIAlertView *alert = [[UIAlertView alloc]
                          initWithTitle:@"You selected"
                          message:message delegate:nil
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil];
    [alert show];
    [alert release];
    [message release];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
     */
}
- (void)serviceConnectionToServerTimedOut {
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
