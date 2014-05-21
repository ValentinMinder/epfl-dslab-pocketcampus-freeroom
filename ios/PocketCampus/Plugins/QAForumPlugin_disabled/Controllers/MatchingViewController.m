//
//  MatchingViewController.m
//  PocketCampus
//
//  Created by Susheng on 5/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MatchingViewController.h"

#import "QAFListCell.h"

@interface MatchingViewController ()

@end

@implementation MatchingViewController
@synthesize tableData,tableview,data,question;

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
    self.title = NSLocalizedStringFromTable(@"SimilarQues", @"QAForumPlugin", nil);
    NSLog(@"%@",data);
    NSMutableArray* questionlist = [[NSMutableArray alloc] init];
    for(id key in data) {
        NSString* value = [data objectForKey:key];
        QAFListCell* temp = [[QAFListCell alloc] initWithContent:value Data:key];
        [questionlist addObject:temp];
    }
    self.tableData = questionlist;

}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.tableData count]+1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *SimpleTableIdentifier = @"SimpleTableIdentifier";
        
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
    
    if (indexPath.row >= [self.tableData count]) {
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        int width = cell.contentView.frame.size.width;
        btn.frame = CGRectMake(20,10,width-40,50);
        [btn setTitle:NSLocalizedStringFromTable(@"SubmitQuestion", @"QAForumPlugin", nil) forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(bnSubmitQuestion) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:btn];
    }
    else
    {
        int row = [indexPath row];
        QAFListCell* cellTemp = [self.tableData objectAtIndex:row];
        NSString* displayText = [NSString stringWithFormat:@"%d. %@", row+1,cellTemp.content];
        cell.textLabel.text =  displayText;
    }
    return cell;
}

- (void)bnSubmitQuestion
{
    [qaforumService askQuestionWithQuestion:self.question delegate:self];
}

- (void)askQuestionWithQuestion:(s_ask *)data didReturn:(int32_t)result {
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}

- (void)askQuestionFailed {
    [self error];
}

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row >= [self.tableData count])
    {
        return;
    }
    QAFListCell* temp = [self.tableData objectAtIndex:[indexPath row]];
    int quesid = [temp.data intValue];
    [qaforumService oneQuestionWithQuestionid:quesid delegate:self];
}

- (void)oneQuestionWithQuestionid:(int)data didReturn:(NSString *)result {
    MyQuestionViewController* viewController = [MyQuestionViewController alloc];
    viewController.data = result;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (void)oneQuestionFailed {
    [self error];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([indexPath row]>=[self.tableData count]) {
        return 70;
    }
    QAFListCell * temp =  [self.tableData objectAtIndex:[indexPath row]];
    NSString *cellText = temp.content;
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
