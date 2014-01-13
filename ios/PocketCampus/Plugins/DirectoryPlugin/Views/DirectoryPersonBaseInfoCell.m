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






//  Created by Loïc Gardiol on 25.09.13.



#import "DirectoryPersonBaseInfoCell.h"

#import "Person+Extras.h"

#import "AFNetworking.h"

#import "UIImageView+AFNetworking.h"

#import <QuartzCore/QuartzCore.h>

@interface DirectoryPersonBaseInfoCell ()

@property (nonatomic, strong) DirectoryService* directoryService;

@property (nonatomic, readwrite) DirectoryPersonBaseInfoCellStyle style;

@property (nonatomic, readwrite, strong) UIImage* profilePicture;

@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* imageLoadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* titleLabel;

@end

@implementation DirectoryPersonBaseInfoCell

- (id)initWithDirectoryPersonBaseInfoCellStyle:(DirectoryPersonBaseInfoCellStyle)style reuseIdentifer:(NSString*)reuseIdentifier; {
    self = [[NSBundle mainBundle] loadNibNamed:@"DirectoryPersonBaseInfoCell" owner:self options:nil][0];
    if (self) {
        if (style != DirectoryPersonBaseInfoCellStyleLarge) {
            [NSException raise:@"Unimplemented style" format:@"sorry, only style DirectoryPersonBaseInfoCellStyleLarge is currently supported"];
        }
        _style = style;
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.profilePictureImageView.layer.cornerRadius = self.profilePictureImageView.frame.size.width / 2.0;
        self.profilePictureImageView.layer.masksToBounds = YES;
        self.profilePictureImageView.layer.borderWidth = 1.0;
        self.profilePictureImageView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.5].CGColor;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        UIView* backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
        backgroundView.backgroundColor = [UIColor clearColor];
        self.backgroundView = backgroundView;
        self.backgroundColor = [UIColor clearColor];
        self.separatorInset = UIEdgeInsetsMake(0, 50, 0, 0);
    }
    return self;
}

+ (CGFloat)heightForStyle:(DirectoryPersonBaseInfoCellStyle)style {
    return 106.0;
}

- (void)setPerson:(Person *)person {
    [PCUtils throwExceptionIfObject:person notKindOfClass:[Person class]];
    
    if (_person) {
        [self.profilePictureImageView cancelImageRequestOperation];
    }
    
    _person = person;
    
    if (self.person.pictureUrl) {
        [self.imageLoadingIndicator startAnimating];
        self.profilePicture = nil;
        self.profilePictureImageView.image = nil;
        NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"GET" URLString:self.person.pictureUrl parameters:nil];
        request.cachePolicy = NSURLRequestUseProtocolCachePolicy;
        __weak __typeof(self) weakSelf = self;
        [self.profilePictureImageView setImageWithURLRequest:request placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            if (image) {
                image = [[UIImage alloc] initWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
                weakSelf.profilePicture = image;
                weakSelf.profilePictureImageView.image = image;
                weakSelf.profilePictureImageView.layer.borderWidth = 0.0;
                [weakSelf showImageViewAnimated:YES];
            } else {
                [weakSelf noProfilePictureOrError];
            }
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            [weakSelf noProfilePictureOrError];
        }];
    } else {
        [self noProfilePictureOrError];
    }
    
    
    NSString* firstLastName = self.person.fullFirstnameLastname;
    NSString* organizations = self.person.organizationsString;
    NSString* finalString = [NSString stringWithFormat:@"%@\n%@", firstLastName, organizations];
    
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:finalString];
    
    [attrString setAttributes:[NSDictionary dictionaryWithObject:[UIFont preferredFontForTextStyle:UIFontTextStyleHeadline] forKey:NSFontAttributeName] range:[finalString rangeOfString:firstLastName]];
    
    [attrString setAttributes:[NSDictionary dictionaryWithObject:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline] forKey:NSFontAttributeName] range:[finalString rangeOfString:organizations]];
    
    [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:[finalString rangeOfString:organizations]];
    
    self.titleLabel.attributedText = attrString;
}

#pragma mark - ImageView stuff

- (void)noProfilePictureOrError {
    self.profilePictureImageView.layer.borderWidth = 1.0;
    self.profilePicture = nil;
    self.profilePictureImageView.image = [UIImage imageNamed:@"DirectoryEmptyPicture"];
    [self showImageViewAnimated:YES];
}

- (void)showImageViewAnimated:(BOOL)animated {
    self.profilePictureImageView.alpha = 0.0;
    [UIView animateWithDuration:animated ? 0.5 : 0.0 animations:^{
        self.profilePictureImageView.alpha = 1.0;
        self.imageLoadingIndicator.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self.imageLoadingIndicator stopAnimating];
        self.imageLoadingIndicator.alpha = 1.0;
    }];
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.profilePictureImageView cancelImageRequestOperation];
}

@end
