//
//  QAForumController.h
//  PocketCampus
//

#import "PluginController.h"

#import "AuthenticationController.h"

#import "PushNotifController.h"

#import "QAForumService.h"

#import "QAForumViewController.h"

#import "AskQuestionViewController.h"

#import "MyAnswerListViewController.h"

#import "MyQuestionListViewController.h"

#import "AcceptViewController.h"

#import "SettingViewController.h"

#import "AnswerViewController.h"

#import "AnswerListViewController.h"

#import "FeedbackListViewController.h"

#import "FeedbackViewController.h"

#import "QuestionListViewController.h"

#import "QuestionViewController.h"

#import "RequestViewController.h"

#import "ReportViewController.h"

#import "PendingViewController.h"

#import "LatestForumViewController.h"

#import "MatchingViewController.h"

#import "AnswerRecViewController.h"

#import "JSONKit.h"

#import "PCUtils.h"

@interface QAForumController : PluginController<PluginControllerProtocol, UISplitViewControllerDelegate, QAForumServiceDelegate>

@property (nonatomic, strong) PushNotifController* pushNotifController;

@end
