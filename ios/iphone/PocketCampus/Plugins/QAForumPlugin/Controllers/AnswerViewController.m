//
//  answerViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "answerViewController.h"

@interface AnswerViewController ()

@end

@implementation AnswerViewController
@synthesize questionLabel, answerLabel, feedbackLabel, data, forwardid, rateView, rate, pending, bnUser, centerIndicator, bnReport, bnSubmit, othername, lbfeedback, lbrate;
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
    [self connectionEnd];
    self.title = NSLocalizedStringFromTable(@"Answer", @"QAForumPlugin", nil);
    lbfeedback.text = NSLocalizedStringFromTable(@"GiveFeedback", @"QAForumPlugin",nil);
    lbrate.text = NSLocalizedStringFromTable(@"RateAnswer", @"QAForumPlugin",nil);
    [bnReport setTitle:NSLocalizedStringFromTable(@"Report", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    [bnSubmit setTitle:NSLocalizedStringFromTable(@"Submit", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    questionLabel.text = [NSString stringWithFormat:@"Q: %@",[deserializedData objectForKey:@"content"]];
    answerLabel.text = [NSString stringWithFormat:@"A: %@",[deserializedData objectForKey:@"answer"]];
    forwardid = [[deserializedData objectForKey:@"forwardid"] integerValue];
    othername = [deserializedData objectForKey:@"replierid"];
    [bnUser setTitle:self.othername forState:UIControlStateNormal];
    self.rateView.notSelectedImage = [UIImage imageNamed:@"kermit_empty.png"];
    self.rateView.halfSelectedImage = [UIImage imageNamed:@"kermit_half.png"];
    self.rateView.fullSelectedImage = [UIImage imageNamed:@"kermit_full.png"];
    self.rateView.rating = 5;
    self.rateView.editable = YES;
    self.rateView.maxRating = 5;
    self.rateView.delegate = self;
    if (pending!=1) {
        self.navigationItem.leftBarButtonItem =
        [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Back", @"QAForumPlugin",nil)
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

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void)dealloc {
    [questionLabel release];
    [answerLabel release];
    [feedbackLabel release];
    [rateView release];
    [bnUser release];
    [centerIndicator release];
    [bnSubmit release];
    [bnReport release];
    [lbfeedback release];
    [lbrate release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setQuestionLabel:nil];
    [self setAnswerLabel:nil];
    [self setFeedbackLabel:nil];
    [self setRateView:nil];
    [self setBnUser:nil];
    [self setCenterIndicator:nil];
    [self setBnReport:nil];
    [self setBnSubmit:nil];
    [self setBnReport:nil];
    [self setLbfeedback:nil];
    [self setLbrate:nil];
    [super viewDidUnload];
}
- (IBAction)submit:(UIButton *)sender {
    NSString* feedback = feedbackLabel.text;
    if ([feedback length] == 0) {
        // empty answer
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EmptyFeedback", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    if ([feedback length] > 1000) {
        // empty answer
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"LongAlert", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    if (![[self languageForString:feedback] isEqualToString:@"en"]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EnglishFeedback", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
        //send the answer
        s_feedback* feedbackData = [[s_feedback alloc] initWithSessionid:[QAForumService lastSessionId].sessionid forwardid:forwardid feedback:feedback rate:rate];
        [qaforumService feedbackQuestionWithFeedback:feedbackData delegate:self];
        [feedbackData release];
        [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)feedbackQuestionFailed {
    [self error];
}

- (void)feedbackQuestionWithFeedback:(s_feedback *)data didReturn:(int32_t)result {
    //
}

- (NSString *)languageForString:(NSString *) text{
    
    if (text.length < 100) {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, text.length));
    } else {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, 100));
    }
}

- (IBAction)report:(UIButton *)sender {
    ReportViewController* viewController = [ReportViewController alloc];
    viewController.type = 0;
    viewController.forwardid = forwardid;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (void) rateView:(RateView *)rateView ratingDidChange:(float)rating
{
    rate = rating;
}

- (void)connectionStart
{
    bnReport.enabled = false;
    bnSubmit.enabled = false;
    centerIndicator.hidden = false;
    [centerIndicator startAnimating];
}
- (void)connectionEnd
{
    bnSubmit.enabled = true;
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

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}
@end
