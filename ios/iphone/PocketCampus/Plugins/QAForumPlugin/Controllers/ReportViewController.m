//
//  reportViewController.m
//  PocketCampus
//
//  Created by Susheng on 1/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "ReportViewController.h"

@interface ReportViewController ()

@end

@implementation ReportViewController
@synthesize forwardid, type, contentTextView, scrollView, activeField, reportContent, bnSubmit;
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
    self.title = NSLocalizedStringFromTable(@"Report", @"QAForumPlugin", nil);
    [reportContent setText:NSLocalizedStringFromTable(@"ReportContent", @"QAForumPlugin", nil)];
    [bnSubmit setTitle:NSLocalizedStringFromTable(@"Submit", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    [contentTextView.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
    [contentTextView.layer setBorderWidth:2.0];
    contentTextView.layer.cornerRadius = 5;
    contentTextView.clipsToBounds = YES;
    [self registerForKeyboardNotifications];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [contentTextView release];
    [scrollView release];
    [scrollView release];
    [reportContent release];
    [bnSubmit release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setContentTextView:nil];
    [self setScrollView:nil];
    [self setScrollView:nil];
    [self setReportContent:nil];
    [self setBnSubmit:nil];
    [super viewDidUnload];
}
- (IBAction)submit:(UIButton *)sender {
    
    if (contentTextView.text.length == 0) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EmptyAlert", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    if (contentTextView.text.length > 1000) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"LongAlert", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    s_report* report = [[s_report alloc] initWithSessionid:[QAForumService lastSessionId].sessionid forwardid:forwardid type:type comment:contentTextView.text];
    [qaforumService reportQuestionWithReport:report delegate:self];
    [report release];
    
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
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

- (void)reportQuestionFailed {
    [self error];
}

- (void)reportQuestionWithReport:(s_report *)data didReturn:(int32_t)result {
    [self.navigationController popViewControllerAnimated:TRUE];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
- (void)error {
    [PCUtils showServerErrorAlert];
}
@end
