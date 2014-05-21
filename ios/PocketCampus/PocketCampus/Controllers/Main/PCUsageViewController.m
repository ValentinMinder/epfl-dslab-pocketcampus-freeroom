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

//  Created by Loïc Gardiol on 16.01.14.

#import "PCUsageViewController.h"

static int const kCrashlyticsEnabledSection = 0;

@interface PCUsageViewController ()

@end

@implementation PCUsageViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/dashboard/settings/usage";
        self.title = NSLocalizedStringFromTable(@"Usage", @"PocketCampus", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

#pragma mark - Values observing

- (void)crashlyticsEnabledSwitchValueChanged:(UISwitch*)toggle {
    [self trackAction:@"SendCrashReportsAutomatically" contentInfo:toggle.isOn ? @"YES" : @"NO"];
    [[PCConfig defaults] setBool:toggle.isOn forKey:PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY];
}

#pragma mark - UITableViewDelegate

/*- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}*/

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kCrashlyticsEnabledSection:
            return NSLocalizedStringFromTable(@"CrashReports", @"PocketCampus", nil);
            
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        case kCrashlyticsEnabledSection:
            return NSLocalizedStringFromTable(@"CrashReportsExplanations", @"PocketCampus", nil);

    }
    return nil;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kCrashlyticsEnabledSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.text = NSLocalizedStringFromTable(@"SendAutomatically", @"PocketCampus", nil);
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            UISwitch* toggle = [UISwitch new];
            toggle.on = [[PCConfig defaults] boolForKey:PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY];
            [toggle addTarget:self action:@selector(crashlyticsEnabledSwitchValueChanged:) forControlEvents:UIControlEventValueChanged];
            cell.accessoryView = toggle;
            break;
        }
            
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kCrashlyticsEnabledSection:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
