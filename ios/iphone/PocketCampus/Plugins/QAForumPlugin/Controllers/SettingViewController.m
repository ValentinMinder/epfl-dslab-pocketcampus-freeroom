//
//  settingViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/13/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "settingViewController.h"

#import "ActionSheetPicker.h"

@interface SettingViewController ()

@end

@implementation SettingViewController
@synthesize bn_English, bnFrench, bnDeutsch, bnItalian, bnLiving, bnOther, bnStudy, bnTravel, tvIterval,selectedRow,topicselectedRow, labTopic, labLang, labNotif, bnConfirm;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    self.title = NSLocalizedStringFromTable(@"Settings", @"QAForumPlugin", nil);
    s_session* temp = [QAForumService lastSessionId];
    if([temp.language rangeOfString:@"English"].location != NSNotFound) {
        bn_English.selected = true;
    }
    if([temp.language rangeOfString:@"French"].location != NSNotFound) {
        bnFrench.selected = true;
    }
    if([temp.language rangeOfString:@"Deutsch"].location != NSNotFound) {
        bnDeutsch.selected = true;
    }
    if([temp.language rangeOfString:@"Italian"].location != NSNotFound) {
        bnItalian.selected = true;
    }
    
    if([temp.topic rangeOfString:@"Travel"].location != NSNotFound) {
        bnTravel.selected = true;
    }
    if([temp.topic rangeOfString:@"Study"].location != NSNotFound) {
        bnStudy.selected = true;
    }
    if([temp.topic rangeOfString:@"Living"].location != NSNotFound) {
        bnLiving.selected = true;
    }
    if([temp.topic rangeOfString:@"Other"].location != NSNotFound) {
        bnOther.selected = true;
    }
    
    switch([temp resttime]) {
        case 0:
            selectedRow = 0;
            tvIterval.text = NSLocalizedStringFromTable(@"None", @"QAForumPlugin",nil);
            break;
        case 1:
            selectedRow = 1;
            tvIterval.text = NSLocalizedStringFromTable(@"1 min", @"QAForumPlugin",nil);
            break;
        case 2:
            selectedRow = 2;
            tvIterval.text = NSLocalizedStringFromTable(@"2 mins", @"QAForumPlugin",nil);
            break;
        case 5:
            selectedRow = 3;
            tvIterval.text = NSLocalizedStringFromTable(@"5 mins", @"QAForumPlugin",nil);
            break;
        case 10:
            selectedRow = 4;
            tvIterval.text = NSLocalizedStringFromTable(@"10 mins", @"QAForumPlugin",nil);
            break;
        case 30:
            selectedRow = 5;
            tvIterval.text = NSLocalizedStringFromTable(@"30 mins", @"QAForumPlugin",nil);
            break;
        case 60:
            selectedRow = 6;
            tvIterval.text = NSLocalizedStringFromTable(@"1 h", @"QAForumPlugin",nil);
            break;
        case 120:
            selectedRow = 7;
            tvIterval.text = NSLocalizedStringFromTable(@"2 h", @"QAForumPlugin",nil);
            break;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    labLang.text = NSLocalizedStringFromTable(@"PreferredLanguage", @"QAForumPlugin",nil);
    labNotif.text = NSLocalizedStringFromTable(@"NotificationInterval", @"QAForumPlugin",nil);
    labTopic.text = NSLocalizedStringFromTable(@"PreferredTopics", @"QAForumPlugin",nil);
    [bnConfirm setTitle:NSLocalizedStringFromTable(@"Confirm", @"QAForumPlugin",nil) forState:UIControlStateNormal];
    
    [bn_English setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnFrench setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnDeutsch setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnItalian setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnLiving setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnStudy setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnTravel setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    [bnOther setImage:[UIImage imageNamed:@"qaforum_selected.png"] forState:UIControlStateSelected];
    
    [bn_English setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnFrench setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnDeutsch setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnItalian setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnLiving setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnStudy setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnTravel setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    [bnOther setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    
    [bnLiving setTitle:NSLocalizedStringFromTable(@"Living", @"QAForumPlugin",nil) forState:UIControlStateNormal];
    [bnStudy setTitle:NSLocalizedStringFromTable(@"Study", @"QAForumPlugin",nil) forState:UIControlStateNormal];
    [bnOther setTitle:NSLocalizedStringFromTable(@"Other", @"QAForumPlugin",nil) forState:UIControlStateNormal];
    [bnTravel setTitle:NSLocalizedStringFromTable(@"Travel", @"QAForumPlugin",nil) forState:UIControlStateNormal];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [bn_English release];
    [bnFrench release];
    [bnDeutsch release];
    [bnItalian release];
    [bnTravel release];
    [bnStudy release];
    [bnLiving release];
    [bnOther release];
    [tvIterval release];
    [labLang release];
    [labTopic release];
    [labNotif release];
    [bnConfirm release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setBnDeutsch:nil];
    [self setBnItalian:nil];
    [self setBnTravel:nil];
    [self setBnStudy:nil];
    [self setBnLiving:nil];
    [self setBnOther:nil];
    [self setTvIterval:nil];
    [self setLabLang:nil];
    [self setLabTopic:nil];
    [self setLabNotif:nil];
    [self setBnConfirm:nil];
    [super viewDidUnload];
}

- (IBAction)bnEnglishAction:(UIButton *)sender {
    bn_English.selected = !bn_English.selected;
}

- (IBAction)bnFrenchAction:(UIButton *)sender {
    bnFrench.selected = !bnFrench.selected;
}

- (IBAction)bnDeutsch:(UIButton *)sender {
    bnDeutsch.selected = !bnDeutsch.selected;
}

- (IBAction)bnItalianAction:(UIButton *)sender {
    bnItalian.selected = !bnItalian.selected;
}

- (IBAction)bnTravelAction:(UIButton *)sender {
    bnTravel.selected = !bnTravel.selected;
}

- (IBAction)bnStudyAction:(UIButton *)sender {
    bnStudy.selected = !bnStudy.selected;
}

- (IBAction)bnLivingAction:(UIButton *)sender {
    bnLiving.selected = !bnLiving.selected;
}

- (IBAction)bnOtherAction:(UIButton *)sender {
    bnOther.selected = !bnOther.selected;
}

- (IBAction)bnConfirmAction:(UIButton *)sender {
    NSString* language = @"";
    if (bn_English.selected) {
        language = [NSString stringWithFormat:@"%@ %@", language,@"English"];
    }
    if (bnFrench.selected) {
        language = [NSString stringWithFormat:@"%@ %@", language,@"French"];
    }
    if (bnDeutsch.selected) {
        language = [NSString stringWithFormat:@"%@ %@", language,@"Deutsch"];
    }
    if (bnItalian.selected) {
        language = [NSString stringWithFormat:@"%@ %@", language,@"Italian"];
    }
    
    NSString* topics = @"";
    if (bnTravel.selected) {
        topics = [NSString stringWithFormat:@"%@ %@", topics,@"Travel"];
    }
    if (bnStudy.selected) {
        topics = [NSString stringWithFormat:@"%@ %@", topics,@"Study"];
    }
    if (bnLiving.selected) {
        topics = [NSString stringWithFormat:@"%@ %@", topics,@"Living"];
    }
    if (bnOther.selected) {
        topics = [NSString stringWithFormat:@"%@ %@", topics,@"Other"];
    }
    int expritytime;
    
    switch (selectedRow) {
        case 0:
            expritytime=0;
            break;
        case 1:
            expritytime=1;
            break;
        case 2:
            expritytime=2;
            break;
        case 3:
            expritytime=5;
            break;
        case 4:
            expritytime=10;
            break;
        case 5:
            expritytime=30;
            break;
        case 6:
            expritytime=60;
            break;
        case 7:
            expritytime=120;
            break;
        default:
            break;
    }
    s_session* temp = [QAForumService lastSessionId];
    temp.topic = topics;
    temp.language = language;
    temp.resttime = expritytime;
    [QAForumService saveSessionId:temp];
    [qaforumService updateSettingWithSetting:temp delegate:self];
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Settings", @"QAForumPlugin",nil)
                                                    message:NSLocalizedStringFromTable(@"SettingChanged", @"QAForumPlugin",nil)
                                                   delegate:nil
                                          cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin",nil)
                                          otherButtonTitles:nil];
    [alert show];
    [alert release];
}


- (void)updateSettingWithSetting:(s_session*)data didReturn:(int32_t)result {
    NSLog(@"%@",@"Update Setting succeeds");
}
- (void)updateSettingFailed {
    [PCUtils showServerErrorAlert];
}

// Delegate function
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    // We are now showing the UIPickerViewer instead
    if (textField == self.tvIterval) {
        [self.tvIterval endEditing:YES];
        NSArray* expiryOptions= [[NSArray alloc] initWithObjects:@"None",@"1 min",@"2 mins",@"5 mins",@"10 mins",@"30 mins",@"1 hour",@"2 hours",nil];
        [self showPickerViewer :expiryOptions :@"Notification Interval": textField];
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
        if (textField == self.tvIterval) {
            selectedRow = selectedIndex;
        }
    };
    ActionStringCancelBlock cancel = ^(ActionSheetStringPicker *picker) {
    };
    [ActionSheetStringPicker showPickerWithTitle:title rows:items initialSelection:selectedRow doneBlock:done cancelBlock:cancel origin:textField];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}
@end
    