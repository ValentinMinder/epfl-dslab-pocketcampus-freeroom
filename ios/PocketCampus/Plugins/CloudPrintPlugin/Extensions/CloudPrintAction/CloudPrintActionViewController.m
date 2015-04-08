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

//  Created by Loïc Gardiol on 14.09.14.

#import "CloudPrintActionViewController.h"

#import "CloudPrintController.h"

@import MobileCoreServices;

@interface CloudPrintActionViewController ()

@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIButton* cancelButton;

@property (nonatomic, strong) CloudPrintController* cloudPrintController;

@property (nonatomic, copy) NSString* loadedUTType;

@end

@implementation CloudPrintActionViewController

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.cloudPrintController = [CloudPrintController sharedInstance];
    self.cloudPrintController.extensionContext = self.extensionContext;
    
    self.view.tintColor = [PCValues pocketCampusRed];
    [self.cancelButton setTitle:NSLocalizedStringFromTable(@"Close", @"PocketCampus", nil) forState:UIControlStateNormal];

    
    if (![[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY]) {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"OpenPocketCampusAndLoginFirst", @"CloudPrintPlugin", nil);
        return;
    }
    
    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.text = nil;
    
    __weak __typeof(self) welf = self;
    BOOL itemFound = NO;
    for (NSExtensionItem *item in self.extensionContext.inputItems) {
        for (NSItemProvider *itemProvider in item.attachments) {
            if ([itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypePDF]) {
                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypePDF options:nil completionHandler:^(NSURL* pdfURL, NSError *error) {
                    if (!welf) {
                        return;
                    }
                    if (!pdfURL || error) {
                        [welf showUnknownError];
                        return;
                    }
                    welf.loadedUTType = (NSString*)kUTTypePDF;
                    [welf loadItemWithURL:pdfURL];
                }];
                itemFound = YES;
                break;
            }
            if (!itemFound && [itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypeFileURL]) {
                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypeURL options:nil completionHandler:^(NSURL* fileURL, NSError *error) {
                    if (!welf) {
                        return;
                    }
                    if (!fileURL || error) {
                        [welf showUnknownError];
                        return;
                    }
                    welf.loadedUTType = (NSString*)kUTTypeFileURL;
                    [welf loadItemWithURL:fileURL];
                }];
                itemFound = YES;
                break;
            }
            if (!itemFound && [itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypeURL]) {
                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypeURL options:nil completionHandler:^(NSURL* url, NSError *error) {
                    if (!welf) {
                        return;
                    }
                    if (!url || error) {
                        [welf showUnknownError];
                        return;
                    }
                    welf.loadedUTType = (NSString*)kUTTypeURL;
                    [welf loadItemWithURL:url];
                }];
                itemFound = YES;
                break;
            }
        }
        
        if (itemFound) {
            // We only handle one PDF, so stop looking for more.
            break;
        }
    }
    
    if (!itemFound) {
        [self showUnsupportedFileFormatError];
    }
}

#pragma mark - Actions

- (IBAction)cancelTapped:(id)sender {
    [self.extensionContext cancelRequestWithError:[NSError errorWithDomain:@"User cancelled" code:0 userInfo:nil]];
}

#pragma mark - Private

- (void)loadItemWithURL:(NSURL*)url {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.loadingIndicator stopAnimating];
        self.centerMessageLabel.text = nil;
        NSString* filename = [url lastPathComponent];
        __weak __typeof(self) welf = self;
        UIViewController* viewController = [self.cloudPrintController viewControllerForPrintDocumentWithURL:url docName:filename printDocumentRequestOrNil:nil completion:^(CloudPrintCompletionStatusCode printStatusCode) {
            if (printStatusCode == CloudPrintCompletionStatusCodeUnsupportedFile) {
                [welf dismissViewControllerAnimated:YES completion:NULL];
                [welf showUnsupportedFileFormatError];
            } else {
                [welf.extensionContext completeRequestReturningItems:@[] completionHandler:NULL];
            }
        }];
        if (viewController) {
            viewController.view.tintColor = [PCValues pocketCampusRed];
            [self presentViewController:viewController animated:NO completion:NULL];
        }
    });
}

- (void)showUnknownError {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"SorryErrorOccured", @"CloudPrintPlugin", nil);
}

- (void)showUnsupportedFileFormatError {
    [self.loadingIndicator stopAnimating];
    if ([self.loadedUTType isEqualToString:(NSString*)kUTTypeURL]) {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"SorryUnsupportedURLSafariExplanations", @"CloudPrintPlugin", nil);
    } else {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"SorryUnsupportedFileFormat", @"CloudPrintPlugin", nil);
    }
}

@end
