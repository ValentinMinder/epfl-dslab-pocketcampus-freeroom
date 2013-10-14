//
//  feedbackListViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "feedbackListViewController.h"

#import "QAFListCell.h"


@interface FeedbackListViewController ()

@end

@implementation FeedbackListViewController
@synthesize data, tableContents, sortedKeys;

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
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    NSMutableArray* answers = [[NSMutableArray alloc] init];
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    NSMutableArray* temp = [deserializedData objectForKey:@"feedbacklist"];
    
    for (NSDictionary* eachAnswer in temp) {
        [answers addObject:[[QAFListCell alloc] initWithContent:[eachAnswer objectForKey:@"feedback"] Data:[eachAnswer JSONString]]];
    }
    
    self.tableContents = [[NSDictionary alloc] initWithObjectsAndKeys:answers,@"Feedbacks", nil];
    self.sortedKeys =[self.tableContents allKeys];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    [tableContents release];
    [sortedKeys release];
}


#pragma mark Table Methods

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
        
        /*cell = [[[UITableViewCell alloc]
         initWithStyle:UITableViewCellStyleSubtitle
         reuseIdentifier:SimpleTableIdentifier] autorelease];
         */
    }
    
    NSUInteger row = [indexPath row];
    TableCell* cellTemp = [listData objectAtIndex:row];
    cell.textLabel.text = cellTemp.content;
    return cell;
}

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    NSUInteger row = [indexPath row];
    QAFListCell *rowValue = [listData objectAtIndex:row];
    
    FeedbackViewController* viewController = [FeedbackViewController alloc];
    viewController.data = rowValue.data;
    [self.navigationController pushViewController:viewController animated:YES];
    
    
    /*
     NSString *message = [[NSString alloc] initWithString:rowValue.content];
     UIAlertView *alert = [[UIAlertView alloc]
     initWithTitle:@"You selected"
     message:message delegate:nil
     cancelButtonTitle:@"OK"
     otherButtonTitles:nil];
     [alert show];
     [alert release];
     [message release];
    /Users/Susheng/Coding/pocketcampus-master/ios/iphone/PocketCampus/Plugins/QAForumPlugin/Controllers/AnswerListViewController.m */
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
@end
