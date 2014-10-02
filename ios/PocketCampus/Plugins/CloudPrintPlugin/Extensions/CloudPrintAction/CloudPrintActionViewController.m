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

@end

@implementation CloudPrintActionViewController

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.tintColor = [PCValues pocketCampusRed];
    [self.cancelButton setTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) forState:UIControlStateNormal];

    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.text = nil;
    
    __weak __typeof(self) welf = self;
    BOOL pdfFound = NO;
    for (NSExtensionItem *item in self.extensionContext.inputItems) {
        for (NSItemProvider *itemProvider in item.attachments) {
            if ([itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypePDF]) {
                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypePDF options:nil completionHandler:^(NSURL* pdfURL, NSError *error) {
                    if (welf && pdfURL && !error) {
                        [welf.loadingIndicator stopAnimating];
                        welf.centerMessageLabel.text = nil;
                        NSString* filename = [pdfURL lastPathComponent];
                        UIViewController* viewController = [[CloudPrintController sharedInstance] viewControllerForPrintDocumentWithLocalURL:pdfURL docName:filename printDocumentRequestOrNil:nil completion:^(CloudPrintCompletionStatusCode printStatusCode) {
                            [welf.extensionContext completeRequestReturningItems:@[] completionHandler:NULL];
                        }];
                        viewController.view.tintColor = [PCValues pocketCampusRed];
                        [welf presentViewController:viewController animated:NO completion:NULL];
                    }
                }];
                pdfFound = YES;
                break;
            }
        }
        
        if (pdfFound) {
            // We only handle one PDF, so stop looking for more.
            break;
        }
    }
    
    if (!pdfFound) {
        [self.loadingIndicator stopAnimating];
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"SorryUnsupportedFileFormat", @"CloudPrintPlugin", nil);
    }
}

#pragma mark - Actions

- (IBAction)cancelTapped:(id)sender {
    [self.extensionContext cancelRequestWithError:[NSError errorWithDomain:@"User cancelled" code:0 userInfo:nil]];
}

@end
