//
//  LatestForumViewController.m
//  PocketCampus
//
//  Created by Susheng on 4/20/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "LatestForumViewController.h"

#import "ForumTableCell.h"

@interface LatestForumViewController ()

@end


@implementation LatestForumViewController
@synthesize data, tableData, tableview, locked;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    [tableview deselectRowAtIndexPath:[tableview indexPathForSelectedRow] animated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    locked = 0;
    self.title = NSLocalizedStringFromTable(@"Forum", @"QAForumPlugin", nil);
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    NSMutableArray* temp = [deserializedData objectForKey:@"questionlist"];
    NSMutableArray* questionlist = [[NSMutableArray alloc] init];
    for (NSDictionary* question in temp) {
        [questionlist addObject:question];
    }
    self.tableData = questionlist;
    
    UILongPressGestureRecognizer *lpgr = [[UILongPressGestureRecognizer alloc]
                                          initWithTarget:self action:@selector(handleLongPress:)];
    lpgr.minimumPressDuration = 1.0; //seconds
    lpgr.delegate = (id)self;
    [self.tableview addGestureRecognizer:lpgr];
    [lpgr release];

}

- (IBAction)handleLongPress:(UILongPressGestureRecognizer *)gestureRecognizer
{
    if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
        
        CGPoint p = [gestureRecognizer locationInView:self.tableview];
        
        NSIndexPath *indexPath = [self.tableview indexPathForRowAtPoint:p];
        if (indexPath == nil) {
            //NSLog(@"long press on table view but not on a row");
        } else {
            UITableViewCell *cell = [self.tableview cellForRowAtIndexPath:indexPath];
            if (cell.isHighlighted) {
                NSDictionary* onequestion = [self.tableData objectAtIndex:indexPath.row];
                s_relation* relation = [[s_relation alloc] initWithMyuserid:[QAForumService lastSessionId].sessionid otheruserid:[onequestion objectForKey:@"askername"]];
                [qaforumService RelationshipWithRelation:relation delegate:self];
                [relation release];
            }
        }
    }
}

- (void)RelationshipFailed {
    [self error];
}

- (void)RelationshipWithRelation:(s_relation *)relation didReturn:(NSString *)result {
    
    NSMutableDictionary *deserializedData = [result objectFromJSONString];
    NSString* nameString = [NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"name"]];
    NSString* path = [NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"path"]];
    NSString* questions = [NSString stringWithFormat:@"Total questions: %@",[deserializedData objectForKey:@"question"]];
    NSString* message;
    if ([deserializedData count]==3) {
        message = [NSString stringWithFormat:@"%@\n%@\n",questions,path];
    }
    else
    {
        NSString* language = [NSString stringWithFormat:@"Language: %@",[deserializedData objectForKey:@"language"]];
        NSString* answerme = [NSString stringWithFormat:@"Times of answering me:%@",[deserializedData objectForKey:@"answerme"]];
        NSString* answerall = [NSString stringWithFormat:@"Times of answering: %@",[deserializedData objectForKey:@"answerall"]];
        NSString* reputation = [NSString stringWithFormat:@"Reputation: %@",[deserializedData objectForKey:@"reputation"]];
        NSString* online = @"";
        if([[NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"online"]] isEqualToString:@"1"]) {
            online = [NSString stringWithFormat:@"Status: %@",@"ONLINE"];
        }
        else {
            online = [NSString stringWithFormat:@"Status: %@",@"OFFLINE"];
        }
        
        NSString* topic = [NSString stringWithFormat:@"Topics interested in: %@",[deserializedData objectForKey:@"topic"]];
        
        message = [NSString stringWithFormat:@"%@\n%@\n%@\n%@\n%@\n%@\n%@\n%@\n",reputation,questions,answerme,answerall,language,topic,online,path];
    }
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nameString message:message delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin",nil) otherButtonTitles:nil,nil];
    [alert show];
    [alert release];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.tableData count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *simpleTableIdentifier = @"ForumTableCell";
    
    ForumTableCell *cell = (ForumTableCell*)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"ForumTableCell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    if([[[self.tableData objectAtIndex:indexPath.row] valueForKey:@"closed"] intValue]==1)
    {
        UIImage* image = [UIImage imageNamed:@"qaforum_right"];
        cell.imageView.image = image;
    }
    else
    {
        UIImage* image = [UIImage imageNamed:@"qaforum_question"];
        cell.imageView.image = image;
    }
    cell.orderNumber.text = [NSString stringWithFormat:@"%d.",indexPath.row+1];
    cell.content.text = [[self.tableData objectAtIndex:indexPath.row] valueForKey:@"content"];
    cell.time.text = [[self.tableData objectAtIndex:indexPath.row] valueForKey:@"time"];
    cell.username.text = [[self.tableData objectAtIndex:indexPath.row] valueForKey:@"askername"];
    return cell;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 73;
}

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (locked==0) {
        if ([[[self.tableData objectAtIndex:indexPath.row] valueForKey:@"closed"] intValue] == 1) {
            [qaforumService oneQuestionWithQuestionid:[[[self.tableData objectAtIndex:indexPath.row] valueForKey:@"questionid"] intValue]  delegate:self];
            locked=1;
        }
        else
        {
            QuestionViewController* viewController = [QuestionViewController alloc];
            viewController.data = [[self.tableData objectAtIndex:indexPath.row] JSONString];
            viewController.pending = 1;
            viewController.type = 1;
            viewController.title = NSLocalizedStringFromTable(@"OpenQuestion", @"QAForumPlugin", nil);
            [self.navigationController pushViewController:viewController animated:YES];
            [tableView deselectRowAtIndexPath:indexPath animated:YES];
        }
    }
    
}


-(void)oneQuestionWithQuestionid:(int)data didReturn:(NSString *)result {
    MyQuestionViewController* viewController = [MyQuestionViewController alloc];
    locked = 0;
    viewController.data = result;
    viewController.title = NSLocalizedStringFromTable(@"ClosedQuestion", @"QAForumPlugin", nil);
    [self.navigationController pushViewController:viewController animated:YES];
}

-(void)oneQuestionFailed {
    [self error];
}


- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil)];
    tableview.hidden = YES;
}
- (void)error {
    [PCUtils showServerErrorAlert];
    [PCUtils addCenteredLabelInView:self.view withMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil)];
    tableview.hidden = YES;
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
