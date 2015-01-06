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

//  Created by Loïc Gardiol on 08.09.14.

#import "CloudPrintStatusViewController.h"

@interface CloudPrintStatusViewController ()

@property (nonatomic, weak) IBOutlet UILabel* documentNameLabel;
@property (nonatomic, weak) IBOutlet UILabel* label;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;
@property (nonatomic, weak) IBOutlet UIButton* tryAgainButton;

@end

@implementation CloudPrintStatusViewController

#pragma mark - Init

- (instancetype)init {
    self = [self initWithNibName:@"CloudPrintStatusView" bundle:nil];
    if (self) {
        self.title = @"EPFL Print";
        self.gaiScreenName = @"/cloudprint/status";
        self.documentName = nil;
        self.statusMessage = CloudPrintStatusMessageLoading;
        self.progressView.progress = 0.0;
        self.progress = [NSProgress progressWithTotalUnitCount:1];
        self.userCancelledBlock = nil; //hide back button
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    [self.tryAgainButton setTitle:NSLocalizedStringFromTable(@"TryAgain", @"CloudPrintPlugin", nil) forState:UIControlStateNormal];
    self.documentName = self.documentName;
    self.statusMessage = self.statusMessage;
    [self updateProgress];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

#pragma mark - Actions

- (void)cancelTapped {
    if (self.userCancelledBlock) {
        [self trackAction:@"Cancel"];
        self.userCancelledBlock();
    }
}

- (IBAction)tryAgainTapped {
    if (self.showTryAgainButtonWithTappedBlock) {
        self.showTryAgainButtonWithTappedBlock();
    }
}

#pragma mark - Public

- (void)setDocumentName:(NSString *)documentName {
    _documentName = documentName;
    self.documentNameLabel.text = documentName;
}

- (void)setStatusMessage:(CloudPrintStatusMessage)statusMessage {
    _statusMessage = statusMessage;
    switch (statusMessage) {
        case CloudPrintStatusMessageLoading:
            self.label.text = NSLocalizedStringFromTable(@"Loading", @"CloudPrintPlugin", nil);
            break;
        case CloudPrintStatusMessageDownloadingFile:
            self.label.text = NSLocalizedStringFromTable(@"DownloadingFile", @"CloudPrintPlugin", nil);
            break;
        case CloudPrintStatusMessageUploadingFile:
            self.label.text = NSLocalizedStringFromTable(@"UploadingFile", @"CloudPrintPlugin", nil);
            break;
        case CloudPrintStatusMessageSendingToPrinter:
            self.label.text = NSLocalizedStringFromTable(@"SendingToPrinter", @"CloudPrintPlugin", nil);
            break;
        case CloudPrintStatusMessageSuccess:
            self.label.text = NSLocalizedStringFromTable(@"Success", @"CloudPrintPlugin", nil);
            break;
        case CloudPrintStatusMessageError:
            self.label.text = NSLocalizedStringFromTable(@"Error", @"CloudPrintPlugin", nil);
            break;
        default:
            self.label.text = NSLocalizedStringFromTable(@"Loading", @"CloudPrintPlugin", nil);
            break;
    }
}

- (void)setProgress:(NSProgress *)progress {
    @try {
        [self.progress removeObserver:self forKeyPath:NSStringFromSelector(@selector(fractionCompleted))];
    }
    @catch (NSException *exception) {}
    
    _progress = progress;
    [self updateProgress];
    [self.progress addObserver:self forKeyPath:NSStringFromSelector(@selector(fractionCompleted)) options:NSKeyValueObservingOptionNew context:NULL];
}

- (void)setUserCancelledBlock:(void (^)())userCancelledBlock {
    _userCancelledBlock = userCancelledBlock;
    if (userCancelledBlock) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelTapped)];
    } else {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:[[UIView alloc] initWithFrame:CGRectMake(0, 0, 20, 30)]];
    }
}

- (void)setShowTryAgainButtonWithTappedBlock:(void (^)())showTryAgainButtonWithTappedBlock {
    _showTryAgainButtonWithTappedBlock = showTryAgainButtonWithTappedBlock;
    self.tryAgainButton.hidden = (showTryAgainButtonWithTappedBlock == nil);
    self.progressView.hidden = (showTryAgainButtonWithTappedBlock != nil);
}

#pragma mark - Private

- (void)updateProgress {
    [self.progressView setProgress:self.progress.fractionCompleted animated:self.progress.fractionCompleted >= self.progressView.progress];
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.progress) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self updateProgress];
        });
    } else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    @try {
        [self.progress removeObserver:self forKeyPath:NSStringFromSelector(@selector(fractionCompleted))];
    }
    @catch (NSException *exception) {}
}

@end
