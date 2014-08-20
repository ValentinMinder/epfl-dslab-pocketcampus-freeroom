//
//  LORadarItemBubbleView.m
//  Locus
//
//  Created by Loïc Gardiol on 06.03.14.
//  Copyright (c) 2014 Locus. All rights reserved.
//

#import "RecommendedAppThumbView.h"

#import "UIImageView+AFNetworking.h"

#import <QuartzCore/QuartzCore.h>

#import <AFURLSessionManager.h>

#import <AFHTTPRequestOperation.h>

@interface RecommendedAppThumbView()

@property (nonatomic, strong) IBOutlet UIImageView* imageView;

@property (nonatomic, strong) IBOutlet UILabel* titleLabel;

@property (nonatomic, strong) IBOutlet UILabel* descriptionLabel;

@property (nonatomic, weak) AFHTTPRequestOperation* appDetailsRequestOperation;

@end
@implementation RecommendedAppThumbView

#pragma mark - Init

- (instancetype)initWithRecommendedApp:(RecommendedApp*)recommendedApp
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"RecommendedAppThumbView" owner:nil options:nil];
    self = (RecommendedAppThumbView*)elements[0];
    if (self) {
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        self.recommendedApp = recommendedApp;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self addConstraints:[NSLayoutConstraint width:250 height:80 constraintsForView:self]];
    }
    return self;
}

#pragma mark - Public

- (void)setRecommendedApp:(RecommendedApp*)recommendedApp {
    _recommendedApp = recommendedApp;
    __weak __typeof(self) welf = self;
    
    NSString* jsonString = [NSString stringWithFormat:@"http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsLookup?country=ch&id=%@", recommendedApp.appStoreQuery];
    NSURL* jsonURL = [NSURL URLWithString:jsonString];
    NSURLRequest* request = [NSURLRequest requestWithURL:jsonURL];
    
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = [AFJSONResponseSerializer serializer];
    
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        CLS_LOG(@"Downloaded info for %@ (%@)", welf.recommendedApp.appName, responseObject);
        if(!responseObject){
            CLS_LOG(@"Failed to download image for %@", welf.recommendedApp.appName);
            return;
        }
       
        id resultsArray = [responseObject objectForKey:@"results"];
        id results = resultsArray[0];
        welf.titleLabel.text = [results valueForKey:@"trackName"];
        welf.descriptionLabel.text = [results valueForKey:@"description"];
        NSString* logoUrl = [results valueForKey:@"artworkUrl60"];
        [welf.imageView setImageWithURL:[NSURL URLWithString:logoUrl]];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        CLS_LOG(@"Error %@", error);
    }];
    
    self.appDetailsRequestOperation = operation;
    [operation start];
}

#pragma mark - Description

- (NSString*)description {
    return [[super description] stringByAppendingFormat:@"%@", self.recommendedApp];
}

- (void)dealloc
{
    [self.appDetailsRequestOperation cancel];
    [self.imageView cancelImageRequestOperation];
}

@end
