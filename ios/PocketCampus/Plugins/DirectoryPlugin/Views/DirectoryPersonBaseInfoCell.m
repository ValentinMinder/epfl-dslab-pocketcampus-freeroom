//
//  DirectoryPersonBaseInfoCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "DirectoryPersonBaseInfoCell.h"

#import "Person+Extras.h"

#import <QuartzCore/QuartzCore.h>

@interface DirectoryPersonBaseInfoCell ()<DirectoryServiceDelegate>

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
        [self.directoryService cancelOperationsForDelegate:self];
    }
    
    _person = person;
    
    [self.imageLoadingIndicator startAnimating];
    self.profilePicture = nil;
    self.profilePictureImageView.image = nil;
    [self.directoryService getProfilePicture:self.person delegate:self];
    
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

#pragma mark - DirectoryServiceDelegate

- (void)profilePictureFor:(Person *)person didReturn:(NSData *)data {
    if (person == self.person) {
        if (data) {
            self.profilePicture = [UIImage imageWithData:data];
            self.profilePictureImageView.image = self.profilePicture;
            self.profilePictureImageView.layer.borderWidth = 0.0;
            [self showImageViewAnimated:YES];
        } else {
            [self profilePictureFailedFor:person];
        }
    }
}

- (void)profilePictureFailedFor:(Person *)person {
    if (person == self.person) {
        self.profilePictureImageView.layer.borderWidth = 1.0;
        self.profilePicture = nil;
        self.profilePictureImageView.image = [UIImage imageNamed:@"DirectoryEmptyPicture"];
        [self showImageViewAnimated:YES];
    }
}

- (void)serviceConnectionToServerTimedOut {
    self.profilePictureImageView.layer.borderWidth = 1.0;
    self.profilePicture = nil;
    self.profilePictureImageView.image = [UIImage imageNamed:@"DirectoryEmptyPicture "];
    [self showImageViewAnimated:YES];
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.directoryService cancelOperationsForDelegate:self];
}

@end
