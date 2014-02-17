//
//  PCUsageViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

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
