//
//  QAForumViewController.m
//  PocketCampus
//
//  Created by Susheng on 12/5/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "QAForumViewController.h"

#import "PCUtils.h"

#import "IntroViewController.h"

@interface QAForumViewController ()

@end

@implementation QAForumViewController
@synthesize notificationswitch, centerActivityIndicator, centerMessageLabel,bnAsk,bnForum,bnPending,bnRecord,bnSettings,labAsk,labForum,labPending,labRecord,pushController;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        /*
        self.pushController = [PushNotifController sharedInstance];
        [self.pushController addNotificationObserverWithPluginLowerIdentifier:@"qaforum" newNotificationBlock:^(NSString *notifMessage, NSDictionary* notifCompleteDictionary) {
            NSLog(@"%@",notifCompleteDictionary);
            NSString* notificaionid = notifCompleteDictionary[@"notificationid"];
            NSString* notificationMessage = notifCompleteDictionary[@"alert"];
            AcceptViewController* viewController = [AcceptViewController alloc];
            viewController.data = notificationMessage;
            viewController.notificationid = [notificaionid intValue];
            [self.navigationController pushViewController:viewController animated:YES];
        }];
         */
        
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    labAsk.text = NSLocalizedStringFromTable(@"Ask", @"QAForumPlugin", nil);
    labForum.text = NSLocalizedStringFromTable(@"Forum", @"QAForumPlugin", nil);
    labPending.text = NSLocalizedStringFromTable(@"Pending", @"QAForumPlugin", nil);
    labRecord.text = NSLocalizedStringFromTable(@"Record", @"QAForumPlugin", nil);
    bnSettings.title = NSLocalizedStringFromTable(@"Settings", @"QAForumPlugin", nil);
    [self refresh];
}

- (void)waitForData {
    //centerMessageLabel.text = @"Waiting...";
    centerMessageLabel.text = NSLocalizedStringFromTable(@"Waiting", @"QAForumPlugin", nil);
    centerMessageLabel.hidden = NO;
    [centerActivityIndicator startAnimating];
    centerActivityIndicator.hidden = NO;
    [self hideElements];
}

- (void)waitForDataDidReturn {
    centerMessageLabel.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    [self showElements];
    //check whether there is a notification message to display
    if ([QAForumService lastNotif]!=nil) {
        NSDictionary* notifCompleteDictionary = [QAForumService lastNotif];
        NSString* notificaionid = notifCompleteDictionary[@"notificationid"];
        NSDictionary* aps = [notifCompleteDictionary objectForKey:@"aps"];
        NSString* notificationMessage = aps[@"alert"];
        AcceptViewController* viewController = [AcceptViewController alloc];
        viewController.data = notificationMessage;
        viewController.notificationid = [notificaionid intValue];
        [self.navigationController pushViewController:viewController animated:YES];
    }
}

- (void)hideElements {
    labAsk.hidden = YES;
    labForum.hidden = YES;
    labPending.hidden = YES;
    labRecord.hidden = YES;
    bnAsk.hidden = YES;
    bnForum.hidden = YES;
    bnPending.hidden = YES;
    bnRecord.hidden = YES;
    bnSettings.enabled = NO;
    notificationswitch.enabled = NO;
}

- (void)showElements {
    labAsk.hidden = NO;
    labForum.hidden = NO;
    labPending.hidden = NO;
    labRecord.hidden = NO;
    bnAsk.hidden = NO;
    bnForum.hidden = NO;
    bnPending.hidden = NO;
    bnRecord.hidden = NO;
    bnSettings.enabled = YES;
    notificationswitch.enabled = YES;
}

- (void)refresh {
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    s_session* sessionId = [QAForumService lastSessionId];
    if (sessionId == nil) {
        [self waitForData];
        NSLog(@"-> No previously saved sessionId. Requesting credentials...");
        [self addLoginObserver:self operationIdentifier:nil successBlock:nil userCancelledBlock:nil failureBlock:nil];
    } else {
        [self waitForDataDidReturn];
    }
}


- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(VoidBlock)successBlock
      userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    
    @synchronized(self) {
        PCLoginObserver* loginObserver = [[PCLoginObserver alloc] init];
        loginObserver.observer = observer;
        loginObserver.operationIdentifier = identifier;
        loginObserver.successBlock = successBlock;
        loginObserver.userCancelledBlock = userCancelledblock;
        loginObserver.failureBlock = failureBlock;
        [self.loginObservers addObject:loginObserver];
        if(!self.authController) {
            self.authController = [AuthenticationController sharedInstanceToRetain];
            [qaforumService getTequilaTokenForQAforumWithDelegate:self];
        }
    }
}

- (void)getTequilaTokenForQAforumDidReturn:(QATequilaToken *)tequilaToken {
    self.tequilaToken = tequilaToken;
    [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:self delegate:(id)self];
}

- (void)getTequilaTokenForQAforumFailed {
    [PCUtils showServerErrorAlert];
}

- (void)authenticationSucceeded {
    NSLog(@"authen succ");
    [qaforumService getSessionIdWithTequilaToken:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    //TODO delete session in QAForumService
    //call login observers and execute userCancelledBlock
    [[MainController publicController] requestLeavePlugin:@"QAForum"];
}

- (void)invalidToken {
    NSLog(@"%@",@"Invalid Token");
    [self serviceConnectionToServerTimedOut];
}

- (void)getSessionIdWithTequilaToken:(QATequilaToken *)token didReturn:(s_session *)session {
    NSLog(@"%@", session);
    [QAForumService saveSessionId:session];
    
    self.pushController = [PushNotifController sharedInstanceToRetain];
    
    [self.pushController registerDeviceForPushNotificationsWithPluginLowerIdentifier:@"qaforum" reason:@"TODO" success:^{
        NSLog(@"push OK");
    } failure:^(PushNotifDeviceRegistrationError error) {
        NSLog(@"push failed");
    }];
    
    self.navigationItem.rightBarButtonItem.enabled = NO;
    [self waitForDataDidReturn];
    //*****depending on user settings...
    //start the introduction View.
//    UIBarButtonItem *newBackButton = [[UIBarButtonItem alloc] initWithTitle: NSLocalizedStringFromTable(@"Skip", @"QAForumPlugin", nil) style: UIBarButtonItemStyleBordered target: nil action: nil];
//    [[self navigationItem] setBackBarButtonItem: newBackButton];
//    [newBackButton release];
    if([QAForumService lastSessionId].intro == 0)
    {
        IntroViewController* viewController = [IntroViewController alloc];
        [self.navigationController pushViewController:viewController animated:YES];
    }
}

- (void)getSessionIdFailedForTequilaToken:(QATequilaToken *)token {
    [PCUtils showServerErrorAlert];
    [self waitForDataDidReturn];
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {

    [notificationswitch release];
    [centerMessageLabel release];
    [centerActivityIndicator release];
    [bnAsk release];
    [bnForum release];
    [bnRecord release];
    [bnPending release];
    [bnSettings release];
    [labAsk release];
    [labForum release];
    [labRecord release];
    [labPending release];
    [super dealloc];
}
- (void)viewDidUnload {

    [self setNotificationswitch:nil];
    [self setCenterMessageLabel:nil];
    [self setCenterActivityIndicator:nil];
    [self setBnAsk:nil];
    [self setBnForum:nil];
    [self setBnRecord:nil];
    [self setBnPending:nil];
    [self setBnSettings:nil];
    [self setLabAsk:nil];
    [self setLabForum:nil];
    [self setLabRecord:nil];
    [self setLabPending:nil];
    [super viewDidUnload];
}

- (IBAction)askQuestionAction:(UIButton *)sender {
    
    AskQuestionViewController* viewController = [AskQuestionViewController alloc];
    [self.navigationController pushViewController:viewController animated:YES];
}

- (IBAction)forumAction:(UIButton *)sender {
    [qaforumService LatestQuestionsWithUserid:[QAForumService lastSessionId].sessionid delegate:self];
    [self waitForData];
    
}

- (void)LatestQuestionsWithUserid:(NSString *)userid didReturn:(NSString *)result {
    [self waitForDataDidReturn];
    LatestForumViewController* viewController = [LatestForumViewController alloc];
    viewController.data = result;
    [self.navigationController pushViewController:viewController animated:YES];
}

-(void)LatestQuestionsFailed {
    [self waitForDataDidReturn];
    [PCUtils showServerErrorAlert];
}

- (IBAction)myQuestionListAction:(UIButton *)sender {
    [self waitForData];
    [qaforumService myQuestionsWithUserid:[QAForumService lastSessionId].sessionid delegate:self];
}

- (IBAction)PendingAction:(UIButton *)sender {
    [self waitForData];
    [qaforumService PendingNotificationsWithUserid:[QAForumService lastSessionId].sessionid delegate:self];
}

- (void)PendingNotificationsWithUserid:(NSString *)userid didReturn:(NSString *)result {
    [self waitForDataDidReturn];
    PendingViewController* viewController = [PendingViewController alloc];
    viewController.data = result;
    [self.navigationController pushViewController:viewController animated:YES];
}

- (void)PendingNotificationsFailed {
    [self waitForDataDidReturn];
    [PCUtils showServerErrorAlert];
}

- (IBAction)settingAction:(UIBarButtonItem *)sender {
    SettingViewController* viewController = [SettingViewController alloc];
    [self.navigationController pushViewController:viewController animated:YES];
}

- (IBAction)notificationSwitch:(UISwitch *)sender {
    NSString* message;
    if (!sender.isOn) {
        message = NSLocalizedStringFromTable(@"StartNotif", @"QAForumPlugin", nil);
    } else {
        message = NSLocalizedStringFromTable(@"StopNotif", @"QAForumPlugin", nil);
    }
   
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"NotifSetting", @"QAForumPlugin", nil) message:message delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"QAForumPlugin", nil) otherButtonTitles:NSLocalizedStringFromTable(@"OK", @"QAForumPlugin", nil),nil];
    [alert show];
    [alert release];
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        notificationswitch.on = !notificationswitch.isOn;
    }
    else if (buttonIndex == 1) {
        s_session* temp = [QAForumService lastSessionId];
        if (notificationswitch.isOn) {
            temp.accept = 1;
        } else {
            temp.accept = 0;
        }
        [QAForumService saveSessionId:temp];
        [qaforumService updateSettingWithSetting:temp delegate:self];
        
    }
}

