//
//  MyEduModuleVideoViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleVideoViewController.h"

#import "PCValues.h"

#import "MyEduModuleVideoDownloadViewController.h"

#import <MediaPlayer/MediaPlayer.h>

#import "ObjectArchiver.h"

#import "PCUtils.h"

#import "UIActionSheet+Additions.h"

#import "UIPopoverController+Additions.h"

#import "GANTracker.h"

@interface MyEduModuleVideoViewController ()

@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MPMoviePlayerController* moviePlayerController;
@property (nonatomic, strong) UIWebView* webView;
@property (nonatomic, strong) ASIHTTPRequest* videoHEADRequest;
@property (nonatomic, strong) UIPopoverController* downloadPopoverController;
@property (nonatomic, strong) UIActionSheet* deleteVideoActionSheet;
@property (nonatomic, strong) NSTimer* downloadingButtonBlinkTimer;
@property (nonatomic) NSTimeInterval playbackTimeAtLoad;
@property (nonatomic, strong) NSTimer* saveLastPlaybackTimeTimer;

@end

static NSTimeInterval kSaveLastPlaybackTimePeriod = 2.0; //seconds

@implementation MyEduModuleVideoViewController

- (id)initWithMyEduModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleVideoView" bundle:nil];
    if (self) {
        self.module = module;
        self.myEduService = [MyEduService sharedInstanceToRetain];
        self.playbackTimeAtLoad = [self.myEduService lastPlaybackTimeForVideoForModule:module];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/myedu/sections/modules/video" withError:NULL];
	// Do any additional setup after loading the view.
    //self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"LightGrayTextureBackground"]];
    
    
    NSString* filePath = [MyEduService localPathOfVideoForModule:self.module nilIfNoFile:YES];
    
    if (filePath) { //video in cache
        [self initVideoPlayerWithURL:[NSURL fileURLWithPath:filePath]];
        [self setDeleteButtonAnimated:NO];
    } else {
        if (self.module.iVideoDownloadURL) { //will use native movie player
            if ([self.myEduService videoOfModuleIsDownloading:self.module]) {
                self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingVideo", @"MyEduPlugin", nil);
                self.centerMessageLabel.hidden = NO;
            } else {                
                [self.loadingIndicator startAnimating];
                self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingVideo", @"MyEduPlugin", nil);
                self.centerMessageLabel.hidden = NO;
                [self startVideoHEADRequest];
            }
        } else { //will use webview to embed video
            if (!self.module.iVideoID || [self.module.iVideoID isEqualToString:@""]) {
                self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoVideoInModule", @"MyEduPlugin", nil);
                self.centerMessageLabel.hidden = NO;
            } else {
                [self.loadingIndicator startAnimating];
                self.webView = [[UIWebView alloc] initWithFrame:CGRectMake(1, 1, 450, 338)];
                self.webView.delegate = self;
                [self.view addSubview:self.webView];
                self.webView.center = self.view.center;
                self.webView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleLeftMargin;
                self.webView.hidden = YES;
                self.webView.layer.masksToBounds = NO;
                self.webView.layer.shadowOffset = CGSizeMake(0.0, 2.00);
                self.webView.layer.shadowRadius = 10.0;
                self.webView.layer.shadowOpacity = 0.2;
                self.webView.layer.shadowColor = [UIColor whiteColor].CGColor;
                self.webView.allowsInlineMediaPlayback = YES;
                self.webView.scrollView.bounces = NO;
                NSString* htmlString = [MyEduService videoHTMLCodeForMyEduModule:self.module videoWidth:self.webView.frame.size.width-16.0 videoHeight:self.webView.frame.size.height-16.0];
                if (htmlString) {
                    [self.webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@"http://myedu.epfl.ch/"]];
                    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingVideo", @"MyEduPlugin", nil);
                    self.centerMessageLabel.hidden = NO;
                } else {
                    [self.loadingIndicator stopAnimating];
                    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"VideoNotSupported", @"MyEduPlugin", nil);
                    self.centerMessageLabel.hidden = NO;
                }
            }
        }
    }
    
    [self initDownloadObserver];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self.downloadingButtonBlinkTimer invalidate]; //prevent retaining self from NSTimer
    self.downloadingButtonBlinkTimer = nil;
}

