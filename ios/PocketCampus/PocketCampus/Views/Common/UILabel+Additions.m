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






//  Created by Loïc Gardiol on 01.01.14.



#import "UILabel+Additions.h"

@implementation UILabel (Additions)

- (void)setHighlightedColor:(UIColor*)highlightedColor forMatchesOfRegex:(NSRegularExpression*)regex dimmedColor:(UIColor*)dimmedColor  {
    [PCUtils throwExceptionIfObject:highlightedColor notKindOfClass:[UIColor class]];
    [PCUtils throwExceptionIfObject:regex notKindOfClass:[NSRegularExpression class]];
    [PCUtils throwExceptionIfObject:dimmedColor notKindOfClass:[UIColor class]];
    __weak __typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW,0), ^{
        NSMutableAttributedString* attrText = [[NSMutableAttributedString alloc] initWithString:self.text attributes:@{NSForegroundColorAttributeName:dimmedColor,NSFontAttributeName:self.font}];
        [regex enumerateMatchesInString:self.text options:0 range:NSMakeRange(0, self.text.length) usingBlock:^(NSTextCheckingResult* result, NSMatchingFlags flags, BOOL* stop) {
            [attrText addAttribute:NSForegroundColorAttributeName value:highlightedColor range:result.range];
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            weakSelf.attributedText = attrText;
        });
    });
}

@end
