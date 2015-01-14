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

//  Created by Loïc Gardiol on 19.11.14.

#import "CloudPrintPreviewViewController.h"

#import "CloudPrintService.h"

#import "UIImageView+AFNetworking.h"

@interface CloudPrintPreviewViewController ()<CloudPrintServiceDelegate>

@property (nonatomic, weak) IBOutlet UIImageView* imageView;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UIButton* prevPageButton;
@property (nonatomic, weak) IBOutlet UILabel* pageLabel;
@property (nonatomic, weak) IBOutlet UIButton* nextPageButton;

@property (nonatomic, strong) NSTimer* updateImageTimer;

@property (nonatomic, strong) CloudPrintService* cloudPrintService;

@property (nonatomic) NSInteger totalNbPages;
@property (nonatomic) NSInteger currentPageIndex;

@end

@implementation CloudPrintPreviewViewController

#pragma mark - Init

- (instancetype)init {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.gaiScreenName = @"/cloudprint/printpreview";
        self.title = NSLocalizedStringFromTable(@"PrintPreview", @"CloudPrintPlugin", nil);
        self.cloudPrintService = [CloudPrintService sharedInstanceToRetain];
        self.imageView.layer.shadowColor = [UIColor grayColor].CGColor;
        self.imageView.layer.shadowRadius = 4.0;
        self.imageView.layer.shadowOpacity = 0.5;
        self.imageView.layer.shadowOffset = CGSizeMake(0, 0);
        self.preferredContentSize = CGSizeMake(600.0, 600.0);
        [self.prevPageButton setTitle:NSLocalizedStringFromTable(@"PreviousWithArrow", @"CloudPrintPlugin", nil) forState:UIControlStateNormal];
        [self.nextPageButton setTitle:NSLocalizedStringFromTable(@"NextWithArrow", @"CloudPrintPlugin", nil) forState:UIControlStateNormal];
        self.imageView.userInteractionEnabled = YES;
        UISwipeGestureRecognizer* prevGesture = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(prevPageTapped)];
        prevGesture.direction = UISwipeGestureRecognizerDirectionRight;
        [self.imageView addGestureRecognizer:prevGesture];
        UISwipeGestureRecognizer* nextGesture = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(nextPageTapped)];
        nextGesture.direction = UISwipeGestureRecognizerDirectionLeft;
        [self.imageView addGestureRecognizer:nextGesture];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Close", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(closeTapped)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Print", @"CloudPrintPlugin", nil) style:UIBarButtonItemStyleDone target:self action:@selector(printTapped)];
}

#pragma mark - Public

- (void)setPrintDocumentRequest:(PrintDocumentRequest *)printDocumentRequest {
    _printDocumentRequest = printDocumentRequest;
    self.totalNbPages = -1;
    self.currentPageIndex = 0;
    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"PreparingPrintPreviewMightTakeSomeTime", @"CloudPrintPlugin", nil);
    [self.cloudPrintService cancelOperationsForDelegate:self];
    [self.cloudPrintService printPreviewWithRequest:printDocumentRequest delegate:self];
    [self update];
}

#pragma mark - Actions

- (void)closeTapped {
    if (self.closeTappedBlock) {
        [self trackAction:@"Close"];
        self.closeTappedBlock();
    }
}

- (void)printTapped {
    if (self.printTappedBlock) {
        [self trackAction:@"Print"];
        self.printTappedBlock();
    }
}

- (IBAction)prevPageTapped {
    if (self.currentPageIndex > 0) {
        [self trackAction:@"PreviousPage"];
        self.currentPageIndex--;
        [self update];
    }
}

- (IBAction)nextPageTapped {
    if (self.currentPageIndex < self.totalNbPages - 1) {
        [self trackAction:@"NextPage"];
        self.currentPageIndex++;
        [self update];
    }
}

#pragma mark - Private

- (void)update {
    if (self.totalNbPages < 0) {
        self.prevPageButton.hidden = YES;
        self.pageLabel.hidden = YES;
        self.nextPageButton.hidden = YES;
        self.imageView.image = nil;
    } else {
        self.prevPageButton.hidden = NO;
        self.pageLabel.hidden = NO;
        self.nextPageButton.hidden = NO;
        self.prevPageButton.enabled = self.currentPageIndex > 0;
        self.nextPageButton.enabled = self.currentPageIndex < self.totalNbPages - 1;
        self.pageLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PageIndexOutOfTotalWithFormat", @"CloudPrintPlugin", nil), self.currentPageIndex+1, self.totalNbPages];
        
        [self.imageView cancelImageRequestOperation];
        self.imageView.image = nil;
        
        [self.loadingIndicator startAnimating];

        // We use a timer so that if user changes page quickly and multiple times,
        // image download requests do not stack. We "wait" that the user stops on a page
        // to start the download for this page only
        NSTimeInterval interval = self.updateImageTimer ? 0.4 : 0.0;
        [self.updateImageTimer invalidate];
        self.updateImageTimer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(updateImage) userInfo:nil repeats:NO];
    }
    
}

- (void)updateImage {
    NSURLRequest* request = [self.cloudPrintService printPreviewImageRequestForDocumentId:self.printDocumentRequest.documentId pageIndex:self.currentPageIndex];
    if (!request) {
        [self.loadingIndicator stopAnimating];
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
        return;
    }
    __weak __typeof(self) welf = self;
    [self.imageView setImageWithURLRequest:request placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
        welf.updateImageTimer = nil;
        [welf.loadingIndicator stopAnimating];
        welf.centerMessageLabel.text = nil;
        welf.imageView.image = image;
    } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
        welf.updateImageTimer = nil;
        [welf.loadingIndicator stopAnimating];
        switch (response.statusCode) {
            case CloudPrintStatusCode_AUTHENTICATION_ERROR:
            {
                __weak __typeof(self) welf = self;
                [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                    [welf.loadingIndicator startAnimating];
                    [welf updateImage];
                } userCancelled:^{
                    welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoginRequired", @"PocketCampus", nil);
                } failure:^(NSError *error) {
                    if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                        welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil);
                    } else {
                        welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
                    }
                }];
                break;
            }
            default:
                welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
                break;
        }
    }];
}

#pragma mark - CloudPrintService

- (void)printPreviewForRequest:(PrintDocumentRequest *)request didReturn:(PrintPreviewDocumentResponse *)response {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = nil;
    switch (response.statusCode) {
        case CloudPrintStatusCode_OK:
            self.totalNbPages = response.numberOfPages;
            [self update];
            break;
        case CloudPrintStatusCode_AUTHENTICATION_ERROR:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstanceToRetain] addLoginObserver:self success:^{
                welf.printDocumentRequest = welf.printDocumentRequest;
            } userCancelled:^{
                [welf closeTapped]; //cancel
            } failure:^(NSError *error) {
                welf.totalNbPages = -1;
                [welf update];
                if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                    welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil);
                } else {
                    welf.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
                }
            }];
            break;
        }
        default:
            [self printPreviewFailedForRequest:request];
            break;
    }
}

- (void)printPreviewFailedForRequest:(PrintDocumentRequest *)request {
    [self.loadingIndicator stopAnimating];
    self.totalNbPages = -1;
    [self update];
     self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
}

- (void)serviceConnectionToServerFailed {
    [self.loadingIndicator stopAnimating];
    self.totalNbPages = -1;
    [self update];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.updateImageTimer invalidate];
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.cloudPrintService cancelOperationsForDelegate:self];
    [self.imageView cancelImageRequestOperation];
}

@end
