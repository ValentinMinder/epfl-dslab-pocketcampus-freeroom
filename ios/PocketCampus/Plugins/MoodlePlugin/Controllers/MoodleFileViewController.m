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

#import "MoodleFileViewController.h"

#import "UIActionSheet+Additions.h"

#import "MoodleSplashDetailViewController.h"

#import "MoodleService.h"

#import "PluginSplitViewController.h"

#import "MoodleController.h"

#import "NSTimer+Blocks.h"

static NSTimeInterval kHideNavbarSeconds = 5.0;

@interface MoodleFileViewController ()<UIGestureRecognizerDelegate, UIWebViewDelegate, UIDocumentInteractionControllerDelegate, UIActionSheetDelegate, MoodleServiceDelegate>

@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) MoodleFile2* moodleFile;
@property (nonatomic, strong) UIActionSheet* deleteActionSheet;
@property (nonatomic, strong) UIDocumentInteractionController* docController;
@property (nonatomic, strong) UITapGestureRecognizer* tapGestureReco;
@property (nonatomic) CGFloat navbarOriginalAlpha;
@property (nonatomic, strong) NSTimer* hideNavbarTimer;
@property (nonatomic) BOOL isShowingActionMenu;

@property (nonatomic) CGSize lastKnownContentSize;

@property (nonatomic, weak) UISplitViewController* splitViewControllerPtr; //used to keep a pointer to splitViewController that is not nillified before being able to remove observers on it

@end

@implementation MoodleFileViewController

#pragma mark - Init

- (id)initWithMoodleFile:(MoodleFile2*)moodleFile {
    self = [super initWithNibName:@"MoodleFileView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/moodle/course/document";
        self.moodleFile = moodleFile;
        self.title = moodleFile.name; //enough space to display title if iPad
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.lastKnownContentSize = CGSizeZero;
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
    
    BOOL isFavorite = [self.moodleService isFavoriteMoodleItem:self.moodleFile];
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
    
    if ([self.moodleService isMoodleFileDownloaded:self.moodleFile]) {
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
    [super viewWillAppear:animated];
    [self trackScreen];
    self.webView.scrollView.contentInset = [PCUtils edgeInsetsForViewController:self];
    self.webView.scrollView.scrollIndicatorInsets = [PCUtils edgeInsetsForViewController:self];
    [self.webView.scrollView addObserver:self forKeyPath:NSStringFromSelector(@selector(contentSize)) options:0 context:nil];
    
    self.splitViewControllerPtr = self.splitViewController;
    [self.splitViewController addObserver:self forKeyPath:NSStringFromSelector(@selector(isMasterViewControllerHidden)) options:0 context:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFavoriteButton) name:kMoodleFavoritesMoodleItemsUpdatedNotification object:self.moodleService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillResignActive) name:UIApplicationWillResignActiveNotification object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self saveScrollViewState];
    [self showNavbar];
    [self.moodleService cancelDownloadOfMoodleFilesForDelegate:self];
    [self removeSplitViewControllerObserver];
    [self removeScrollViewContentSizeObserver];
    if (!self.isDisappearingBecauseOtherPushed) {
        [self.webView loadHTMLString:@"" baseURL:nil]; //prevent major memory leak, see http://stackoverflow.com/a/16514274/1423774
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    [self.webView reload]; //should release a bit of memory
}

#pragma mark - Observers

- (void)refreshFavoriteButton {
    BOOL isFavorite = [self.moodleService isFavoriteMoodleItem:self.moodleFile];
    UIImage* favoriteImage = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIImage* favoriteImageLandscape = [PCValues imageForFavoriteNavBarButtonLandscapePhone:YES glow:isFavorite];
    UIBarButtonItem* favoriteButton = [[UIBarButtonItem alloc] initWithImage:favoriteImage landscapeImagePhone:favoriteImageLandscape style:UIBarButtonItemStylePlain target:self action:@selector(favoriteButtonPressed)];
    favoriteButton.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveDocumentFromFavorites", @"MoodlePlugin", nil) : NSLocalizedStringFromTable(@"AddDocumentToFavorites", @"MoodlePlugin", nil);
    NSUInteger index = [self.navigationItem.rightBarButtonItems indexOfObject:[self favoriteButton]];
    NSMutableArray* items = [self.navigationItem.rightBarButtonItems mutableCopy];
    [items replaceObjectAtIndex:index withObject:favoriteButton];
    self.navigationItem.rightBarButtonItems = items;
}

- (void)appWillResignActive {
    [self saveScrollViewState];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (object == self.splitViewController && [keyPath isEqualToString:NSStringFromSelector(@selector(isMasterViewControllerHidden))]) {
            if (![(PluginSplitViewController*)(self.splitViewController) isMasterViewControllerHidden]) {
                [self showNavbar];
            }
            [self rescheduleHideNavbarTimer];
        } else if (object == self.webView.scrollView && [keyPath isEqualToString:NSStringFromSelector(@selector(contentSize))]) {
            CGSize currentContentSize = self.webView.scrollView.contentSize;
            if (!CGSizeEqualToSize(currentContentSize, self.lastKnownContentSize) && !CGSizeEqualToSize(currentContentSize, self.webView.frame.size)) {
                self.lastKnownContentSize = currentContentSize;
                [self restoreScrollViewSateIfExists];
            }
        }
    });
}

