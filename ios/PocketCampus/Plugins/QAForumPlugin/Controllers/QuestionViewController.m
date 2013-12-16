//
//  questionViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "questionViewController.h"



@interface QuestionViewController ()

@end

@implementation QuestionViewController
@synthesize questionLabel, data, notificationid,answerText,scrollView,activeField,pending,username,othername,type,centerIndicator,bnReport,bnSubmit, lbAnswer;
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
    self.title = NSLocalizedStringFromTable(@"Question", @"QAForumPlugin", nil);
    centerIndicator.hidden = true;
    lbAnswer.text = NSLocalizedStringFromTable(@"GiveAnswer", @"QAForumPlugin", nil);
    [bnReport setTitle:NSLocalizedStringFromTable(@"Report", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    [bnSubmit setTitle:NSLocalizedStringFromTable(@"Submit", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    [bnReport setTitleColor:[UIColor grayColor] forState:UIControlStateDisabled];
    [bnSubmit setTitleColor:[UIColor grayColor] forState:UIControlStateDisabled];
    [self registerForKeyboardNotifications];
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    NSMutableDictionary *deserializedData = [data objectFromJSONString];
    questionLabel.text = [NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"content"]];
    [username setTitle:[NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"askername"]] forState:UIControlStateNormal];
    self.othername = [NSString stringWithFormat:@"%@",[deserializedData objectForKey:@"askername"]];
    if (type==0) {
        notificationid = [[deserializedData objectForKey:@"forwardid"] integerValue];
    }
    else
    {
        notificationid = [[deserializedData objectForKey:@"questionid"] integerValue];
    }
    
    [answerText.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
    [answerText.layer setBorderWidth:2.0];
    answerText.layer.cornerRadius = 5;
    answerText.clipsToBounds = YES;
    // change the back button and add an event handler
    if (pending != 1) {
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

- (IBAction)report:(UIButton *)sender {

    ReportViewController* viewController = [ReportViewController alloc];
    viewController.type = 0;
    viewController.forwardid = notificationid;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (IBAction)submit:(UIButton *)sender {
    if (answerText.text.length==0) {
        //text could no be empty
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                        message:NSLocalizedStringFromTable(@"EmptyAnswer", @"QAForumPlugin",nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin",nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    if (answerText.text.length > 1000) {
        //text could no be empty
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                        message:NSLocalizedStringFromTable(@"LongAlert", @"QAForumPlugin",nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin",nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    if (![answerText.text canBeConvertedToEncoding:NSISOLatin1StringEncoding]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EnglishAnswer", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    s_answer* myanswer = [[s_answer alloc] initWithSessionid:[QAForumService lastSessionId].sessionid forwardid:notificationid answer:answerText.text typeid:type];
    [qaforumService answerQuestionWithAnswer:myanswer delegate:self];
    [myanswer release];
    [self.navigationController popToRootViewControllerAnimated:TRUE];

}

- (void)answerQuestionWithAnswer:(s_answer *)data didReturn:(int32_t)result {
    NSLog(@"%@",@"answer succeed.");
    //nothing to do
}

- (void)answerQuestionFailed {
    [self error];
}

- (NSString *)languageForString:(NSString *) text{
    
    if (text.length < 100) {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, text.length));
    } else {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, 100));
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

- (void)dealloc {
    [questionLabel release];
    [answerText release];
    [answerText release];
    [scrollView release];
    [username release];
    [centerIndicator release];
    [bnSubmit release];
    [bnReport release];
    [lbAnswer release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setQuestionLabel:nil];
    [self setAnswerText:nil];
    [self setAnswerText:nil];
    [self setScrollView:nil];
    [self setUsername:nil];
    [self setCenterIndicator:nil];
    [self setBnSubmit:nil];
    [self setBnReport:nil];
    [self setLbAnswer:nil];
    [super viewDidUnload];
}

// Call this method somewhere in your view controller setup code.
- (void)registerForKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillBeHidden:)
                                                 name:UIKeyboardWillHideNotification object:nil];
    
}

// Called when the UIKeyboardDidShowNotification is sent.
- (void)keyboardWasShown:(NSNotification*)aNotification
{
    NSDictionary* info = [aNotification userInfo];
    CGSize kbSize = [[info objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
    
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, kbSize.height, 0.0);
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
    
    // If active text field is hidden by keyboard, scroll it so it's visible
    // Your application might not need or want this behavior.
    CGRect aRect = self.view.frame;
    aRect.size.height -= kbSize.height;
    if (!CGRectContainsPoint(aRect, activeField.frame.origin) ) {
        CGPoint scrollPoint = CGPointMake(0.0, activeField.frame.origin.y-kbSize.height);
        [scrollView setContentOffset:scrollPoint animated:YES];
    }
}

// Called when the UIKeyboardWillHideNotification is sent
- (void)keyboardWillBeHidden:(NSNotification*)aNotification
{

    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
    [self.scrollView scrollRectToVisible:scrollView.frame animated:YES];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    activeField = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    activeField = nil;
}

- (IBAction)userAction:(UIButton *)sender {
    s_relation* temp = [[s_relation alloc] initWithMyuserid:[QAForumService lastSessionId].sessionid otheruserid:self.othername];
    [qaforumService RelationshipWithRelation:temp delegate:self];
    [temp release];
    [self connectionStart];
}

- (void)connectionStart
{
    bnSubmit.enabled = false;
    bnReport.enabled = false;
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
- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}
@end
