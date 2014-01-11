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




//  Created by Loïc Gardiol on 07.10.12.


#import "MainMenuItemCell.h"

#import "PCValues.h"

@interface MainMenuItemCell ()

@property (nonatomic, readwrite, copy) NSString* reuseIdentifier;

@property (nonatomic, weak) IBOutlet UIImageView* leftImageView;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UILabel* subtitleLabel;
@property (nonatomic, weak) IBOutlet UIButton* eyeButton;

@property (nonatomic, weak) IBOutlet UIView* line1;
@property (nonatomic, weak) IBOutlet UIView* line2;

@property (nonatomic, readwrite, strong) MainMenuItem* menuItem;

@end

@implementation MainMenuItemCell

@synthesize reuseIdentifier = _reuseIdentifier;

+ (MainMenuItemCell*)cellWithMainMenuItem:(MainMenuItem*)menuItem reuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"MainMenuItemCell" owner:self options:nil];
    MainMenuItemCell* instance = elements[0];
    instance.menuItem = menuItem;
    instance.reuseIdentifier = reuseIdentifier;
    instance.selectionStyle = UITableViewCellSelectionStyleDefault;
    instance.titleLabel.textColor = [PCValues textColor1];
    return instance;
}

- (void)awakeFromNib {
    self.contentView.userInteractionEnabled = YES;
}

+ (CGFloat)height {
    return 55.0;
}

- (void)setEyeButtonState:(EyeButtonState)state {
    switch (state) {
        case EyeButtonStateButtonHidden:
            self.eyeButton.alpha = 0.0;
            break;
        case EyeButtonStateDataHidden:
            self.eyeButton.alpha = 0.5;
            [self.eyeButton setImage:[UIImage imageNamed:@"CheckmarkRoundedSquareEmpty"] forState:UIControlStateNormal];
            break;
        case EyeButtonStateDataVisible:
            self.eyeButton.alpha = 1.0;
            [self.eyeButton setImage:[UIImage imageNamed:@"CheckmarkRoundedSquare"] forState:UIControlStateNormal];
            break;
        default:
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"unsupported EyeButtonState" userInfo:nil];
            break;
    }
}

- (void)setMenuItem:(MainMenuItem *)menuItem {
    [PCUtils throwExceptionIfObject:menuItem notKindOfClass:[MainMenuItem class]];
    _menuItem = menuItem;
    self.titleLabel.text = menuItem.title;
    self.subtitleLabel.text = menuItem.subtitle;
    self.leftImageView.image = menuItem.leftImage;
}

- (IBAction)eyeButtonPressed {
    if ([self.eyeButtonDelegate respondsToSelector:@selector(eyeButtonPressedForMenuItemCell:)]) {
        [self.eyeButtonDelegate eyeButtonPressedForMenuItemCell:self];
    }
}

- (void)setEditing:(BOOL)editing animated:(BOOL)animated {
    [super setEditing:editing animated:animated];
    self.eyeButton.hidden = !editing;
    self.subtitleLabel.hidden = editing;
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    self.leftImageView.image = highlighted && self.menuItem.highlightedLeftImage ? self.menuItem.highlightedLeftImage : self.menuItem.leftImage;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    self.leftImageView.image = selected && self.menuItem.highlightedLeftImage ? self.menuItem.highlightedLeftImage : self.menuItem.leftImage;
}

@end
