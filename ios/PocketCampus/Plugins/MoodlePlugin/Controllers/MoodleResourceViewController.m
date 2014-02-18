/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */




//  Created by Loïc Gardiol on 04.12.12.


#import "MoodleResourceViewController.h"

#import "UIActionSheet+Additions.h"

#import "MoodleSplashDetailViewController.h"

#import "MoodleService.h"

#import "PluginSplitViewController.h"

#import "MoodleController.h"

#import "NSTimer+Blocks.h"

static NSTimeInterval kHideNavbarSeconds = 5.0;

@interface MoodleResourceViewController ()<UIWebViewDelegate, UIDocumentInteractionControllerDelegate, UIActionSheetDelegate, MoodleServiceDelegate>

@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleResource* moodleResource;
@property (nonatomic, strong) UIActionSheet* deleteActionSheet;
@property (nonatomic, strong) UIDocumentInteractionController* docController;
@property (nonatomic) CGFloat navbarOriginalAlpha;
@property (nonatomic, strong) NSTimer* hideNavbarTimer;
@property (nonatomic) BOOL isShowingActionMenu;

@property (nonatomic, weak) UISplitViewController* splitViewControllerPtr; //used to keep a pointer to splitViewController that is not nillified before being able to remove observers on it

@end

@implementation MoodleResourceViewController

#pragma mark - Init

- (id)initWithMoodleResource:(MoodleResource*)moodleResource {
    self = [super initWithNibName:@"MoodleResourceView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/moodle/course/document";
        self.moodleResource = moodleResource;
        self.title = moodleResource.iName; //enough space to display title if iPad
        self.moodleService = [MoodleService sharedInstanceToRetain];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
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
    
    BOOL isFavorite = [self.moodleService isFavoriteMoodleResource:self.moodleResource];
    UIImage* favoriteImage = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIImage* favoriteImageLandscape = [PCValues imageForFavoriteNavBarButtonLandscapePhone:YES glow:isFavorite];
    
    UIBarButtonItem* favoriteButton = [[UIBarButtonItem alloc] initWithImage:favoriteImage landscapeImagePhone:favoriteImageLandscape style:UIBarButtonItemStylePlain target:self action:@selector(favoriteButtonPressed)];
    favoriteButton.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveDocumentFromFavorites", @"MoodlePlugin", nil) : NSLocalizedStringFromTable(@"AddDocumentToFavorites", @"MoodlePlugin", nil);
    [rightButtons addObject:favoriteButton];
    
    UIBarButtonItem* deleteButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(deleteButtonPressed)];
    deleteButton.enabled = NO;
    deleteButton.accessibilityHint = NSLocalizedStringFromTable(@"DeleteDocumentFromLocalStorage", @"MoodlePlugin", nil);
    [rightButtons addObject:deleteButton];

    self.navigationItem.rightBarButtonItems = rightButtons;
    
    if ([self.moodleService isMoodleResourceDownloaded:self.moodleResource]) {
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
    self.splitViewControllerPtr = self.splitViewController;
    [self.splitViewController addObserver:self forKeyPath:NSStringFromSelector(@selector(isMasterViewControllerHidden)) options:0 context:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFavoriteButton) name:kMoodleFavoritesMoodleResourcesUpdatedNotification object:self.moodleService];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    self.webView.scrollView.contentInset = [PCUtils edgeInsetsForViewController:self];
    self.webView.scrollView.scrollIndicatorInsets = [PCUtils edgeInsetsForViewController:self];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self showNavbar];
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

- (BOOL)prefersStatusBarHidden {
    return self.navigationController.navigationBarHidden;
}

- (UIStatusBarAnimation)preferredStatusBarUpdateAnimation {
    return UIStatusBarAnimationSlide;
}

#pragma mark - Observers

