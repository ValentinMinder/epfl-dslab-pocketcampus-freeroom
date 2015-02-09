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

//  Created by Loïc Gardiol on 14.10.14.

#import "CloudPrintPOOL1InfoViewController.h"

#import "CloudPrintController.h"

#import "MapController.h"

#import "map.h"

@interface CloudPrintPOOL1InfoViewController ()

@end

@implementation CloudPrintPOOL1InfoViewController

#pragma mark - Init

- (instancetype)init {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.title = @"POOL1";
        self.preferredContentSize = CGSizeZero;
    }
    return self;
}

#pragma mark - Actions

- (IBAction)showPrintersOnMapTapped:(id)sender {
#ifdef TARGET_IS_EXTENSION
    //Does not work from action extension in iOS 8.0
    /*NSString* urlString = [NSString stringWithFormat:@"pocketcampus://map.plugin.pocketcampus.org/showLayer?layerId=%ld", [mapConstants MapLayerIdMyPrint]];
    NSURL* url = [NSURL URLWithString:urlString];
    [[CloudPrintController sharedInstance].extensionContext openURL:url completionHandler:NULL];*/
    
    UIAlertController* alertController = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"Sorry", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"OpenPCandShowMyPrintLayerExplanations", @"CloudPrintPlugin", nil) preferredStyle:UIAlertControllerStyleAlert];
    [alertController addAction:[UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"Ok", @"PocketCampus", nil) style:UIAlertActionStyleCancel handler:NULL]];
    [self presentViewController:alertController animated:YES completion:NULL];
#else
     UIViewController* viewController = [MapController viewControllerWithMapLayerIdsToDisplay:[NSSet setWithObject:@([mapConstants MapLayerIdMyPrint])]];
     viewController.title = NSLocalizedStringFromTable(@"POOL1Printers", @"CloudPrintPlugin", nil);
     [self.navigationController pushViewController:viewController animated:YES];
#endif
}

@end
