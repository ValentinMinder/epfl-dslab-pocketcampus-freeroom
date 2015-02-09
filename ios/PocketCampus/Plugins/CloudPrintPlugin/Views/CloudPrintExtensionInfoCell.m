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

//  Created by Loïc Gardiol on 24.10.14.

#import "CloudPrintExtensionInfoCell.h"

@import CoreText;

@interface CloudPrintExtensionInfoCell ()

@property (nonatomic, weak) IBOutlet UILabel* label;

@end

@implementation CloudPrintExtensionInfoCell

#pragma mark - Init

- (instancetype)init {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil];
    self = (CloudPrintExtensionInfoCell*)elements[0];
    if (self) {
        self.label.attributedText = [self.class infoAttributedString];
    }
    return self;
}

#pragma mark - Public

- (CGFloat)preferredHeightInTableView:(UITableView*)tableView {
    NSAttributedString* attrString = [self.class infoAttributedString];
    CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString((__bridge CFAttributedStringRef)attrString);
    CGSize targetSize = CGSizeMake(tableView.frame.size.width-52.0, CGFLOAT_MAX); //account for text left and right insets of the text view
    CGSize size = CTFramesetterSuggestFrameSizeWithConstraints(framesetter, CFRangeMake(0, [attrString length]), NULL, targetSize, NULL);
    CFRelease(framesetter);
    return size.height+20.0;
}

#pragma mark - Private

- (IBAction)closeTapped {
    if (self.closeButtonTapped) {
        self.closeButtonTapped();
    }
}

+ (NSAttributedString*)infoAttributedString {
    static NSAttributedString* attrString = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString* title = NSLocalizedStringFromTable(@"DidYouKnowExtensionTitle", @"CloudPrintPlugin", nil);
        NSString* subtitle = NSLocalizedStringFromTable(@"DidYouKnowExtensionSubtitle", @"CloudPrintPlugin", nil);
        NSString* finalString = [NSString stringWithFormat:@"%@\n%@", title, subtitle];
        NSMutableAttributedString* mAttrString = [[NSMutableAttributedString alloc] initWithString:finalString];
        [mAttrString setAttributes:@{NSFontAttributeName:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline]} range:[finalString rangeOfString:title]];
        [mAttrString setAttributes:@{NSFontAttributeName:[UIFont preferredFontForTextStyle:UIFontTextStyleCaption1], NSForegroundColorAttributeName:[UIColor darkGrayColor]} range:[finalString rangeOfString:subtitle]];
        attrString = [mAttrString copy];
    });
    return attrString;
}

@end
