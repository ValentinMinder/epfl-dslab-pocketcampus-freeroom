//
//  askQuestionViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/8/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "askQuestionViewController.h"

#import "ActionSheetPicker.h"

#import <QuartzCore/QuartzCore.h>

@interface AskQuestionViewController ()

@end

@implementation AskQuestionViewController
@synthesize topicList,QuestionTextView,expiryTimeTextView,tagsTextView,selectedRow,topicselectedRow,scrollView, activeField, QuestionText, topicTextView, question, labExpiry, labQues, labTags, labTopic, bnContinue, matchingNumbers,centerIndicator;
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
    self.title = NSLocalizedStringFromTable(@"Ask", @"QAForumPlugin", nil);
    labQues.text = NSLocalizedStringFromTable(@"Question:", @"QAForumPlugin", nil);
    labTags.text = NSLocalizedStringFromTable(@"Tags:", @"QAForumPlugin", nil);
    labTopic.text = NSLocalizedStringFromTable(@"Topic:", @"QAForumPlugin", nil);
    labExpiry.text = NSLocalizedStringFromTable(@"ExpiryTime:", @"QAForumPlugin", nil);
    [bnContinue setTitle:NSLocalizedStringFromTable(@"Continue", @"QAForumPlugin", nil) forState: UIControlStateNormal];
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    [self registerForKeyboardNotifications];
    
    [QuestionText.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
    [QuestionText.layer setBorderWidth:2.0];
    QuestionText.layer.cornerRadius = 5;
    [QuestionText setPlaceholder:NSLocalizedStringFromTable(@"AskPlaceholder", @"QAForumPlugin",nil)];
    QuestionText.clipsToBounds = YES;
    
    
    
    [tagsTextView setPlaceholder:NSLocalizedStringFromTable(@"TagPlaceholder", @"QAForumPlugin", nil)];
        
    questionDic = [[NSMutableDictionary alloc] init];
    
    [bnContinue setTitleColor:[UIColor grayColor] forState:UIControlStateDisabled];
    
    centerIndicator.hidden = true;
    [centerIndicator stopAnimating];
    
    //Resize the picker, rotate it so that it is horizontal and set its position
    /*CGAffineTransform rotate = CGAffineTransformMakeRotation(-1.57);
    rotate = CGAffineTransformScale(rotate, .46, 2.25);
    CGAffineTransform t0 = CGAffineTransformMakeTranslation(3, 22.5);
    topickPickerView.transform = CGAffineTransformConcat(rotate,t0);*/

}

- (void)viewDidAppear:(BOOL)animated {
    if (sheet!=nil) {
        [sheet showFromRect:self.view.bounds inView:self.view animated:YES];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)thePickerView
{
    return 1;
}

- (void)dealloc {
    [topicList release];
    [expiryTimeTextView release];
    [tagsTextView release];
    [scrollView release];
    [QuestionText release];
    [topicTextView release];
    [tagsTextView release];
    [labQues release];
    [labTopic release];
    [labExpiry release];
    [labTags release];
    [bnContinue release];
    [centerIndicator release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setExpiryTimeTextView:nil];
    [self setTagsTextView:nil];
    [self setScrollView:nil];
    [self setQuestionText:nil];
    [self setTopicTextView:nil];
    [self setTagsTextView:nil];
    [self setLabQues:nil];
    [self setLabTopic:nil];
    [self setLabExpiry:nil];
    [self setLabTags:nil];
    [self setBnContinue:nil];
    [self setCenterIndicator:nil];
    [super viewDidUnload];
}
- (IBAction)submitQuestion:(UIButton *)sender {
    //submit the question to the server
    
    if (QuestionText.text.length == 0) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EmptyAlert", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    if (QuestionText.text.length > 1000) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"LongAlert", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    if (![QuestionText.text canBeConvertedToEncoding:NSISOLatin1StringEncoding]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EnglishQuestion", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    
    if ([topicTextView.text isEqualToString:@""]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Invalidinput", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"EmptyTopic", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    
    NSString* tags = tagsTextView.text;
    int expritytime=0;
    switch (selectedRow) {
		case 0:
			expritytime=5;
			break;
		case 1:
			expritytime=10;
			break;
		case 2:
			expritytime=30;
			break;
		case 3:
			expritytime=60;
			break;
		case 4:
			expritytime=360;
			break;
		case 5:
			expritytime=720;
			break;
		case 6:
			expritytime=1440;
			break;
		case 7:
			expritytime=4320;
			break;
		case 8:
			expritytime=10080;
			break;
		default:
			break;
    }
    NSString* content = QuestionText.text;
    int topic = topicselectedRow+1;
    [bnContinue setEnabled:false];
    //continue...
    question = [[s_ask alloc] initWithSessionid:[QAForumService lastSessionId].sessionid content:content topic:topic tags:tags expirytime:expritytime quesid:0];
    [qaforumService QuestionMatchingWithQuestion:content delegate:self];
    [centerIndicator startAnimating];
    centerIndicator.hidden = false;
}

- (void)QuestionMatchingFailed {
    [self error];
}

- (NSString *)languageForString:(NSString *) text{
    
    if (text.length < 100) {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, text.length));
    } else {
        
        return (NSString *)CFStringTokenizerCopyBestStringLanguage((CFStringRef)text, CFRangeMake(0, 100));
    }
}

