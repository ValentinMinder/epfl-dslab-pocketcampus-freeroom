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

//  Created by Loïc Gardiol on 06.10.14.

#import "RecommendedAppCollectionViewCell.h"

#import "recommendedapps.h"

#import <AFNetworking/AFNetworking.h>

#import "UIImageView+AFNetworking.h"

static NSString* kAppNameKey = @"AppName";
static NSString* kAppDescriptionKey = @"AppDescription";
static NSString* kAppImageURLKey = @"AppImageURL";

@interface RecommendedAppCollectionViewCell ()

@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UIImageView* appThumbImageView;
@property (nonatomic, weak) IBOutlet UILabel* appNameLabel;
@property (nonatomic, weak) IBOutlet UILabel* appDescriptionLabel;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;

@property (nonatomic, strong) AFHTTPRequestOperation* requestOperation;

@end

@implementation RecommendedAppCollectionViewCell

#pragma mark - UIView overrides

- (void)awakeFromNib {
    [super awakeFromNib];
    self.appThumbImageView.layer.cornerRadius = 16.0;
    self.appThumbImageView.layer.borderWidth = 0.5;
    self.appThumbImageView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.appThumbImageView.clipsToBounds = YES;
    self.appThumbImageView.layer.masksToBounds = YES;
}

#pragma mark - UICollectionViewCell overrides

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    self.backgroundColor = selected ? [UIColor colorWithWhite:0.0 alpha:0.1] : [UIColor clearColor];
}

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    self.backgroundColor = highlighted ? [UIColor colorWithWhite:0.0 alpha:0.1] : [UIColor clearColor];
}

#pragma mark - Public

- (void)setApp:(RecommendedApp *)app {
    _app = app;

    [self.requestOperation setCompletionBlockWithSuccess:NULL failure:NULL];
    [self.requestOperation cancel];
    self.centerMessageLabel.text = nil;
    [self.appThumbImageView cancelImageRequestOperation];
    self.appThumbImageView.image = nil;
    self.appNameLabel.text = nil;
    self.appDescriptionLabel.text = nil;
    if (!app) {
        return;
    }
    
    static NSCache* appInfoForAppStoreQuery;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        appInfoForAppStoreQuery = [NSCache new];
    });
    
    NSString* appStoreQuery = [app.appStoreQuery copy];
    
    NSDictionary* appInfo = appInfoForAppStoreQuery[appStoreQuery];
    if (appInfo) {
        [self setWithAppInfo:appInfo];
        return;
    }
    
    [self.loadingIndicator startAnimating];
    __weak __typeof(self) welf = self;
    
    NSString* jsonString = [NSString stringWithFormat:@"http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsLookup?country=ch&lang=%@&id=%@", [PCUtils userLanguageCode], appStoreQuery];
    NSURL* jsonURL = [NSURL URLWithString:jsonString];
    NSURLRequest* request = [NSURLRequest requestWithURL:jsonURL];

    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = [AFJSONResponseSerializer serializer];

    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        if (!welf) {
            return;
        }
        if(!responseObject){
            [welf error];
            return;
        }
        NSArray* resultsArray = [responseObject objectForKey:@"results"];
        if (resultsArray.count == 0) {
            [welf error];
            return;
        }
        id results = resultsArray[0];
        
        NSString* appName =  [results valueForKey:@"trackName"];
        NSString* appDescription = [results valueForKey:@"description"];
        NSString* appImageURLString = [results valueForKey:@"artworkUrl100"];
        
        NSMutableDictionary* appInfo = [NSMutableDictionary dictionary];
        if (appName) {
            appInfo[kAppNameKey] = appName;
        }
        if (appDescription) {
            appInfo[kAppDescriptionKey] = appDescription;
        }
        if (appImageURLString) {
            appInfo[kAppImageURLKey] = [NSURL URLWithString:appImageURLString];
        }
        appInfoForAppStoreQuery[appStoreQuery] = appInfo;
        
        
        [welf setWithAppInfo:appInfo];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [welf error];
    }];
    
    self.requestOperation = operation;
    
    [operation start];
}

#pragma mark - Private

- (void)setWithAppInfo:(NSDictionary*)appInfo {
    [self.loadingIndicator stopAnimating];
    self.appNameLabel.text = appInfo[kAppNameKey];
    self.appDescriptionLabel.text = appInfo[kAppDescriptionKey];
    NSURL* imageURL = appInfo[kAppImageURLKey];
    self.appThumbImageView.image = nil;
    if (imageURL) {
        [self.appThumbImageView setImageWithURL:imageURL];
    }
}

- (void)error {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.requestOperation setCompletionBlockWithSuccess:NULL failure:NULL];
    [self.requestOperation cancel];
    [self.appThumbImageView cancelImageRequestOperation];
}

@end