- (void)removeSplitViewControllerObserver {
    @try {
        [self.splitViewControllerPtr removeObserver:self forKeyPath:NSStringFromSelector(@selector(isMasterViewControllerHidden))];
    }
    @catch (NSException *exception) {}
}

- (void)removeScrollViewContentSizeObserver {
    @try {
        [self.webView.scrollView removeObserver:self forKeyPath:NSStringFromSelector(@selector(contentSize))];
    }
    @catch (NSException *exception) {}
}

#pragma mark - Navbar visibility

- (void)rescheduleHideNavbarTimer {
    [self.hideNavbarTimer invalidate];
    MoodleFileViewController* weakSelf __weak = self;
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
                                       
- (void)actionButtonPressed {
    if (self.deleteActionSheet.isVisible) {
        [self.deleteActionSheet dismissWithClickedButtonIndex:self.deleteActionSheet.cancelButtonIndex animated:NO];
    }
    if (self.docController) {
        [self.docController dismissMenuAnimated:YES];
        self.docController = nil;
    } else {
        [self trackAction:PCGAITrackerActionActionButtonPressed contentInfo:self.moodleFile.name];
        NSURL* resourceLocalURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleFile:self.moodleFile]];
        self.docController = [UIDocumentInteractionController interactionControllerWithURL:resourceLocalURL];
        self.docController.delegate = self;
        [self.docController presentOptionsMenuFromBarButtonItem:[self actionButton] animated:YES];
    }
}

- (void)favoriteButtonPressed {
    if ([self.moodleService isFavoriteMoodleItem:self.moodleFile]) {
        [self trackAction:PCGAITrackerActionUnmarkFavorite contentInfo:self.moodleFile.name];
        [self.moodleService removeFavoriteMoodleItem:self.moodleFile];
    } else {
        [self trackAction:PCGAITrackerActionMarkFavorite contentInfo:self.moodleFile.name];
        [self.moodleService addFavoriteMoodleItem:self.moodleFile];
    }
}

- (void)deleteButtonPressed {
    if (!self.deleteActionSheet) {
        self.deleteActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteFileFromCacheQuestion", @"MoodlePlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    }
    if (self.docController) {
        [self.docController dismissMenuAnimated:NO];
    }
    [self.deleteActionSheet toggleFromBarButtonItem:[self deleteButton] animated:YES];
}



#pragma mark - Moodle Resource loading

- (void)startMoodleResourceDownload {

    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MoodlePlugin", nil);
    self.centerMessageLabel.hidden = NO;
    self.progressView.hidden = NO;
    [self.moodleService downloadMoodleFile:self.moodleFile progressView:self.progressView delegate:self];
}

- (void)loadDownloadedMoodleResourceInWebView {
    self.webView.hidden = NO;
    NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleFile:self.moodleFile]];
    [self.webView loadRequest:[NSURLRequest requestWithURL:localFileURL]];
    
    UITapGestureRecognizer* tapGestureReco = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleNavbarVisibility)];
    tapGestureReco.delegate = self;
    self.tapGestureReco = tapGestureReco;

    UITapGestureRecognizer* doubleTapGestureReco = [[UITapGestureRecognizer alloc] initWithTarget:nil action:nil];
    doubleTapGestureReco.delegate = self;
    doubleTapGestureReco.numberOfTapsRequired = 2;
    
    [tapGestureReco requireGestureRecognizerToFail:doubleTapGestureReco]; //must add also double-tap because otherwise, single-tap is triggered immediately, even when double-tapping for zooming into PDF.
    
    //Like in GoodReader, faster because reacts immediatly (instead of having to wait to check if second tap is coming becore triggering
    UITapGestureRecognizer* tripleFingerTapReco = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleNavbarVisibility)];
    tripleFingerTapReco.delegate = self;
    tripleFingerTapReco.numberOfTouchesRequired = 3;
    
    [self.view addGestureRecognizer:tapGestureReco];
    [self.view addGestureRecognizer:doubleTapGestureReco];
    [self.view addGestureRecognizer:tripleFingerTapReco];
    
    [self rescheduleHideNavbarTimer];
    
    __weak __typeof(self) welf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [welf removeScrollViewContentSizeObserver];
    });
}


#pragma mark - MoodleServiceDelegate

- (void)downloadOfMoodleFile:(MoodleFile2 *)moodleFile didFinish:(NSURL *)localFileURL {
    self.centerMessageLabel.hidden = YES;
    self.progressView.hidden = YES;
    [self deleteButton].enabled = YES;
    [self actionButton].enabled = YES;
    [self loadDownloadedMoodleResourceInWebView];
}

