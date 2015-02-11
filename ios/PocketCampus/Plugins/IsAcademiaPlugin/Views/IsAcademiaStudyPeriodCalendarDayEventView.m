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

//  Created by Loïc Gardiol on 19.03.14.

#import "IsAcademiaStudyPeriodCalendarDayEventView.h"

#import "IsAcademiaModelAdditions.h"

@implementation IsAcademiaStudyPeriodCalendarDayEventView

#pragma mark - Init

- (instancetype)init {
    self = [super initWithFrame:CGRectMake(0, 0, 100, 100)];
    self.clipsToBounds = NO;
    self.layer.masksToBounds = NO;
    self.titleLabel.clipsToBounds = NO;
    self.titleLabel.layer.masksToBounds = NO;
    self.titleLabel.isAccessibilityElement = NO;
    self.locationLabel.isAccessibilityElement = NO;
    return self;
}

#pragma mark - Public

+ (IsAcademiaStudyPeriodCalendarDayEventView*)studyPeriodEventView {
    return [self new];
}

- (void)setStudyPeriod:(StudyPeriod *)studyPeriod {
    _studyPeriod = studyPeriod;
    
    self.startDate = [NSDate dateWithTimeIntervalSince1970:self.studyPeriod.startTime/1000];
    self.endDate = [NSDate dateWithTimeIntervalSince1970:self.studyPeriod.endTime/1000];
    
    self.locationLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleFootnote];
    
    self.titleLabel.textColor = [self darkColor];
    self.locationLabel.textColor = [self darkColor];
    
    self.backgroundColor = [self lightColorWithAlpha:0.2];
    self.edgeView.backgroundColor = [self lightColorWithAlpha:1.0];
    
    if ((self.studyPeriod.endTime/1000 - self.studyPeriod.startTime/1000) < 5400) { //less than 1h30 = two periods
        
        self.titleLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleCaption2];
        
        NSString* roomsString = self.studyPeriod.rooms.count > 1 ? [NSString stringWithFormat:@"%@…", self.studyPeriod.rooms[0]] : self.studyPeriod.roomsString;
        
        NSString* fullString = [NSString stringWithFormat:@"%@ %@ %@", self.studyPeriod.startTimeString, roomsString, self.studyPeriod.name];
        NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
        [attrString addAttribute:NSUnderlineStyleAttributeName value:@(NSUnderlineStyleSingle) range:[fullString rangeOfString:roomsString]];
        [attrString addAttribute:NSFontAttributeName value:[UIFont boldSystemFontOfSize:self.titleLabel.font.pointSize+2] range:[fullString rangeOfString:self.studyPeriod.name]];
        self.titleLabel.attributedText = attrString;
        
        self.locationLabel.text = nil;
        
    } else {
        self.titleLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleFootnote];
        
        NSString* fullString = [NSString stringWithFormat:@"%@\n%@", self.studyPeriod.startTimeString, self.studyPeriod.name];
        NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
        
        [attrString addAttribute:NSFontAttributeName value:[UIFont boldSystemFontOfSize:self.titleLabel.font.pointSize+2] range:[fullString rangeOfString:self.studyPeriod.name]];
        self.titleLabel.attributedText = attrString;
        
        self.locationLabel.attributedText = [[NSAttributedString alloc] initWithString:self.studyPeriod.roomsString attributes:@{NSUnderlineStyleAttributeName:@(NSUnderlineStyleSingle)}];
    }
}

#pragma mark - Accessiblity

- (BOOL)isAccessibilityElement {
    return YES;
}

- (NSString*)accessibilityLabel {
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodViewAccessiblityLabelWithFormat", @"IsAcademiaPlugin", nil), self.studyPeriod.startTimeString, self.studyPeriod.name, self.studyPeriod.roomsString];
}

- (NSString*)accessibilityHint {
    return NSLocalizedStringFromTable(@"PeriodViewAccessiblityHint", @"IsAcademiaPlugin", nil);
}

#pragma mark - Private

- (UIColor*)darkColor {
    UIColor* color = nil;
    switch (self.studyPeriod.periodType) {
        case StudyPeriodType_LECTURE:
            color = [UIColor colorWithRed:0.145098 green:0.447059 blue:0.572549 alpha:1.0];
            break;
        case StudyPeriodType_EXERCISES:
            color = [UIColor colorWithRed:0.290196 green:0.498039 blue:0.200000 alpha:1.0];
            break;
        case StudyPeriodType_LAB:
            color = [UIColor colorWithRed:0.619608 green:0.384314 blue:0.101961 alpha:1.0];
            break;
        case StudyPeriodType_PROJECT:
            color = [UIColor colorWithWhite:0.3 alpha:1.0];
            break;
        case StudyPeriodType_ORAL_EXAM:
            color = [UIColor colorWithRed:0.349020 green:0.090196 blue:0.458824 alpha:1.0];
            break;
        case StudyPeriodType_WRITTEN_EXAM:
            color = [UIColor colorWithRed:0.349020 green:0.090196 blue:0.458824 alpha:1.0];
            break;
        default:
            color = [UIColor colorWithWhite:0.3 alpha:1.0];
            break;
    }
    return color;
}

- (UIColor*)lightColorWithAlpha:(CGFloat)alpha {
    UIColor* color = nil;
    switch (self.studyPeriod.periodType) {
        case StudyPeriodType_LECTURE:
            color = [UIColor colorWithRed:0.200000 green:0.682353 blue:0.960784 alpha:alpha];
            break;
        case StudyPeriodType_EXERCISES:
            color = [UIColor colorWithRed:0.403922 green:0.850980 blue:0.266667 alpha:alpha];
            break;
        case StudyPeriodType_LAB:
            color = [UIColor colorWithRed:0.988235 green:0.580392 blue:0.141176 alpha:alpha];
            break;
        case StudyPeriodType_PROJECT:
            color = [UIColor colorWithWhite:0.6 alpha:alpha];
            break;
        case StudyPeriodType_ORAL_EXAM:
            color = [UIColor colorWithRed:0.552941 green:0.086275 blue:0.729412 alpha:alpha];
            break;
        case StudyPeriodType_WRITTEN_EXAM:
            color = [UIColor colorWithRed:0.552941 green:0.086275 blue:0.729412 alpha:alpha];
            break;
        default:
            color = [UIColor colorWithWhite:0.6 alpha:alpha];
            break;
    }
    return color;
}

- (NSString*)debugDescription {
    return [[super debugDescription] stringByAppendingString:[self.studyPeriod debugDescription]];
}

@end
