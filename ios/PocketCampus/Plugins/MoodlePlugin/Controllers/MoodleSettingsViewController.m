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

//  Created by Loïc Gardiol on 14.04.14.

#import "MoodleSettingsViewController.h"

#import "MoodleModelAdditions.h"

#import "MoodleService.h"

static NSUInteger const kGeneralSection = 0;

static NSString* const kKeepDocsPositionGeneralSettingBoolKey = @"KeepDocsPositionGeneralSettingBool";

@interface MoodleSettingsViewController ()

@property (nonatomic, strong) MoodleResource* moodleResource;

@property (nonatomic) BOOL saveDocsPositionGeneralSetting;
@property (nonatomic) BOOL savePositionResourceSetting;

@end

@implementation MoodleSettingsViewController

#pragma mark - Init

- (instancetype)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
        self.gaiScreenName = @"/moodle/settings";
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismiss)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

#pragma mark - Actions

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - Properties

- (BOOL)saveDocsPositionGeneralSetting {
    return [[[PCPersistenceManager defaultsForPluginName:@"moodle"] objectForKey:kMoodleSaveDocsPositionGeneralSettingBoolKey] boolValue];
}

- (void)setSaveDocsPositionGeneralSetting:(BOOL)saveDocsPositionGeneralSetting {
    [[PCPersistenceManager defaultsForPluginName:@"moodle"] setObject:[NSNumber numberWithBool:saveDocsPositionGeneralSetting] forKey:kMoodleSaveDocsPositionGeneralSettingBoolKey];
}

#pragma mark - UITableViewDelegate

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kGeneralSection:
            return NSLocalizedStringFromTable(@"General", @"PocketCampus", nil);
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        case kGeneralSection:
            return NSLocalizedStringFromTable(@"KeepDocsPositionExplanation", @"MoodlePlugin", nil);
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kGeneralSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.textLabel.text = NSLocalizedStringFromTable(@"KeepDocsPosition", @"MoodlePlugin", nil);
            UISwitch* toggle = [UISwitch new];
            __weak __typeof(self) welf = self;
            __weak __typeof(toggle) woggle = toggle;
            [toggle addEventHandler:^(id sender, UIEvent *event) {
                welf.saveDocsPositionGeneralSetting = woggle.isOn;
                [welf trackAction:@"SaveDocsPositionGeneralSetting" contentInfo:woggle.isOn ? @"Yes" : @"No"];
            } forControlEvent:UIControlEventValueChanged];
            toggle.on = self.saveDocsPositionGeneralSetting;
            cell.accessoryView = toggle;
            break;
        }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kGeneralSection:
            return 1; //keep docs position in general
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

@end
