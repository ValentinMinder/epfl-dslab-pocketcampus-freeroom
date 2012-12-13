//
//  DocumentViewController2.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MoodleResourceViewController.h"

#import "GANTracker.h"

#import "PCUtils.h"

@interface MoodleResourceViewController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleResource* moodleResource;
@property (nonatomic, strong) UIDocumentInteractionController* docInteractionController;
@property (nonatomic, strong) UIActionSheet* deleteActionSheet;
@property (nonatomic, copy) VoidBlock downloadedBlock;
@property (nonatomic, copy) VoidBlock deletedBlock;

@end

@implementation MoodleResourceViewController

- (id)initWithMoodleResource:(MoodleResource*)moodleResource downloadedBlock:(VoidBlock)downloadedBlock deletedBlock:(VoidBlock)deletedBlock {
    self = [super initWithNibName:@"MoodleResourceView" bundle:nil];
    if (self) {
        self.moodleResource = moodleResource;
        self.downloadedBlock = downloadedBlock;
        self.deletedBlock = deletedBlock;
        if ([PCUtils isIdiomPad]) {
            self.title = moodleResource.iName; //enough space to display title if iPad
        }
        self.moodleService = [MoodleService sharedInstanceToRetain];
        if ([self.moodleService isMoodleResourceDownloaded:moodleResource]) {
            NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleResource:moodleResource]];
            self.docInteractionController = [UIDocumentInteractionController interactionControllerWithURL:localFileURL];
            self.docInteractionController.delegate = self;
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document" withError:NULL];
    
    self.webView.scalesPageToFit = YES; //otherwise, pinching zoom is disabled
    
    if ([PCUtils isIdiomPad]) {
        self.navigationItem.leftBarButtonItem = [self toggleMasterViewBarButtonItem];
    }
    
    NSMutableArray* rightButtons = [NSMutableArray arrayWithCapacity:2];
    
    UIBarButtonItem* actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    actionButton.enabled = NO;
    [rightButtons addObject:actionButton];
    
    UIBarButtonItem* deleteButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(deleteButtonPressed)];
    deleteButton.enabled = NO;
    [rightButtons addObject:deleteButton];

    self.navigationItem.rightBarButtonItems = rightButtons;
    
    if (self.docInteractionController) { //means resource already downloaded, see init
        self.centerMessageLabel.hidden = YES;
        self.progressView.hidden = YES;
        [self deleteButton].enabled = YES;
        [self actionButton].enabled = YES;
        [self loadDownloadedMoodleResourceInWebView];
    } else {
        [self deleteButton].enabled = NO;
        [self actionButton].enabled = NO;
        [self startMoodleResourceDownload];
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)loadDownloadedMoodleResourceInWebView {
    [self.loadingIndicator startAnimating];
    NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleResource:self.moodleResource]];
    [self.webView loadRequest:[NSURLRequest requestWithURL:localFileURL]];
}

- (void)startMoodleResourceDownload {
    
    VoidBlock successBlock = ^{
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MoodlePlugin", nil);
        self.centerMessageLabel.hidden = NO;
        self.progressView.hidden = NO;
        [self.moodleService downloadMoodleResource:self.moodleResource progressView:self.progressView delegate:self];
    };
    
    if ([self.moodleService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MoodleController sharedInstance] addLoginObserver:self operationIdentifier:nil successBlock:successBlock userCancelledBlock:^{
            if ([PCUtils isIdiomPad]) {
                //TODO
            } else {
                [self.navigationController popViewControllerAnimated:YES];
            }
        } failureBlock:^{
            [self serviceConnectionToServerTimedOut];
        }];
    }
}

#pragma - buttons access

- (UIBarButtonItem*)actionButton {
    if (self.navigationItem.rightBarButtonItems.count < 1) {
        return nil;
    }
    return [self.navigationItem.rightBarButtonItems objectAtIndex:0];
}

- (UIBarButtonItem*)deleteButton {
    if (self.navigationItem.rightBarButtonItems.count < 2) {
        return nil;
    }
    return [self.navigationItem.rightBarButtonItems objectAtIndex:1];
}

#pragma mark - actions management

- (UIBarButtonItem*)toggleMasterViewBarButtonItem {
    UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 25.0, 25.0)];
    [button setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
    button.adjustsImageWhenHighlighted = NO;
    button.showsTouchWhenHighlighted = YES;
    [button addTarget:self action:@selector(toggleMasterVideoControllerHidden:) forControlEvents:UIControlEventTouchUpInside];
    return [[UIBarButtonItem alloc] initWithCustomView:button];
}

