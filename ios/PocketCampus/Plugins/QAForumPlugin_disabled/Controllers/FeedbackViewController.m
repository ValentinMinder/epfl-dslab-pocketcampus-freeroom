//
//  feedbackViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "feedbackViewController.h"

@interface FeedbackViewController ()

@end

@implementation FeedbackViewController

@synthesize questionLabel, answerLabel, feedbackLabel, rateLabel, data, notificationid,rateView,rate,pending, bnUser, othername, bnReport, centerIndicator, scrollView;
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
    [self connectionEnd];
    self.title = NSLocalizedStringFromTable(@"Feedback", @"QAForumPlugin", nil);
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    [bnReport setTitle:NSLocalizedStringFromTable(@"Report", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    questionLabel.text = [NSString stringWithFormat:@"%@: %@",NSLocalizedStringFromTable(@"Question", @"QAForumPlugin",nil),[deserializedData objectForKey:@"question"]];
    answerLabel.text = [NSString stringWithFormat:@"%@: %@",NSLocalizedStringFromTable(@"Answer", @"QAForumPlugin",nil),[deserializedData objectForKey:@"answer"]];
    notificationid = [[deserializedData objectForKey:@"forwardid"] integerValue];
    feedbackLabel.text = [NSString stringWithFormat:@"%@: %@",NSLocalizedStringFromTable(@"Feedback", @"QAForumPlugin",nil),[deserializedData objectForKey:@"feedback"]];
    self.othername = [NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"userid"]];
    [bnUser setTitle:self.othername forState:UIControlStateNormal];
    self.rateView.notSelectedImage = [UIImage imageNamed:@"kermit_empty.png"];
    self.rateView.halfSelectedImage = [UIImage imageNamed:@"kermit_half.png"];
    self.rateView.fullSelectedImage = [UIImage imageNamed:@"kermit_full.png"];
    self.rateView.rating = [[deserializedData objectForKey:@"rate"] doubleValue];
    self.rateView.editable = NO;
    self.rateView.maxRating = 5;
    self.rateView.delegate = (id)self;
    
    if (pending!=1) {
        self.navigationItem.leftBarButtonItem =
        [[UIBarButtonItem alloc] initWithTitle:@"Back"
                                         style:UIBarButtonItemStyleBordered
                                        target:self
                                        action:@selector(handleBack:)];
    }
}


-(void)handleBack:(id)sender
{
    NSLog(@"About to go back to the first screen..");
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)report:(UIButton *)sender {
    ReportViewController* viewController = [ReportViewController alloc];
    [self.navigationController pushViewController:viewController animated:YES];
}

- (void)dealloc {
    [rateLabel release];
    [feedbackLabel release];
    [questionLabel release];
    [answerLabel release];
    [rateView release];
    [bnUser release];
    [bnReport release];
    [centerIndicator release];
    [scrollView release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setRateLabel:nil];
    [self setFeedbackLabel:nil];
    [self setQuestionLabel:nil];
    [self setAnswerLabel:nil];
    [self setRateView:nil];
    [self setBnUser:nil];
    [self setBnReport:nil];
    [self setCenterIndicator:nil];
    [self setScrollView:nil];
    [super viewDidUnload];
}

- (void) rateView:(RateView *)rateView ratingDidChange:(float)rating {
    NSLog(@"%f",rating);
    rate = rating;
}
- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}

- (void)connectionStart
{
    bnReport.enabled = false;
    centerIndicator.hidden = false;
    [centerIndicator startAnimating];
}
- (void)connectionEnd
{
    bnReport.enabled = true;
    centerIndicator.hidden = true;
    [centerIndicator stopAnimating];
}

- (IBAction)Relationship:(id)sender {
    s_relation* temp = [[s_relation alloc] initWithMyuserid:[QAForumService lastSessionId].sessionid otheruserid:self.othername];
    [qaforumService RelationshipWithRelation:temp delegate:self];
    [temp release];
    [self connectionStart];
}
- (void)RelationshipFailed {
    [self error];
}

- (void)RelationshipWithRelation:(s_relation *)relation didReturn:(NSString *)result {
    [self connectionEnd];
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

@end
