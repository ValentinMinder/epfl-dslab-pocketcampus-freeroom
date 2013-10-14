//
//  acceptViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/12/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "acceptViewController.h"


@interface AcceptViewController ()

@end


@implementation AcceptViewController
@synthesize data, notificationid, notificationLable;

- (id)init
{
    self = [super initWithNibName:@"AcceptViewController" bundle:nil];
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

    //display the notification content
    notificationLable.text = data;
    
    
    self.navigationItem.leftBarButtonItem =
    [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Back", @"QAForumPlugin", nil)
                                     style:UIBarButtonItemStyleBordered
                                    target:self
                                    action:@selector(handleBack:)];
    
    //clear the history of lastnotification
    [QAForumService saveLastNotif:nil];
}

-(void)handleBack:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    [data release];
}



- (IBAction)acceptNotifiation:(UIButton *)sender {    
    s_accept *message = [[s_accept alloc] initWithNotificationid:notificationid accept:1];
    [qaforumService acceptNotifWithAccept:message delegate:self];
    [message release];
}

- (void)acceptNotifWithAccept:(s_accept *)data didReturn:(NSString *)result {
    if ([result isEqualToString:@"declined"]) {
        //declined
        [self.navigationController popToRootViewControllerAnimated:TRUE];
    }else{
        //accepted
        NSMutableDictionary *deserializedData = [result objectFromJSONString];
        NSString* type = [deserializedData objectForKey:@"type"];
        if ([type isEqualToString:@"forwardquestion"]) {
            NSLog(@"%@",@"forwardquestion");
            //call new view
            if ([[deserializedData objectForKey:@"number"] integerValue]==1) {
                QuestionViewController* viewController = [QuestionViewController alloc];
                NSMutableArray* temp = [deserializedData objectForKey:@"questionlist"];
                viewController.data = [temp[0] JSONString];
                [self.navigationController pushViewController:viewController animated:YES];
            } else {
                QuestionListViewController* viewController = [QuestionListViewController alloc];
                [self.navigationController pushViewController:viewController animated:YES];
            }
            
        }else if ([type isEqualToString:@"forwardanswer"]) {
            NSLog(@"%@",@"forwardanswer");
            //call new view
            if ([[deserializedData objectForKey:@"number"] integerValue]==1) {
                AnswerViewController* viewController = [AnswerViewController alloc];
                NSMutableArray* temp = [deserializedData objectForKey:@"answerlist"];
                viewController.data = [temp[0] JSONString];
                [self.navigationController pushViewController:viewController animated:YES];
            } else {
                AnswerListViewController* viewController = [AnswerListViewController alloc];
                [self.navigationController pushViewController:viewController animated:YES];
            }
        }else if ([type isEqualToString:@"transfer"]) {
            NSLog(@"%@",@"forwardfeedback");
            //call new view
            if ([[deserializedData objectForKey:@"number"] integerValue]==1) {
                FeedbackViewController* viewController = [FeedbackViewController alloc];
                NSMutableArray* temp = [deserializedData objectForKey:@"feedbacklist"];
                viewController.data = [temp[0] JSONString];
                [self.navigationController pushViewController:viewController animated:YES];
            } else {
                FeedbackListViewController* viewController = [FeedbackListViewController alloc];
                [self.navigationController pushViewController:viewController animated:YES];
            }
        }else if ([type isEqualToString:@"alert"]) {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Alert", @"QAForumPlugin", nil)
                                                            message:NSLocalizedStringFromTable(@"MessageDeleted", @"QAForumPlugin", nil)
                                                           delegate:nil
                                                  cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                                  otherButtonTitles:nil];
            [alert show];
            [alert release];
            [self.navigationController popToRootViewControllerAnimated:TRUE];
        }
    }
}

- (void)acceptNotifFailed {
    [self error];
}

- (IBAction)declineNotification:(UIButton *)sender {
    s_accept *message = [[s_accept alloc] initWithNotificationid:notificationid accept:0];
    [qaforumService acceptNotifWithAccept:message delegate:self];
    [message release];
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}
- (void)dealloc {
    [notificationLable release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setNotificationLable:nil];
    [super viewDidUnload];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}
@end
