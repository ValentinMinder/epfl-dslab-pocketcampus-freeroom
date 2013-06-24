//
//  AnswerRecViewController.m
//  PocketCampus
//
//  Created by Susheng on 5/24/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "AnswerRecViewController.h"

@interface AnswerRecViewController ()

@end

@implementation AnswerRecViewController
@synthesize tableview,data;
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
    //tableview.separatorStyle = UITableViewCellSeparatorStyleNone;
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
    int row = [indexPath row];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    if (row == 0) {
        cell.textLabel.text =  [NSString stringWithFormat:@"Q: %@", [[data objectFromJSONString] objectForKey:@"content"]];
    }
    else if(row == 1){
        cell.textLabel.text =  [NSString stringWithFormat:@"A: %@", [[data objectFromJSONString] objectForKey:@"answer"]];
    }
    
    else if (row == 3){
        cell.textLabel.text = @"Please give your feedback.";
    }
    else if (row == 4){
        UITextView *tf = [[UITextView alloc] init];
        int width = cell.contentView.frame.size.width;
        tf.font = [UIFont fontWithName:@"Helvetica" size:17.0];
        tf.frame = CGRectMake(10,10,width-40,50);
        tf.layer.borderWidth=1.0f;
        tf.layer.borderColor = [[UIColor redColor] CGColor];
        tf.delegate = (id)self;
        [cell.contentView addSubview:tf];
    }
    else if (row == 5){
        
    }
    else if(row==6){
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        int width = cell.contentView.frame.size.width;
        btn.frame = CGRectMake(20,10,width-40,50);
        [btn setTitle:@"Send" forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(bnSubmitQuestion) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:btn];
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 7;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return NO;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [tableview release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setTableview:nil];
    [super viewDidUnload];
}
- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}
@end