/* use to follow redirect if needed */
- (void)startVideoHEADRequest {
    ASIHTTPRequest* headRequest = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:self.module.iVideoDownloadURL]];
    headRequest.timeOutSeconds = 45.0;
    headRequest.requestMethod = @"HEAD";
    headRequest.delegate = self;
    self.videoHEADRequest = headRequest;
    [headRequest startAsynchronous];
}

- (void)initVideoPlayerWithURL:(NSURL*)url {
    if (self.moviePlayerController) { //prevent init twice
        return;
    }
    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingVideo", @"MyEduPlugin", nil);
    self.centerMessageLabel.hidden = NO;
    
    self.moviePlayerController = [[MPMoviePlayerController alloc] initWithContentURL:url];
    self.moviePlayerController.view.frame = CGRectMake(0, 0, self.view.frame.size.width-40.0, (self.view.frame.size.width-40.0)/(16.0/10.0));
    self.moviePlayerController.view.center = self.view.center;
    self.moviePlayerController.view.autoresizingMask =
    UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleHeight;
    self.moviePlayerController.controlStyle = MPMovieControlStyleEmbedded;
    self.moviePlayerController.shouldAutoplay = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(resizeVideoForNaturalSize) name:MPMovieMediaTypesAvailableNotification object:self.moviePlayerController];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playerLoadStateDidChange) name:MPMoviePlayerLoadStateDidChangeNotification object:self.moviePlayerController];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playerPlaybackStateDidChange) name:MPMoviePlayerPlaybackStateDidChangeNotification object:self.moviePlayerController];
    [self.view addObserver:self forKeyPath:@"frame" options:0 context:nil];
    [self.moviePlayerController prepareToPlay];

}

- (void)destroyVideoPlayer {
    if (!self.moviePlayerController) {
        return;
    }
    [self.saveLastPlaybackTimeTimer invalidate];
    [self.moviePlayerController pause];
    [self.moviePlayerController stop];
    self.moviePlayerController.initialPlaybackTime = -1.0; //prevent flickering bug
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:nil object:self.moviePlayerController];
        [self.view removeObserver:self forKeyPath:@"frame"];
    }
    @catch (NSException *exception) {
        //Might only happen if removeObserver called while no registered observer. Should never happen.
        //In that case, just ignore it.
    }
    self.moviePlayerController = nil;
}

- (void)replaceMoviePlayerURL:(NSURL*)url keepPlaybackTime:(BOOL)keepPlaybackTime {
    [self.loadingIndicator startAnimating];
    NSTimeInterval currentTime = floor(self.moviePlayerController.currentPlaybackTime);
    [self.myEduService saveLastPlaybackTime:currentTime forVideoOfModule:self.module];
    self.playbackRateButton.hidden = YES;
    [self.moviePlayerController.view removeFromSuperview];
    [self destroyVideoPlayer];
    
    [self initVideoPlayerWithURL:url];
    
    if (keepPlaybackTime) { //player current time will be set when it is loaded, in playerLoadStateDidChange
        self.playbackTimeAtLoad = currentTime;
    } else {
        self.playbackTimeAtLoad = 0.0;
    }
}

- (void)initDownloadObserver {
     MyEduModuleVideoViewController* controller __weak = self;
    
    [self.myEduService addDownloadObserver:self forVideoOfModule:self.module startDownload:NO startBlock:^{
        [controller setDownloadingButtonAnimated:NO];
    }  finishBlock:^(NSURL *fileLocalURL) {
        [controller setDeleteButtonAnimated:YES];
        [controller.downloadPopoverController dismissPopoverAnimated:YES];
        controller.downloadPopoverController = nil;
        if (!controller.moviePlayerController) { //had not loaded layer with distant URL because download was in progress
            [controller initVideoPlayerWithURL:fileLocalURL]; //download cancelled => should now use distant URL
        } else {
            [controller replaceMoviePlayerURL:fileLocalURL keepPlaybackTime:YES];
        }
    } progressBlock:^(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio) {
        if (!controller.downloadingButtonBlinkTimer) {
            [controller setDownloadingButtonAnimated:NO];
        }
    }
    cancelledBlock:^{
        [controller setDownloadButtonAnimated:YES];
        [controller.downloadPopoverController dismissPopoverAnimated:YES];
        controller.downloadPopoverController = nil;
        if (!controller.moviePlayerController) { //had not loaded layer with distant URL because download was in progress
            [controller initVideoPlayerWithURL:[NSURL URLWithString:controller.module.iVideoDownloadURL]]; //download cancelled => should now use distant URL
        }
    } failureBlock:^(int statusCode) {
        [controller setDownloadButtonAnimated:YES];
        [controller.downloadPopoverController dismissPopoverAnimated:YES];
        controller.downloadPopoverController = nil;
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ErrorOccuredWhileDownloadingVideo", @"MyEduPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        if (!controller.moviePlayerController) { //had not loaded layer with distant URL because download was in progress
            [controller initVideoPlayerWithURL:[NSURL URLWithString:controller.module.iVideoDownloadURL]]; //download cancelled => should now use distant URL
        }
    } deletedBlock:^{
        if (controller.deleteVideoActionSheet) { //delete done
            [controller setDownloadButtonAnimated:YES];
            controller.deleteVideoActionSheet = nil;
        }
        [controller replaceMoviePlayerURL:[NSURL URLWithString:controller.module.iVideoDownloadURL] keepPlaybackTime:YES];
    }];
}