- (void)updateSettingFailed {
    [PCUtils showServerErrorAlert];
}

- (void)updateSettingWithSetting:(s_session *)data didReturn:(int32_t)result {
    //nothing to do
}

- (void)myQuestionWithUserid:(NSString *)data didReturn:(NSString *)result {
    [QAForumService saveMyQuestionsList:result];
    [qaforumService myAnswersWithUserid:[QAForumService lastSessionId].sessionid delegate:self];
    
}

- (void)myQuestionFailed {
    [PCUtils showServerErrorAlert];
    [self waitForDataDidReturn];
}

- (void)myAnswerWithUserid:(NSString *)data didReturn:(NSString *)resul{
    [self waitForDataDidReturn];
    MyQuestionListViewController* viewController = [MyQuestionListViewController alloc];
    viewController.data = resul;
    [self.navigationController pushViewController:viewController animated:YES];
    //    myAnswerListViewController* viewController = [myAnswerListViewController alloc];
//    viewController.data = resul;
//    [self.navigationController pushViewController:viewController animated:YES];
}

-(void)myAnswerFailed {
    [PCUtils showServerErrorAlert];
    [self waitForDataDidReturn];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterMsg", @"QAForumPlugin", nil);
    centerMessageLabel.hidden = NO;
    [self hideElements];
    self.authController = nil;
    [QAForumService saveSessionId:nil];
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
    [refreshButton release];
    self.navigationItem.rightBarButtonItem.enabled = YES;
}

@end