- (void)refreshFavoriteButton {
    BOOL isFavorite = [self.moodleService isFavoriteMoodleResource:self.moodleResource];
    UIImage* favoriteImage = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIImage* favoriteImageLandscape = [PCValues imageForFavoriteNavBarButtonLandscapePhone:YES glow:isFavorite];
    UIBarButtonItem* favoriteButton = [[UIBarButtonItem alloc] initWithImage:favoriteImage landscapeImagePhone:favoriteImageLandscape style:UIBarButtonItemStylePlain target:self action:@selector(favoriteButtonPressed)];
    favoriteButton.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveDocumentFromFavorites", @"MoodlePlugin", nil) : NSLocalizedStringFromTable(@"AddDocumentToFavorites", @"MoodlePlugin", nil);
    NSUInteger index = [self.navigationItem.rightBarButtonItems indexOfObject:[self favoriteButton]];
    NSMutableArray* items = [self.navigationItem.rightBarButtonItems mutableCopy];
    [items replaceObjectAtIndex:index withObject:favoriteButton];
    self.navigationItem.rightBarButtonItems = items;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.splitViewController && [keyPath isEqualToString:NSStringFromSelector(@selector(isMasterViewControllerHidden))]) {
        if (![(PluginSplitViewController*)(self.splitViewController) isMasterViewControllerHidden]) {
            [self showNavbar];
        }
        [self rescheduleHideNavbarTimer];
    }
}

- (void)removeSplitViewControllerObserver {
    @try {
        [self.splitViewControllerPtr removeObserver:self forKeyPath:NSStringFromSelector(@selector(isMasterViewControllerHidden))];
    }
    @catch (NSException *exception) {}
}

#pragma mark - Navbar visibility

- (void)rescheduleHideNavbarTimer {
    [self.hideNavbarTimer invalidate];
    MoodleResourceViewController* weakSelf __weak = self;
    self.hideNavbarTimer = [NSTimer scheduledTimerWithTimeInterval:kHideNavbarSeconds block:^{
        [weakSelf hideNavbar];
    } repeats:YES];
}

- (void)hideNavbar {
    if (self.navigationController.navigationBarHidden) {
        return;
    }
    if (self.deleteActionSheet.isVisible || self.docController) {
        return;
    }
    if ([PCUtils isIdiomPad] && ![(PluginSplitViewController*)(self.splitViewController) isMasterViewControllerHidden]) {
        return; //on iPad only hide nav bar when in full screen mode (master hidden)
    }
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    [self setNeedsStatusBarAppearanceUpdate];
    self.webView.scrollView.contentInset = UIEdgeInsetsZero;
    self.webView.scrollView.scrollIndicatorInsets = UIEdgeInsetsZero;
}

- (void)showNavbar {
    if (!self.navigationController.navigationBarHidden) {
        return;
    }
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    [self.navigationController setNeedsStatusBarAppearanceUpdate];
    [self.navigationController.navigationBar layoutSubviews]; //workaround for API bug: otherwise, bar button items landscape image is not used, even in landscape
    self.webView.scrollView.contentInset = [PCUtils edgeInsetsForViewController:self];
    self.webView.scrollView.scrollIndicatorInsets = [PCUtils edgeInsetsForViewController:self];
    [self rescheduleHideNavbarTimer];
}

- (void)toggleNavbarVisibility {
    [self rescheduleHideNavbarTimer];
    if (self.navigationController.isNavigationBarHidden) {
        [self showNavbar];
    } else {
        [self hideNavbar];
    }
}


#pragma mark - Buttons access

- (UIBarButtonItem*)actionButton {
    if (self.navigationItem.rightBarButtonItems.count < 1) {
        return nil;
    }
    return self.navigationItem.rightBarButtonItems[0];
}

- (UIBarButtonItem*)favoriteButton {
    if (self.navigationItem.rightBarButtonItems.count < 2) {
        return nil;
    }
    return self.navigationItem.rightBarButtonItems[1];
}

- (UIBarButtonItem*)deleteButton {
    if (self.navigationItem.rightBarButtonItems.count < 3) {
        return nil;
    }
    return self.navigationItem.rightBarButtonItems[2];
}

#pragma mark - Buttons actions

- (void)deleteButtonPressed {
    if (!self.deleteActionSheet) {
        self.deleteActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteFileFromCacheQuestion", @"MoodlePlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    }
    if (self.docController) {
        [self.docController dismissMenuAnimated:NO];
    }
    [self.deleteActionSheet toggleFromBarButtonItem:[self deleteButton] animated:YES];
}