- (void)toggleMasterVideoControllerHidden:(UIButton*)sender {
    if ([self.navigationController.splitViewController isKindOfClass:[PluginSplitViewController class]]) { //should always be the case
        PluginSplitViewController* pluginSplitViewController = (PluginSplitViewController*)self.navigationController.splitViewController;
        if (pluginSplitViewController.masterViewControllerHidden) {
            [sender setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
            [pluginSplitViewController setMasterViewControllerHidden:NO animated:YES];
        } else {
            [sender setImage:[UIImage imageNamed:@"ArrowShow"] forState:UIControlStateNormal];
            [pluginSplitViewController setMasterViewControllerHidden:YES animated:YES];
        }
    }
}

#pragma mark - Button actions

- (void)deleteButtonPressed {
    self.deleteActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteFileFromCacheQuestion", @"MoodlePlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    [self.deleteActionSheet showFromBarButtonItem:[self deleteButton] animated:YES];
}

- (void)actionButtonPressed {
    BOOL couldShowMenu = [self.docInteractionController presentOptionsMenuFromBarButtonItem:[self actionButton] animated:YES];
    if (!couldShowMenu) {
        UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Sorry", @"MoodlePlugin", nil) message:NSLocalizedStringFromTable(@"NoActionForThisFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
    }
}

#pragma mark - MoodleServiceDelegate

- (void)downloadOfMoodleResource:(MoodleResource *)moodleResource didFinish:(NSURL *)localFileURL {
    self.centerMessageLabel.hidden = YES;
    self.progressView.hidden = YES;
    self.docInteractionController = [UIDocumentInteractionController interactionControllerWithURL:localFileURL];
    self.docInteractionController.delegate = self;
    [self deleteButton].enabled = YES;
    [self actionButton].enabled = YES;
    [self loadDownloadedMoodleResourceInWebView];
    self.downloadedBlock(); //run the block that was given in constructor when resource is downloaded
}

- (void)downloadFailedForMoodleResource:(MoodleResource *)moodleResource responseStatusCode:(int)statusCode {
    if (statusCode == 404) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        [self serviceConnectionToServerTimedOut];
    } else if (statusCode == 303 || statusCode == 407) {
        //mans bad cookie
        [self.moodleService deleteSession];
        [self startMoodleResourceDownload];
    } else { //other unkown error
        [self serviceConnectionToServerTimedOut];
    }
}

- (void)serviceConnectionToServerTimedOut {
    [self.loadingIndicator stopAnimating];
    self.webView.hidden = YES;
    self.progressView.hidden = YES;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ErrorWhileDownloadingFile", @"MoodlePlugin", nil);
    self.centerMessageLabel.hidden = NO;
}


#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) { //delete button, starts from the top, cancel button not included
        if (![self.moodleService deleteDownloadedMoodleResource:self.moodleResource]) {
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            return;
        }
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document/delete" withError:NULL];
        self.deletedBlock(); //run the block that was given in constructor when resource is deleted
    }
}

- (void)actionSheetCancel:(UIActionSheet *)actionSheet {
    if (actionSheet == self.deleteActionSheet) {
        self.deleteActionSheet = nil;
    }
}

#pragma mark - UIDocumentInteractionControllerDelegate


- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller canPerformAction:(SEL)action
{
    
    if (action == @selector (print:) && [UIPrintInteractionController canPrintURL:controller.URL]) {
        return YES;
    }
    return NO;
}

- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller performAction:(SEL)action
{
    
    bool __block success = NO;
    if (action == @selector(print:)) {
        UIPrintInteractionController *printController = [UIPrintInteractionController sharedPrintController];
        printController.printingItem = controller.URL;
        [printController presentAnimated:YES completionHandler:^(UIPrintInteractionController *printInteractionController, BOOL completed, NSError *error){
            if (completed) {
                success = YES;
            }
        }];
    }
    return success;
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)webView_ {
    [self.loadingIndicator stopAnimating];
    self.webView.hidden = NO;
}

- (void)webView:(UIWebView *)webView_ didFailLoadWithError:(NSError *)error {
    self.webView.hidden = YES;
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"FileCouldNotBeDisplayedTryOpenIn", @"MoodlePlugin", nil);
    self.centerMessageLabel.hidden = NO;
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleResource:self.moodleResource]];
    if ([request.URL.path isEqualToString:localFileURL.path]) {
        return YES;
    }
    [[UIApplication sharedApplication] openURL:request.URL]; //open in Safari so that current webview does not leave document
    return NO;
}

#pragma mark - dealloc

- (void)dealloc
{
    [self.moodleService cancelOperationsForDelegate:self];
    [self.webView stopLoading];
    self.docInteractionController.delegate = nil;
    self.deleteActionSheet.delegate = nil;
}

@end
