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

//  Created by Loïc Gardiol on 22.07.14.

#import "MoodleUrlViewController.h"

#import "MoodleService.h"

@interface MoodleUrlViewController ()

@property (nonatomic, readwrite, strong) MoodleUrl2* moodleUrl;
@property (nonatomic, strong) MoodleService* moodleService;

@end

@implementation MoodleUrlViewController

#pragma mark - Init

- (instancetype)initWithMoodleUrl:(MoodleUrl2*)moodleUrl {
    [PCUtils throwExceptionIfObject:moodleUrl notKindOfClass:[MoodleUrl2 class]];
    self = [super initWithURL:[NSURL URLWithString:moodleUrl.url] title:moodleUrl.name];
    if (self) {
        self.gaiScreenName = @"/moodle/course/link";
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.moodleUrl = moodleUrl;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSMutableArray* rightBarButtonItems = [self.navigationItem.rightBarButtonItems mutableCopy];
    [rightBarButtonItems addObject:[self newFavoriteButton]];
    self.navigationItem.rightBarButtonItems = rightBarButtonItems;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFavoriteButton) name:kMoodleFavoritesMoodleItemsUpdatedNotification object:self.moodleService];
}

#pragma mark - Button actions

- (void)favoriteButtonTapped {
    if ([self.moodleService isFavoriteMoodleItem:self.moodleUrl]) {
        [self trackAction:PCGAITrackerActionUnmarkFavorite contentInfo:self.moodleUrl.name];
        [self.moodleService removeFavoriteMoodleItem:self.moodleUrl];
    } else {
        [self trackAction:PCGAITrackerActionMarkFavorite contentInfo:self.moodleUrl.name];
        [self.moodleService addFavoriteMoodleItem:self.moodleUrl];
    }
}

#pragma mark - Private

- (void)refreshFavoriteButton {
    UIBarButtonItem* favButton = [self newFavoriteButton];
    NSUInteger index = [self.navigationItem.rightBarButtonItems indexOfObject:[self favoriteButton]];
    NSMutableArray* items = [self.navigationItem.rightBarButtonItems mutableCopy];
    [items replaceObjectAtIndex:index withObject:favButton];
    self.navigationItem.rightBarButtonItems = items;
}

- (UIBarButtonItem*)favoriteButton {
    return [self.navigationItem.rightBarButtonItems lastObject];
}

- (UIBarButtonItem*)newFavoriteButton {
    BOOL isFavorite = [self.moodleService isFavoriteMoodleItem:self.moodleUrl];
    UIImage* favoriteImage = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIImage* favoriteImageLandscape = [PCValues imageForFavoriteNavBarButtonLandscapePhone:YES glow:isFavorite];
    
    UIBarButtonItem* favoriteButton = [[UIBarButtonItem alloc] initWithImage:favoriteImage landscapeImagePhone:favoriteImageLandscape style:UIBarButtonItemStylePlain target:self action:@selector(favoriteButtonTapped)];
    favoriteButton.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveLinkFromFavorites", @"MoodlePlugin", nil) : NSLocalizedStringFromTable(@"AddLinkToFavorites", @"MoodlePlugin", nil);
    return favoriteButton;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