- (void)favoriteButtonPressed {
    if ([self.moodleService isFavoriteMoodleResource:self.moodleResource]) {
        [self trackAction:PCGAITrackerActionUnmarkFavorite];
        [self.moodleService removeFavoriteMoodleResource:self.moodleResource];
    } else {
        [self trackAction:PCGAITrackerActionMarkFavorite];
        [self.moodleService addFavoriteMoodleResource:self.moodleResource];;
    }
}
                                       
- (void)actionButtonPressed {
    if (self.deleteActionSheet.isVisible) {
        [self.deleteActionSheet dismissWithClickedButtonIndex:self.deleteActionSheet.cancelButtonIndex animated:NO];
    }
    if (self.docController) {
        [self.docController dismissMenuAnimated:YES];
        self.docController = nil;
    } else {
        [self trackAction:PCGAITrackerActionActionButtonPressed];
        NSURL* resourceLocalURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleResource:self.moodleResource]];
        self.docController = [UIDocumentInteractionController interactionControllerWithURL:resourceLocalURL];
        self.docController.delegate = self;
        [self.docController presentOptionsMenuFromBarButtonItem:[self actionButton] animated:YES];
    }
}

#pragma mark - Moodle Resource loading

- (void)startMoodleResourceDownload {
    
    /*VoidBlock successBlock = ^{
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MoodlePlugin", nil);
        self.centerMessageLabel.hidden = NO;
        self.progressView.hidden = NO;
        [self.moodleService downloadMoodleResource:self.moodleResource progressView:self.progressView delegate:self];
    };
    
    if ([self.moodleService lastSession]) {
        successBlock();
    } else {
        CLSNSLog(@"-> No saved session, loggin in...");
        [[MoodleController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            if (self.splitViewController) {
                MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:splashViewController]];
            } else {
                [self.navigationController popViewControllerAnimated:YES];
            }
        } failureBlock:^{
            [self serviceConnectionToServerFailed];
        }];
    }*/
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


#pragma mark - MoodleServiceDelegate

- (void)downloadOfMoodleResource:(MoodleResource *)moodleResource didFinish:(NSURL *)localFileURL {
    self.centerMessageLabel.hidden = YES;
    self.progressView.hidden = YES;
    [self deleteButton].enabled = YES;
    [self actionButton].enabled = YES;
    [self loadDownloadedMoodleResourceInWebView];
}

- (void)downloadFailedForMoodleResource:(MoodleResource *)moodleResource responseStatusCode:(int)statusCode {
    if (statusCode == 404) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        [self serviceConnectionToServerFailed];
    } else if (statusCode == 303 || statusCode == 407) {
        //mans bad cookie
        self.progressView.progress = 0.0;
        __weak __typeof(self) weakSelf = self;
        [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
            [weakSelf startMoodleResourceDownload];
        } userCancelled:^{
            [weakSelf serviceConnectionToServerFailed];
        } failure:^{
            [weakSelf serviceConnectionToServerFailed];
        }];
        
    } else { //other unkown error
        [self serviceConnectionToServerFailed];
    }
}

- (void)serviceConnectionToServerFailed {
    self.webView.hidden = YES;
    self.progressView.hidden = YES;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ErrorWhileDownloadingFile", @"MoodlePlugin", nil);
    self.centerMessageLabel.hidden = NO;
}


#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) { //delete button, starts from the top, cancel button not included
        [self trackAction:PCGAITrackerActionDelete];
        [self removeSplitViewControllerObserver];
        if (![self.moodleService deleteDownloadedMoodleResource:self.moodleResource]) {
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            return;
        }
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

- (void)documentInteractionControllerDidDismissOptionsMenu:(UIDocumentInteractionController *)controller {
    self.docController = nil;
}

#pragma mark - UIWebViewDelegate

- (void)webView:(UIWebView *)webView_ didFailLoadWithError:(NSError *)error {
    self.webView.hidden = YES;
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

#pragma mark - Dealloc

- (void)dealloc
{
    [self removeSplitViewControllerObserver];
    [self.hideNavbarTimer invalidate];
    [self.moodleService cancelOperationsForDelegate:self];
    [self.webView stopLoading];
    self.webView.delegate = nil; //docs says so
    self.deleteActionSheet.delegate = nil;
    self.docController.delegate = nil;
}

@end