- (void)QuestionMatchingWithQuestion:(NSString *)question didReturn:(NSString *)result {
    [bnContinue setEnabled:TRUE];
    [centerIndicator stopAnimating];
    centerIndicator.hidden = true;
    if ([result isEqualToString:@""]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Alert", @"QAForumPlugin", nil)
                                                        message:NSLocalizedStringFromTable(@"QuestionMeaningless", @"QAForumPlugin", nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil)
                                              otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    NSMutableDictionary *deserializedData = [[result objectFromJSONString] mutableCopy];
    [self.question setQuesid:[[deserializedData objectForKey:@"key"] integerValue]];
    [deserializedData removeObjectForKey:@"key"];
    
    MatchingViewController* viewController = [MatchingViewController alloc];
    viewController.data = deserializedData;
    viewController.question = self.question;
    [self.navigationController pushViewController:viewController animated:YES];
    
    /*
    sheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"SimilarQues", @"QAForumPlugin", nil) delegate:self
                                                   cancelButtonTitle:nil
                                                destructiveButtonTitle:nil
                                                   otherButtonTitles:nil];
    sheet.destructiveButtonIndex = [deserializedData count] + 2;
    sheet.cancelButtonIndex = [deserializedData count]+1;
    self.matchingNumbers = [deserializedData count];
    
    int i=0;
    for(id key in deserializedData) {
        NSLog(@"%@",key);
            NSString* value = [deserializedData objectForKey:key];
            if(value.length<50)
                value = [value stringByPaddingToLength:50 withString: @" " startingAtIndex:0];
            [sheet addButtonWithTitle:value];
            [questionDic setObject:key forKey:[NSString stringWithFormat:@"%d",i]];
            i++;
    }
    
    [sheet addButtonWithTitle:NSLocalizedStringFromTable(@"Cancel", @"QAForumPlugin", nil)];
    [sheet addButtonWithTitle:NSLocalizedStringFromTable(@"Submit", @"QAForumPlugin", nil)];
    
    sheet.actionSheetStyle = UIActionSheetStyleBlackOpaque;
    //[sheet showInView:[self.view window]];

	[sheet showFromRect:self.view.bounds inView:self.view animated:YES];
     */
}


/*
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    //NSLog(@"%@",[questionDic valueForKey:[NSString stringWithFormat:@"%d",buttonIndex]]);
    //select one question to view details
    if (buttonIndex<self.matchingNumbers) {
        [qaforumService oneQuestionWithQuestionid:[[questionDic valueForKey:[NSString stringWithFormat:@"%d",buttonIndex]] intValue] delegate:self];
    }
    else if(buttonIndex==self.matchingNumbers+1){
     //submit action
        NSLog(@"%@",question);
        [qaforumService askQuestionWithQuestion:question delegate:self];
        [question release];
        [sheet release];
        [self.navigationController popViewControllerAnimated:YES];
    }
}
*/

- (void)oneQuestionWithQuestionid:(int)data didReturn:(NSString *)result {
    MyQuestionViewController* viewController = [MyQuestionViewController alloc];
    viewController.data = result;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (void)oneQuestionFailed {
    [self error];
}

- (void)askQuestionWithQuestion:(s_ask *)data didReturn:(int32_t)result {
    NSLog(@"%d",result);
}

- (void)askQuestionFailed {
    [self error];
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
        CGPoint scrollPoint = CGPointMake(0.0, kbSize.height-(scrollView.frame.size.height-activeField.frame.origin.y-activeField.frame.size.height));
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


// Delegate function
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    // We are now showing the UIPickerViewer instead
    if (textField == self.expiryTimeTextView) {
        [self.expiryTimeTextView endEditing:YES];
        NSArray* expiryOptions= [[NSArray alloc] initWithObjects:@"5 mins",@"10 mins",@"30 mins",@"1 hour",@"6 hours",@"12 hours",@"24 hours",@"3 days",@"1 weeks",nil];
        [self showPickerViewer :expiryOptions :NSLocalizedStringFromTable(@"ExpiryTime", @"QAForumPlugin", nil): textField];
        return NO;
    }
    
    else if(textField == self.topicTextView){
        [self.topicTextView endEditing:YES];
        NSArray* expiryOptions= [[NSArray alloc] initWithObjects:NSLocalizedStringFromTable(@"Travel", @"QAForumPlugin", nil),NSLocalizedStringFromTable(@"Study", @"QAForumPlugin", nil),NSLocalizedStringFromTable(@"Living", @"QAForumPlugin", nil),NSLocalizedStringFromTable(@"Other", @"QAForumPlugin", nil), nil];
        [self showPickerViewer :expiryOptions :NSLocalizedStringFromTable(@"Topics", @"QAForumPlugin", nil) :textField];
        return NO;
    }
    else {
        return YES;
    }
}

// Show the UIPickerView
- (void)showPickerViewer:(NSArray *)items :(NSString *)title :(UITextField *)textField{
    ActionStringDoneBlock done = ^(ActionSheetStringPicker *picker, NSInteger selectedIndex, id selectedValue) {
        if ([textField respondsToSelector:@selector(setText:)]) {
            [textField performSelector:@selector(setText:) withObject:selectedValue];
        }
        if (textField == self.topicTextView) {
            topicselectedRow = selectedIndex;
        } else {
            selectedRow = selectedIndex;
        }
    };
    ActionStringCancelBlock cancel = ^(ActionSheetStringPicker *picker) {
        NSLog(@"UIPicker was cancelled.");
    };
    
    [ActionSheetStringPicker showPickerWithTitle:title rows:items initialSelection:selectedRow doneBlock:done cancelBlock:cancel origin:textField];
}


- (NSInteger)pickerView:(UIPickerView *)thePickerView numberOfRowsInComponent:(NSInteger)component {
    return 1;
    //unuseful, for we use customized pickerView.
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    centerIndicator.hidden = true;
    [centerIndicator stopAnimating];
    [bnContinue setEnabled:true];
}
- (void)error {
    [PCUtils showServerErrorAlert];
    centerIndicator.hidden = true;
    [centerIndicator stopAnimating];
    [bnContinue setEnabled:true];
}




@end