- (void)setDownloadButtonAnimated:(BOOL)animated {
    [self.downloadingButtonBlinkTimer invalidate];
    self.downloadingButtonBlinkTimer = nil;
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"Download"] style:UIBarButtonItemStyleBordered target:self action:@selector(downloadButtonPressed)] animated:animated];
}

- (void)setDownloadingButtonAnimated:(BOOL)animated {
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"Download"] style:UIBarButtonItemStyleDone target:self action:@selector(downloadButtonPressed)] animated:animated];
    [self.downloadingButtonBlinkTimer invalidate];
    self.downloadingButtonBlinkTimer = [NSTimer scheduledTimerWithTimeInterval:0.8 target:self selector:@selector(toggleDownloadingButtonStyle) userInfo:nil repeats:YES];
}

- (void)toggleDownloadingButtonStyle {
    if (self.navigationItem.rightBarButtonItem.style == UIBarButtonItemStyleBordered) {
        self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStyleDone;
    } else {
        self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStyleBordered;
    }
}

- (void)setDeleteButtonAnimated:(BOOL)animated {
    [self.downloadingButtonBlinkTimer invalidate];
    self.downloadingButtonBlinkTimer = nil;
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(deleteButtonPressed)] animated:animated];
}

#pragma mark - user actions

- (void)downloadButtonPressed {
    if (!self.downloadPopoverController) {
        MyEduModuleVideoDownloadViewController* downloadController = [[MyEduModuleVideoDownloadViewController alloc] initWithModule:self.module];
        self.downloadPopoverController = [[UIPopoverController alloc] initWithContentViewController:downloadController];
        [self.downloadPopoverController setPopoverContentSize:CGSizeMake(366.0, 96.0) animated:NO];
    }
    
    [self.downloadPopoverController togglePopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    [self.myEduService downloadVideoOfModule:self.module]; //will not do anything if downloading already
}
         
- (void)deleteButtonPressed {
    if (!self.deleteVideoActionSheet) {
        self.deleteVideoActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteDownloadedVideoExplanation", @"MyEduPlugin", nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    }
    [self.deleteVideoActionSheet toggleFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
}

- (IBAction)playbackRateButtonPressed {
    double curr = (double)self.moviePlayerController.currentPlaybackRate;
    if ([PCUtils double:curr isEqualToDouble:1.0 epsilon:0.1]) {
        self.moviePlayerController.currentPlaybackRate = 1.2;
        [self.playbackRateButton setImage:[UIImage imageNamed:@"MyEduMovieSpeed1_2X"] forState:UIControlStateNormal];
    } else if ([PCUtils double:curr isEqualToDouble:1.2 epsilon:0.1]) {
        self.moviePlayerController.currentPlaybackRate = 1.5;
        [self.playbackRateButton setImage:[UIImage imageNamed:@"MyEduMovieSpeed1_5X"] forState:UIControlStateNormal];
    } else if ([PCUtils double:curr isEqualToDouble:1.5 epsilon:0.1]) {
        self.moviePlayerController.currentPlaybackRate = 0.5;
        [self.playbackRateButton setImage:[UIImage imageNamed:@"MyEduMovieSpeed0_5X"] forState:UIControlStateNormal];
    } else if ([PCUtils double:curr isEqualToDouble:0.5 epsilon:0.1]) {
        self.moviePlayerController.currentPlaybackRate = 1.0;
        [self.playbackRateButton setImage:[UIImage imageNamed:@"MyEduMovieSpeed1X"] forState:UIControlStateNormal];
    } else if ([PCUtils double:curr isEqualToDouble:0.0 epsilon:0.1]) {
        //movie is paused, do not change anything
    } else {
        //unsupported, should not happen
    }
}

#pragma mark - NSURLConnectionDelegate

- (void)requestRedirected:(ASIHTTPRequest *)request {
    [self initVideoPlayerWithURL:request.url];
    [self setDownloadButtonAnimated:YES];
    /* stop request, no need to download the file, we just wanted the redirection URL */
    [self.videoHEADRequest cancel];
    self.videoHEADRequest.delegate = nil;
    self.videoHEADRequest = nil;
}

- (void)requestFailed:(ASIHTTPRequest *)request {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.webView.hidden = YES;
    self.videoHEADRequest.delegate = nil;
    self.videoHEADRequest = nil;
}

#pragma mark - MPMoviePlayerController notifications and KVO

- (void)resizeVideoForNaturalSize {
    if (self.moviePlayerController.naturalSize.width == 0.0 || self.moviePlayerController.naturalSize.height == 0.0) { //natural size not available yet
        return;
    }
    CGFloat ratio = self.moviePlayerController.naturalSize.width/self.moviePlayerController.naturalSize.height;
    CGFloat width = self.view.frame.size.width-40.0;
    CGFloat height = width/ratio;
    self.moviePlayerController.view.bounds = CGRectMake(0, 0, width, height);
    self.moviePlayerController.view.center = self.view.center;
    
    self.playbackRateButton.frame = CGRectMake((int)(self.view.center.x-(self.playbackRateButton.frame.size.width/2.0)), (int)(self.moviePlayerController.view.frame.origin.y+height+15.0), self.playbackRateButton.frame.size.width, self.playbackRateButton.frame.size.height); //int cast is so that frame is integer values. Otherwise blurry on non-retina device.
}

- (void)playerLoadStateDidChange {
    if (!self.moviePlayerController.view.superview && self.moviePlayerController.loadState != MPMovieLoadStateUnknown) {
        [self.loadingIndicator stopAnimating];
        self.centerMessageLabel.hidden = YES;
        [self.view addSubview:self.moviePlayerController.view];
        [self resizeVideoForNaturalSize];
        self.playbackRateButton.hidden = NO;
        self.moviePlayerController.currentPlaybackTime = self.playbackTimeAtLoad;
    }
}

- (void)playerPlaybackStateDidChange {
    if (self.moviePlayerController.playbackState == MPMoviePlaybackStatePlaying) {
        [self saveLastPlaybackTime];
        if (!self.saveLastPlaybackTimeTimer) {
            self.saveLastPlaybackTimeTimer = [NSTimer scheduledTimerWithTimeInterval:kSaveLastPlaybackTimePeriod target:self selector:@selector(saveLastPlaybackTime) userInfo:nil repeats:YES];
        }
    } else {
        [self.saveLastPlaybackTimeTimer invalidate];
    }
}

- (void)saveLastPlaybackTime {
    [self.myEduService saveLastPlaybackTime:floor(self.moviePlayerController.currentPlaybackTime) forVideoOfModule:self.module];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    if (object == self.view) {
        [self resizeVideoForNaturalSize];
    }
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.deleteVideoActionSheet && buttonIndex == 0) { //delete pressed
        [self.myEduService removeDownloadedVideoOfModule:self.module];
        self.deleteVideoActionSheet = nil;
    }
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    self.webView.alpha = 0.0;
    self.webView.hidden = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.webView.alpha = 1.0;
        self.centerMessageLabel.alpha = 0.0;
        self.loadingIndicator.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self.loadingIndicator stopAnimating];
        self.loadingIndicator.alpha = 1.0;
        self.centerMessageLabel.hidden = YES;
        self.centerMessageLabel.alpha = 1.0;
    }];
}

#pragma mark - dealloc

- (void)dealloc
{
    [self.myEduService removeDownloadObserver:self forVideoModule:self.module];
    [self.videoHEADRequest cancel];
    self.videoHEADRequest.delegate = nil;
    [self destroyVideoPlayer];
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