- (void)downloadFailedForMoodleFile:(MoodleFile2 *)moodleFile responseStatusCode:(int)statusCode {
    if (statusCode == 404) {
        UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [errorAlert show];
        [self serviceConnectionToServerFailed];
    } else if (statusCode == 303 || statusCode == 407) {
        //mans not logged in
        self.progressView.progress = 0.0;
        __weak __typeof(self) weakSelf = self;
        [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
            [weakSelf startMoodleResourceDownload];
        } userCancelled:^{
            if (weakSelf.splitViewController) {
                MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                weakSelf.splitViewController.viewControllers = @[weakSelf.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:splashViewController]];
            } else {
                [weakSelf.navigationController popViewControllerAnimated:YES];
            }
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

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    [self removeScrollViewContentSizeObserver]; //do not restore scroll view reading position if user satarts manipulating the document
    if (gestureRecognizer == self.tapGestureReco && [otherGestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) {
        return NO;
    }
    if (gestureRecognizer == self.tapGestureReco && [otherGestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]]) {
        return NO;
    }
    if (gestureRecognizer.view == self.view && [otherGestureRecognizer.view isOrSubviewOfView:self.webView]) {
        return YES;
    }
    return NO;
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) { //delete button, starts from the top, cancel button not included
        [self trackAction:PCGAITrackerActionDelete contentInfo:self.moodleFile.name];
        [self removeSplitViewControllerObserver];
        if (![self.moodleService deleteDownloadedMoodleFile:self.moodleFile]) {
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
    NSURL* localFileURL = [NSURL fileURLWithPath:[self.moodleService localPathForMoodleFile:self.moodleFile]];
    if ([request.URL.path isEqualToString:localFileURL.path]) {
        return YES;
    }
    [[UIApplication sharedApplication] openURL:request.URL]; //open in Safari so that current webview does not leave document
    return NO;
}

#pragma mark - Private utils

static NSString* const kScrollViewStateKey = @"ScrollViewState";
static NSString* const kFrameKey = @"Frame";
static NSString* const kContentSizeKey = @"ContentSize";
static NSString* const kContentOffsetKey = @"ContentOffset";
static NSString* const kZoomScaleKey = @"ZoomScale";

- (void)saveScrollViewState {
    if (!self.moodleFile.url) {
        return;
    }
    NSDictionary* state = @{kFrameKey:NSStringFromCGRect(self.webView.scrollView.frame),
                            kContentSizeKey:NSStringFromCGSize(self.webView.scrollView.contentSize),
                            kContentOffsetKey:NSStringFromCGPoint(self.webView.scrollView.contentOffset),
                            kZoomScaleKey:[NSNumber numberWithFloat:self.webView.scrollView.zoomScale]};
    
    NSMutableDictionary* resourceDic = [[MoodleFile2 defaultsDictionaryForMoodleItem:self.moodleFile] mutableCopy];
    resourceDic[kScrollViewStateKey] = state;
    [MoodleFile2 setDefaultsDictionary:resourceDic forMoodleItem:self.moodleFile];
}

- (void)restoreScrollViewSateIfExists {
    if (![[PCPersistenceManager userDefaultsForPluginName:@"moodle"] boolForKey:kMoodleSaveDocsPositionGeneralSettingBoolKey]) {
        return;
    }
    NSDictionary* resourceDic = [MoodleFile2 defaultsDictionaryForMoodleItem:self.moodleFile];
    NSDictionary* state = resourceDic[kScrollViewStateKey];
    if (!state) {
        return;
    }
    
    CGRect orginalFrame = CGRectFromString(state[kFrameKey]);
    CGSize originalContentSize = CGSizeFromString(state[kContentSizeKey]);
    CGPoint originalContentOffset = CGPointFromString(state[kContentOffsetKey]);
    CGFloat originalZoomScale = [state[kZoomScaleKey] floatValue];
    if (originalContentSize.width == 0.0 || originalContentSize.height == 0.0) {
        return;
    }
    if (originalZoomScale == 0.0) {
        return;
    }
    
    CGSize currentContentSize = self.webView.scrollView.contentSize;
    
    CGFloat newContentOffsetX = originalContentOffset.x * (currentContentSize.width / originalContentSize.width);
    CGFloat newContentOffsetY = originalContentOffset.y * (currentContentSize.height / originalContentSize.height);
    
    CGPoint newOffset = CGPointMake(newContentOffsetX, newContentOffsetY);
    
    CGFloat newZoomScale = originalZoomScale * (orginalFrame.size.width / self.webView.scrollView.frame.size.width);
    
    self.webView.scrollView.contentOffset = newOffset;
    self.webView.scrollView.zoomScale = newZoomScale;
    
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self removeSplitViewControllerObserver];
    [self removeScrollViewContentSizeObserver];
    [self.hideNavbarTimer invalidate];
    [self.moodleService cancelOperationsForDelegate:self];
    self.webView.delegate = nil; //docs says so
    self.deleteActionSheet.delegate = nil;
    self.docController.delegate = nil;
}

@end
