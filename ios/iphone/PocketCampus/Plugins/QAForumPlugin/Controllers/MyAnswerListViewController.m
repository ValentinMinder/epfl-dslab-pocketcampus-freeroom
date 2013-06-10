//
//  myAnswerListViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/12/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyAnswerListViewController.h"

#import "MyAnswerViewController.h"

@interface MyAnswerListViewController ()

@end

@implementation MyAnswerListViewController
@synthesize tableContents, sortedKeys, data, tableview;
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
    self.title = @"My Answers";
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    NSMutableArray* topic1 = [[NSMutableArray alloc] init];
    NSMutableArray* topic2 = [[NSMutableArray alloc] init];
    NSMutableArray* topic3 = [[NSMutableArray alloc] init];
    NSMutableArray* topic4 = [[NSMutableArray alloc] init];
    NSLog(@"%@",@"start to receive the answerlist");
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    NSMutableArray* temp = [deserializedData objectForKey:@"myanswerlist"];
    for (NSDictionary* question in temp) {
        TableCell* newCell = [[TableCell alloc] initWithQuestionid:[[question objectForKey:@"forwardid"] integerValue] Content:[question objectForKey:@"content"]];
        if ([[question objectForKey:@"topicid"] isEqualToString:@"Travel"]) {
            [topic1 addObject:newCell];
        } else if([[question objectForKey:@"topicid"] isEqualToString:@"Study"]) {
            [topic2 addObject:newCell];
        } else if([[question objectForKey:@"topicid"] isEqualToString:@"Living"]) {
            [topic3 addObject:newCell];
        } else {
            [topic4 addObject:newCell];
        }
    }
    
    if ([temp count]==0) {
        [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"NoAnswer", @"QAForumPlugin", nil)];
        tableview.hidden = YES;
    }
    
    self.tableContents = [[NSDictionary alloc] initWithObjectsAndKeys:topic1,@"Travel",topic2, @"Study", topic3, @"Living", topic4, @"Others", nil];
    //self.sortedKeys =[[self.tableContents allKeys] sortedArrayUsingSelector:@selector(compare:)];
    self.sortedKeys =[self.tableContents allKeys];
    
    self.navigationItem.leftBarButtonItem =
    [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Back", @"QAForumPlugin",nil)
                                     style:UIBarButtonItemStyleBordered
                                    target:self
                                    action:@selector(handleBack:)];
    
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
        cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.font = [UIFont fontWithName:@"Helvetica" size:17.0];
    }
    
    NSUInteger row = [indexPath row];
    TableCell* cellTemp = [listData objectAtIndex:row];
    NSString* displayText = [NSString stringWithFormat:@"%d. %@", row+1,cellTemp.content];
    cell.textLabel.text =  displayText;
    return cell;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    NSUInteger row = [indexPath row];
    TableCell* cellTemp = [listData objectAtIndex:row];
    NSString *cellText = [NSString stringWithFormat:@"%d. %@", row+1,cellTemp.content];
    UIFont *cellFont = [UIFont fontWithName:@"Helvetica" size:17.0];
    CGSize constraintSize = CGSizeMake(280.0f, MAXFLOAT);
    CGSize labelSize = [cellText sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
    
    return labelSize.height + 20;
}

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    NSUInteger row = [indexPath row];
    TableCell *rowValue = [listData objectAtIndex:row];
    
    [qaforumService oneAnswerWithUserid:rowValue.questionid delegate:self];
    
    
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
     */
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)oneAnswerWithForwardid:(int)data didReturn:(NSString *)result {
    
    MyAnswerViewController* viewController = [MyAnswerViewController alloc];
    viewController.data = result;
    [self.navigationController pushViewController:viewController animated:YES];
    NSLog(@"%@",result);
}

- (void)oneAnswerFailed {
    [self error];
}

-(void)handleBack:(id)sender
{
    NSLog(@"About to go back to the first screen..");
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}

- (IBAction)MyQuestionsList:(UIBarButtonItem *)sender {
    MyQuestionListViewController* viewController = [MyQuestionListViewController alloc];
    viewController.data = data;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (IBAction)MyAnswersList:(UIBarButtonItem *)sender {
    //do nothing
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
