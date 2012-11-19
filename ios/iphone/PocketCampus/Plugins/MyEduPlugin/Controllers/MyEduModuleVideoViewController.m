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

@interface MyEduModuleVideoViewController ()

@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MPMoviePlayerController* moviePlayerController;
@property (nonatomic, strong) NSURLConnection* videoHEADRequestConnection;
@property (nonatomic, strong) UIPopoverController* downloadPopoverController;
@property (nonatomic, strong) UIActionSheet* deleteVideoActionSheet;

@end

@implementation MyEduModuleVideoViewController

- (id)initWithMyEduModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleVideoView" bundle:nil];
    if (self) {
        _module = module;
        _myEduService = [MyEduService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    //self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"LightGrayTextureBackground"]];
    
    NSString* filePath = [self.myEduService localPathOfVideoForModule:self.module nilIfNoFile:YES];
    
    if (filePath) { //video in cache
        [self initVideoPlayerWithURL:[NSURL fileURLWithPath:filePath]];
        [self setDeleteButtonAnimated:NO];
    } else {
        if (self.module.iVideoDownloadURL) { //will use native movie player
            [self.loadingIndicator startAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingVideo", @"MyEduPlugin", nil);
            [self startVideoHEADRequest];
        } else { //will use webview to embed video
            if (!self.module.iVideoID || [self.module.iVideoID isEqualToString:@""]) {
                self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoVideoInModule", @"MyEduPlugin", nil);
            } else {
                [self.loadingIndicator startAnimating];
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
                } else {
                    [self.loadingIndicator stopAnimating];
                    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"VideoNotSupported", @"MyEduPlugin", nil);
                }
            }
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/* use to follow redirect if needed */
- (void)startVideoHEADRequest {
    NSMutableURLRequest *headRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.module.iVideoDownloadURL] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0];
    headRequest.HTTPMethod = @"HEAD";
    self.videoHEADRequestConnection = [[NSURLConnection alloc] initWithRequest:headRequest delegate:self];
}

- (void)initVideoPlayerWithURL:(NSURL*)url {
    self.moviePlayerController = [[MPMoviePlayerController alloc] initWithContentURL:url];
    self.moviePlayerController.view.frame = CGRectMake(0, 0, self.view.frame.size.width-60.0, (self.view.frame.size.width-60.0)/(16.0/9.0));
    self.moviePlayerController.view.center = self.view.center;
    self.moviePlayerController.view.autoresizingMask =
    UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleHeight;
    self.moviePlayerController.controlStyle = MPMovieControlStyleEmbedded;
    self.moviePlayerController.shouldAutoplay = NO;
    /*self.moviePlayerController.view.layer.masksToBounds = NO;
     self.moviePlayerController.view.layer.shadowOffset = CGSizeMake(0.0, 2.00);
     self.moviePlayerController.view.layer.shadowRadius = 10.0;
     self.moviePlayerController.view.layer.shadowOpacity = 0.2;
     self.moviePlayerController.view.layer.shadowColor = [UIColor whiteColor].CGColor;*/
    [self.view addSubview:self.moviePlayerController.view];
    
    /* registering notifications */
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(resizeVideoForNaturalSize) name:MPMovieMediaTypesAvailableNotification object:self.moviePlayerController];
    [self.view addObserver:self forKeyPath:@"frame" options:0 context:nil];
    [self.moviePlayerController prepareToPlay];

}

- (void)setDownloadButtonAnimated:(BOOL)animated {
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"Download"] style:UIBarButtonItemStyleBordered target:self action:@selector(downloadButtonPressed)] animated:animated];
}

- (void)setDeleteButtonAnimated:(BOOL)animated {
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(deleteButtonPressed)] animated:animated];
}

#pragma mark - user actions

- (void)downloadButtonPressed {
    if (!self.downloadPopoverController) {
        MyEduModuleVideoDownloadViewController* downloadController = [[MyEduModuleVideoDownloadViewController alloc] initWithModule:self.module];
        self.downloadPopoverController = [[UIPopoverController alloc] initWithContentViewController:downloadController];
        
        MyEduModuleVideoViewController* controller __weak = self;
        [self.myEduService addDownloadObserver:self forVideoOfModule:self.module startDownload:YES finishBlock:^(NSURL *fileLocalURL) {
            [controller.myEduService removeDownloadObserver:controller forVideoModule:controller.module];
            [controller.downloadPopoverController dismissPopoverAnimated:YES];
            controller.downloadPopoverController = nil;
            [controller setDeleteButtonAnimated:YES];
        } progressBlock:^(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio) {
            //nothing
        } cancelledBlock:^{
            [controller.myEduService removeDownloadObserver:controller forVideoModule:controller.module];
            [controller.downloadPopoverController dismissPopoverAnimated:YES];
            controller.downloadPopoverController = nil;
        } failureBlock:^(int statusCode) {
            [controller.myEduService removeDownloadObserver:controller forVideoModule:controller.module];
            [controller.downloadPopoverController dismissPopoverAnimated:YES];
            controller.downloadPopoverController = nil;
            //TODO alert
        }];
    }
    
    [self.downloadPopoverController presentPopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    [self.downloadPopoverController setPopoverContentSize:CGSizeMake(366.0, 96.0) animated:NO];
}
         
- (void)deleteButtonPressed {
    if (!self.deleteVideoActionSheet) {
        self.deleteVideoActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"DeleteDownloadedVideoExplanation", @"MyEduPlugin", nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:NSLocalizedStringFromTable(@"Delete", @"PocketCampus", nil) otherButtonTitles:nil];
    }
    [self.deleteVideoActionSheet showFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
}

#pragma mark NSURLConnectionDelegate

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = YES;
    [self initVideoPlayerWithURL:response.URL];
    [self setDownloadButtonAnimated:YES];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.webView.hidden = YES;
    self.videoHEADRequestConnection = nil; //releasing request
}

- (void)connectionDidFinishDownloading:(NSURLConnection *)connection destinationURL:(NSURL *)destinationURL {
    self.videoHEADRequestConnection = nil;
}

#pragma mark MPMoviePlayerController notifications and KVO

- (void)resizeVideoForNaturalSize {
    if (self.moviePlayerController.naturalSize.width == 0.0 || self.moviePlayerController.naturalSize.height == 0.0) { //natural size not available yet
        return;
    }
    CGFloat ratio = self.moviePlayerController.naturalSize.width/self.moviePlayerController.naturalSize.height;
    CGFloat width = self.view.frame.size.width-60.0;
    CGFloat height = width/ratio;
    //NSLog(@"%f, %f", self.moviePlayerController.naturalSize.width, self.moviePlayerController.naturalSize.height);
    self.moviePlayerController.view.bounds = CGRectMake(0, 0, width, height);
    self.moviePlayerController.view.center = self.view.center;
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
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.deleteVideoActionSheet && buttonIndex == 0) { //delete done
        [self setDownloadButtonAnimated:YES];
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
    [self.videoHEADRequestConnection cancel];
    [self.myEduService removeDownloadObserver:self forVideoModule:self.module];
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
