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

//  Created by Loïc Gardiol on 04.06.15.

#import "IsAcademiaCourseGradeCell.h"

@interface IsAcademiaCourseGradeCell ()

@property (nonatomic, copy) NSString* pReuseIdentifier;

@property (nonatomic, weak) IBOutlet UILabel* courseNameLabel;
@property (nonatomic, weak) IBOutlet UILabel* gradeLabel;
@property (nonatomic, weak) IBOutlet UIView* lineView;

@end

@implementation IsAcademiaCourseGradeCell

#pragma mark - Init

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.pReuseIdentifier = reuseIdentifier;
        self.courseNameLabel.text = nil;
        self.gradeLabel.text = nil;
    }
    return self;
}

#pragma mark - UITableViewCell overrides

- (NSString*)reuseIdentifier {
    return self.pReuseIdentifier;
}

#pragma mark - Public

- (void)setCourseName:(NSString*)courseName andGrade:(NSString*)grade {
    static UIFont* activeFont = nil;
    static UIFont* inactiveFont = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        activeFont = [UIFont fontWithName:@"HelveticaNeue" size:17.0];
        inactiveFont = [UIFont fontWithName:@"HelveticaNeue-Light" size:17.0];
    });
    self.courseNameLabel.text = courseName;
    self.gradeLabel.text = grade;
    if (grade.length > 0) {
        self.courseNameLabel.font = activeFont;
        self.courseNameLabel.textColor = [UIColor colorWithWhite:0.25 alpha:1.0];
        self.lineView.hidden = NO;
    } else {
        self.courseNameLabel.font = inactiveFont;
        self.courseNameLabel.textColor = [UIColor grayColor];
        self.lineView.hidden = YES;
    }
}

@end
