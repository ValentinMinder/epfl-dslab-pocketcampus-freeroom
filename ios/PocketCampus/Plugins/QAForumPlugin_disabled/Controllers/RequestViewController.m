//
//  requestViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/24/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "RequestViewController.h"

@interface RequestViewController ()

@end

@implementation RequestViewController
@synthesize Travel, Others, Living, Study, tagsTextView;
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
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)request:(UIButton *)sender {
    NSString* topics = @"";
    if (Travel.on) {
        topics = [NSString stringWithFormat:@"Travel %@", topics];
    }
    if (Study.on){
        topics = [NSString stringWithFormat:@"Study %@", topics];
    }
    if (Living.on){
        topics = [NSString stringWithFormat:@"Living %@", topics];
    }
    if (Others.on){
        topics = [NSString stringWithFormat:@"Others %@", topics];
    }
    if (topics.length == 0) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Invaild input"
                                                        message:@"You have to select at least one topic."
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    [qaforumService requestQuestionWithRequest:[[s_request alloc] initWithSessionid:[QAForumService lastSessionId].sessionid topics:topics tags:tagsTextView.text] delegate:self];
}

- (void)requestQuestionFailed {
    [PCUtils showServerErrorAlert];
}

- (void)requestQuestionWithRequest:(s_request *)data didReturn:(NSString *)result {
    if ([result isEqualToString:@"alert"]) {
        NSString *message = [[NSString alloc] initWithString:@"Sorry, no question available right now. Please try again later."];
        UIAlertView *alert = [[UIAlertView alloc]
                              initWithTitle:@"No Question"
                              message:message delegate:nil
                              cancelButtonTitle:@"OK"
                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        [message release];
    } else {
        QuestionListViewController* viewController = [QuestionListViewController alloc];
        viewController.data = result;
        [self.navigationController pushViewController:viewController animated:YES];
    }
    
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}


- (void)dealloc {
    [tagsTextView release];
    [Travel release];
    [Study release];
    [Living release];
    [Others release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setTagsTextView:nil];
    [self setTravel:nil];
    [self setStudy:nil];
    [self setLiving:nil];
    [self setOthers:nil];
    [super viewDidUnload];
}
- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
}
@end
