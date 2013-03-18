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

#import "UIActionSheet+Additions.h"

#import "MoodleSplashDetailViewController.h"

static NSTimeInterval kHideNavbarSeconds = 5.0;

@interface MoodleResourceViewController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleResource* moodleResource;
@property (nonatomic, strong) UIDocumentInteractionController* docInteractionController;
@property (nonatomic, strong) UIActionSheet* deleteActionSheet;
@property (nonatomic) CGFloat navbarOriginalAlpha;
@property (nonatomic, strong) NSTimer* hideNavbarTimer;
@property (nonatomic) BOOL isShowingActionMenu;

@end

@implementation MoodleResourceViewController

- (id)initWithMoodleResource:(MoodleResource*)moodleResource {
    self = [super initWithNibName:@"MoodleResourceView" bundle:nil];
    if (self) {
        self.moodleResource = moodleResource;
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
    
    self.webView.hidden = YES;
    
    self.progressView.progress = 0.0;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil);
    
    self.webView.scalesPageToFit = YES; //otherwise, pinch-to-zoom is disabled
    
    if ([PCUtils isIdiomPad]) {
        self.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
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

- (void)viewWillAppear:(BOOL)animated {
    self.navigationController.navigationBar.translucent = YES;
}

- (void)viewWillDisappear:(BOOL)animated {
    self.navigationController.navigationBar.translucent = NO;
    [self.moodleService cancelDownloadOfMoodleResourceForDelegate:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    return UIInterfaceOrientationIsLandscape(interfaceOrientation) || (UIInterfaceOrientationPortrait);
}


- (void)loadDownloadedMoodleResourceInWebView {
    self.webView.hidden = NO;
    NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleResource:self.moodleResource]];
    [self.webView loadRequest:[NSURLRequest requestWithURL:localFileURL]];
    
    UITapGestureRecognizer* tapGestureReco = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleNavbarVisibility)];
    UITapGestureRecognizer* doubleTapGestureReco = [[UITapGestureRecognizer alloc] initWithTarget:nil action:NULL];
    doubleTapGestureReco.numberOfTapsRequired = 2;
    
    [tapGestureReco requireGestureRecognizerToFail:doubleTapGestureReco]; //must add also double-tap because otherwise, single-tap is triggered immediately, even when double-tapping for zooming into PDF.
    
    [self.view addGestureRecognizer:tapGestureReco];
    [self.view addGestureRecognizer:doubleTapGestureReco];
    
    
    //Like in GoodReader, faster because reacts immediatly (instead of having to wait to check if second tap is coming becore triggering
    UITapGestureRecognizer* tripleFingerTapRecp = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleNavbarVisibility)];
    tripleFingerTapRecp.numberOfTouchesRequired = 3;
    [self.view addGestureRecognizer:tripleFingerTapRecp];
    
    [self rescheduleHideNavbarTimer];
}

- (void)rescheduleHideNavbarTimer {
    [self.hideNavbarTimer invalidate];
    self.hideNavbarTimer = [NSTimer scheduledTimerWithTimeInterval:kHideNavbarSeconds target:self selector:@selector(hideNavbar) userInfo:nil repeats:NO];
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
        [[MoodleController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            if (self.splitViewController) {
                MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], splashViewController];
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

#pragma mark - Gestures actions

- (void)hideNavbar {
    if (self.navigationController.navigationBar.alpha == 0.0) {
        return; //already hidden
    }
    self.navbarOriginalAlpha = self.navigationController.navigationBar.alpha;
    [UIView animateWithDuration:0.3 animations:^{
        self.navigationController.navigationBar.alpha = 0.0;
    }];
}

- (void)showNavbar {
    if (self.navigationController.navigationBar.alpha > 0.0) {
        return; //already visible
    }
    [UIView animateWithDuration:0.1 animations:^{
        self.navigationController.navigationBar.alpha = self.navbarOriginalAlpha;
    }];
    [self rescheduleHideNavbarTimer];
}

- (void)toggleNavbarVisibility {
    if (self.navigationController.navigationBar.alpha > 0.0) {
        [self hideNavbar];
    } else {
        [self showNavbar];
    }
}

#pragma mark - Button actions

- (void)deleteButtonPressed {
    if (!self.deleteActionSheet) {
        self.deleteActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteFileFromCacheQuestion", @"MoodlePlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    }
    [self.docInteractionController dismissMenuAnimated:NO];
    [self.deleteActionSheet toggleFromBarButtonItem:[self deleteButton] animated:YES];
}

- (void)actionButtonPressed {
    if (self.isShowingActionMenu) {
        [self.docInteractionController dismissMenuAnimated:YES];
        return;
    }
    [self.deleteActionSheet dismissWithClickedButtonIndex:self.deleteActionSheet.cancelButtonIndex animated:NO];
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
}

- (void)downloadFailedForMoodleResource:(MoodleResource *)moodleResource responseStatusCode:(int)statusCode {
    if (statusCode == 404) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        [self serviceConnectionToServerTimedOut];
    } else if (statusCode == 303 || statusCode == 407) {
        //mans bad cookie
        self.progressView.progress = 0.0;
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
        if (![PCUtils isIdiomPad]) { /*iPhone*/
            [self.navigationController popViewControllerAnimated:YES];
        }
    }
    self.deleteActionSheet = nil;
}

- (void)actionSheetCancel:(UIActionSheet *)actionSheet {
    if (actionSheet == self.deleteActionSheet) {
        self.deleteActionSheet = nil;
    }
}

#pragma mark - UIDocumentInteractionControllerDelegate

/* deprecated - required for iOS<=5 devices */
- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller canPerformAction:(SEL)action
{
    
    if (action == @selector (print:) && [UIPrintInteractionController canPrintURL:controller.URL]) {
        return YES;
    }
    return NO;
}

/* deprecated - required for iOS<=5 devices */
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

- (void)documentInteractionControllerWillPresentOptionsMenu:(UIDocumentInteractionController *)controller {
    self.isShowingActionMenu = YES;
}

- (void)documentInteractionControllerDidDismissOptionsMenu:(UIDocumentInteractionController *)controller {
    self.isShowingActionMenu = NO;
}

#pragma mark - UIWebViewDelegate


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
    [self.hideNavbarTimer invalidate];
    [self.moodleService cancelOperationsForDelegate:self];
    [self.webView stopLoading];
    self.webView.delegate = nil; //docs says so
    self.docInteractionController.delegate = nil;
    self.deleteActionSheet.delegate = nil;
}

@end
