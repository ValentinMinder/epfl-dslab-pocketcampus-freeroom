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

//  Created by Loïc Gardiol on 20.07.14.

#import "MoodleResourceCell.h"

#import "MoodleService.h"

@interface MoodleResourceCell ()

@property (nonatomic, strong, readwrite) MoodleResource2* resource;
@property (nonatomic, strong) MoodleService* moodleService;

@end

@implementation MoodleResourceCell

#pragma mark - Init

- (instancetype)initWithMoodleResource:(MoodleResource2*)resource {
    [PCUtils throwExceptionIfObject:resource notKindOfClass:[MoodleResource2 class]];
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    if (self) {
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.resource = resource;
    }
    return self;
}

#pragma mark - Accessibility

- (NSString*)accessibilityLabel {
    NSString* string = nil;
    if (self.resource.file) {
        string = [NSString stringWithFormat:NSLocalizedStringFromTable(@"DocumentDescriptionWithFormat", @"MoodlePlugin", nil), self.resource.file.name, self.resource.file.fileExtension, [self.moodleService isMoodleFileDownloaded:self.resource.file] ? NSLocalizedStringFromTable(@"yes", @"PocketCampus", nil) : NSLocalizedStringFromTable(@"no", @"PocketCampus", nil)];
    } else if (self.resource.folder) {
        string = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FolderDescriptionWithFormat", @"MoodlePlugin", nil), self.resource.folder.name, (int)(self.resource.folder.files.count)];
    } else if (self.resource.url) {
        string = [NSString stringWithFormat:NSLocalizedStringFromTable(@"LinkDescriptionWithFormat", @"MoodlePlugin", nil), self.resource.folder.name];
    }
    return string ?: [super accessibilityLabel];
}

- (UIAccessibilityTraits)accessibilityTraits {
    if (self.resource) {
        return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
    } else {
        return [super accessibilityTraits];
    }
}

#pragma mark - Private

- (void)setResource:(MoodleResource2*)resource {
    _resource = resource;
    
    self.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    self.textLabel.adjustsFontSizeToFitWidth = YES;
    self.textLabel.minimumScaleFactor = 0.9;
    
    self.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
    self.detailTextLabel.adjustsFontSizeToFitWidth = YES;
    self.detailTextLabel.minimumScaleFactor = 0.9;
    
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.imageView.image = resource.systemIcon;
    
    if (resource.file) {
        self.textLabel.text = resource.file.name;
        self.detailTextLabel.text = resource.file.filename;
        
        self.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        self.downloadedIndicationVisible = [self.moodleService isMoodleFileDownloaded:resource.file];
        self.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleItem:resource.file];
        
        __weak __typeof(self) welf = self;
        [self.moodleService addMoodleFileObserver:self forFile:resource.file eventBlock:^(MoodleResourceEvent event) {
            switch (event) {
                case MoodleResourceEventDownloaded:
                    welf.downloadedIndicationVisible  = YES;
                    break;
                case MoodleResourceEventDeleted:
                    welf.downloadedIndicationVisible  = NO;
                    break;
                default:
                    break;
            }
            [welf setNeedsLayout];
        }];
        
    } else if (resource.folder) {
        self.textLabel.text = resource.folder.name;
        self.detailTextLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FolderNbElementsWithFormat", @"MoodlePlugin", nil), (int)(resource.folder.files.count)];
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        self.downloadedIndicationVisible = NO;
        self.favoriteIndicationVisible = NO;
    } else if (resource.url) {
        self.textLabel.text = resource.url.name;
        self.detailTextLabel.text = resource.url.url;
        self.detailTextLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
        self.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        self.downloadedIndicationVisible = NO;
        self.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleItem:resource.url];
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.moodleService removeMoodleFileObserver:self];
}

@end
