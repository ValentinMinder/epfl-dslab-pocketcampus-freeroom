//
//  PendingViewController.m
//  PocketCampus
//
//  Created by Susheng on 4/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PendingViewController.h"

#import "CompundTableCell.h"

#import "PendingTableCell.h"

@interface PendingViewController ()

@end

@implementation PendingViewController
@synthesize tableContents, sortedKeys, data, tableView, forwardid, typeint, rowint, sectionint;

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
    self.title = NSLocalizedStringFromTable(@"PendingMsg", @"QAForumPlugin", nil);
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    NSMutableArray* questioncells = [[NSMutableArray alloc] init];
    NSMutableArray* answercells = [[NSMutableArray alloc] init];
    NSMutableArray* feedbackcells = [[NSMutableArray alloc] init];

    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    NSMutableArray* questionlist = [deserializedData objectForKey:@"questionlist"];
    NSMutableArray* answerlist = [deserializedData objectForKey:@"answerlist"];
    NSMutableArray* feedbacklist = [deserializedData objectForKey:@"feedbacklist"];
    for (NSDictionary* question in questionlist) {
        CompundTableCell* newCell = [[CompundTableCell alloc] initWithDic:question Type:0 Content:[question objectForKey:@"content"] Time:[question objectForKey:@"time"] Name:[question objectForKey:@"askername"]];
        [questioncells addObject:newCell];
        [newCell release];
    }
    for (NSDictionary* answer in answerlist) {
        CompundTableCell* newCell = [[CompundTableCell alloc] initWithDic:answer Type:1 Content:[answer objectForKey:@"content"] Time:[answer objectForKey:@"time"] Name:[answer objectForKey:@"replierid"]];
        [answercells addObject:newCell];
        [newCell release];
    }
    for (NSDictionary* feedback in feedbacklist) {
        CompundTableCell* newCell = [[CompundTableCell alloc] initWithDic:feedback Type:2 Content:[feedback objectForKey:@"question"] Time:[feedback objectForKey:@"feedbacktime"] Name:[feedback objectForKey:@"userid"]];
        [feedbackcells addObject:newCell];
        [newCell release];
    }
    
    if ([questionlist count]==0 && [answerlist count]==0 && [feedbacklist count]==0) {
        [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"NoPending", @"QAForumPlugin", nil)];
        self.tableView.hidden = YES;
    }
    self.tableContents = [[NSMutableDictionary alloc] initWithObjectsAndKeys:questioncells,NSLocalizedStringFromTable(@"PendingQ", @"QAForumPlugin", nil),answercells, NSLocalizedStringFromTable(@"PendingA", @"QAForumPlugin", nil), feedbackcells, NSLocalizedStringFromTable(@"PendingF", @"QAForumPlugin", nil), nil];
    
    self.sortedKeys =[self.tableContents allKeys];
    
    UILongPressGestureRecognizer *lpgr = [[UILongPressGestureRecognizer alloc]
                                          initWithTarget:self action:@selector(handleLongPress:)];
    lpgr.minimumPressDuration = 1.0; //seconds
    lpgr.delegate = (id)self;
    [self.tableView addGestureRecognizer:lpgr];
    [lpgr release];
    
}

- (IBAction)handleLongPress:(UILongPressGestureRecognizer *)gestureRecognizer
{
    if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
        
        CGPoint p = [gestureRecognizer locationInView:self.tableView];
        
        NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:p];
        if (indexPath == nil) {
            //NSLog(@"long press on table view but not on a row");
        } else {
            UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
            if (cell.isHighlighted) {
                //NSLog(@"long press on table view at section %d row %d", indexPath.section, indexPath.row);
                NSString* message = NSLocalizedStringFromTable(@"ToDelete", @"QAForumPlugin", nil);
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"PendingDelete", @"QAForumPlugin", nil) message:message delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"QAForumPlugin", nil) otherButtonTitles:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil),nil];
                rowint = [indexPath row];
                NSMutableArray* listData =[self.tableContents objectForKey:
                                    [self.sortedKeys objectAtIndex:[indexPath section]]];
                NSUInteger row = [indexPath row];
                CompundTableCell *rowValue = [listData objectAtIndex:row];
                typeint = rowValue.type;
                sectionint = [indexPath section];
                forwardid = [[rowValue.data objectForKey:@"forwardid"] integerValue];
                [alert show];
                [alert release];

            }
        }
    }
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {

    }
    else if (buttonIndex == 1) {
        s_delete* temp = [[s_delete alloc] initWithUserid:[QAForumService lastSessionId].sessionid forwardid:forwardid type:typeint];
        [[self.tableContents objectForKey:
         [self.sortedKeys objectAtIndex:sectionint]] removeObjectAtIndex:rowint];
        [self.tableView reloadData];
        [qaforumService DeleteNotificationWithDelete:temp delegate:self];
    }
}

- (void)DeleteNotificationFailed {
    [self error];
}

- (void)DeleteNotificationWithDelete:(s_delete *)deleteinfo didReturn:(int32_t)result {
    NSLog(@"%@",@"delete message succeeded");    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    [tableContents release];
    [sortedKeys release];
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

- (UITableViewCell *)tableView:(UITableView *)tableViewLocal cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *simpleTableIdentifier = @"PendingTableCell";
    
    PendingTableCell *cell = (PendingTableCell*)[tableViewLocal dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"PendingTableCell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    //NSLog(@"%@",[self.tableData objectAtIndex:indexPath.row]);
    //cell.orderNumber.text = [NSString stringWithFormat:@"%d.",indexPath.row+1];
    CompundTableCell* cellTemp = [[self.tableContents objectForKey:[self.sortedKeys objectAtIndex:[indexPath section]]] objectAtIndex:[indexPath row]];
    cell.orderNumber.text = [NSString stringWithFormat:@"%d.",[indexPath row]+1];
    cell.content.text = cellTemp.content;
    cell.time.text = cellTemp.time;
    cell.username.text = cellTemp.name;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 73;
}

- (void)tableView:(UITableView *)tableViewLocal
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *listData =[self.tableContents objectForKey:
                        [self.sortedKeys objectAtIndex:[indexPath section]]];
    NSUInteger row = [indexPath row];
    CompundTableCell *rowValue = [listData objectAtIndex:row];
    
    if (rowValue.type==0) {
        QuestionViewController* viewController = [QuestionViewController alloc];
        viewController.data = [rowValue.data JSONString];
        viewController.pending = 1;
        [self.navigationController pushViewController:viewController animated:YES];
    } else if (rowValue.type==1) {
        //AnswerViewController* viewController = [AnswerViewController alloc];
        AnswerViewController* viewController = [AnswerViewController alloc];
        viewController.data = [rowValue.data JSONString];
        viewController.pending = 1;
        [self.navigationController pushViewController:viewController animated:YES];
    } else {
        FeedbackViewController* viewController = [FeedbackViewController alloc];
        viewController.data = [rowValue.data JSONString];
        viewController.pending = 1;
        [self.navigationController pushViewController:viewController animated:YES];
    }
    [tableViewLocal deselectRowAtIndexPath:indexPath animated:YES];
}


- (void)dealloc {
    [tableView release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setTableView:nil];
    [super viewDidUnload];
}
- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil)];
    self.tableView.hidden = YES;
}
- (void)error {
    [PCUtils showServerErrorAlert];
    [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil)];
    self.tableView.hidden = YES;
}
@end
