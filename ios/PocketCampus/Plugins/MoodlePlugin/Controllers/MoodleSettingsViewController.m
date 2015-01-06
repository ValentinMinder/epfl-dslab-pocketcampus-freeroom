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

static NSUInteger const kReadingSection = 0;
static NSUInteger const kFilesSection = 1;

static NSString* const kKeepDocsPositionGeneralSettingBoolKey = @"KeepDocsPositionGeneralSettingBool";

@interface MoodleSettingsViewController ()<UIActionSheetDelegate>

@property (nonatomic) BOOL saveDocsPositionGeneralSetting;

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic) long long tmpTotalNbResourcesSize; //-2 when should compute, -1 when computing, LLONG_MAX on error
@property (nonatomic, strong) UIActionSheet* deleteAllDocsActionSheet;

@end

@implementation MoodleSettingsViewController

#pragma mark - Init

- (instancetype)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MoodleSettings", @"MoodlePlugin", nil);
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.gaiScreenName = @"/moodle/settings";
        self.tmpTotalNbResourcesSize = -2;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    if (![PCUtils isIdiomPad]) {
        //in popover controller on iPad => no need for OK button
        
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismiss)];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.tmpTotalNbResourcesSize = -2;
    [self.tableView reloadData];
    [self trackScreen];
}

#pragma mark - Actions

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - Properties

- (BOOL)saveDocsPositionGeneralSetting {
    return [[[PCPersistenceManager userDefaultsForPluginName:@"moodle"] objectForKey:kMoodleSaveDocsPositionGeneralSettingBoolKey] boolValue];
}

- (void)setSaveDocsPositionGeneralSetting:(BOOL)saveDocsPositionGeneralSetting {
    [[PCPersistenceManager userDefaultsForPluginName:@"moodle"] setObject:[NSNumber numberWithBool:saveDocsPositionGeneralSetting] forKey:kMoodleSaveDocsPositionGeneralSettingBoolKey];
}

#pragma mark - UITableViewDelegate

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kReadingSection:
            return NSLocalizedStringFromTable(@"General", @"PocketCampus", nil);
        case kFilesSection:
            return nil;
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        case kReadingSection:
            return NSLocalizedStringFromTable(@"KeepDocsPositionExplanation", @"MoodlePlugin", nil);
        case kFilesSection:
        {
            if (self.tmpTotalNbResourcesSize == -2) {
                self.tmpTotalNbResourcesSize = -1;
                __weak __typeof(self) welf = self;
                [self.moodleService totalNbBytesAllDownloadedMoodleFilesWithCompletion:^(unsigned long long totalNbBytes, BOOL error) {
                    if (!welf) {
                        return;
                    }
                    if (error) {
                        welf.tmpTotalNbResourcesSize = LLONG_MAX;
                    } else {
                        welf.tmpTotalNbResourcesSize = (long long)totalNbBytes;
                    }
                    [welf.tableView reloadData];
                }];
            }
            
            NSString* fileSizeString = nil;
            if (self.tmpTotalNbResourcesSize == -1) {
                fileSizeString = [NSString stringWithFormat:@"(%@)", [NSLocalizedStringFromTable(@"Computing", @"MoodlePlugin", nil) lowercaseString]];
            } else if (self.tmpTotalNbResourcesSize == LLONG_MAX) {
                fileSizeString = [NSString stringWithFormat:@"(%@)", [NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) lowercaseString]];
            } else if (self.tmpTotalNbResourcesSize >= 0) {
                if (self.tmpTotalNbResourcesSize == 0) {
                    fileSizeString = nil;
                } else {
                    fileSizeString = [NSByteCountFormatter stringFromByteCount:self.tmpTotalNbResourcesSize countStyle:NSByteCountFormatterCountStyleFile];
                    fileSizeString = [fileSizeString stringByReplacingOccurrencesOfString:@" " withString:@"\u00A0"]; //replace space with unbreakable space, so that it does not happen that quantity is on one line while unit is on another.
                }
            }
            if (fileSizeString) {
                return [NSString stringWithFormat:NSLocalizedStringFromTable(@"DownloadedDocumentsCurrentlyUsingBytesWithFormat", @"MoodlePlugin", nil), fileSizeString];
            } else {
                return NSLocalizedStringFromTable(@"NoDownloadedDocuments", @"MoodlePlugin", nil);
            }
            
        }
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kReadingSection:
            [tableView deselectRowAtIndexPath:indexPath animated:NO];
            break;
        case kFilesSection:
            if (self.tmpTotalNbResourcesSize <= 0) {
                [tableView deselectRowAtIndexPath:indexPath animated:NO];
                return;
            }
            self.deleteAllDocsActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"AllDocsWillBeDeletedExplanation", @"MoodlePlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:NSLocalizedStringFromTable(@"DeleteAll", @"MoodlePlugin", nil) otherButtonTitles:nil];
            [self.deleteAllDocsActionSheet showInView:self.tableView];
            break;
        default:
            break;
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kReadingSection:
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
        case kFilesSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.adjustsFontSizeToFitWidth = YES;
            cell.textLabel.textColor = [PCValues pocketCampusRed];
            cell.textLabel.text = NSLocalizedStringFromTable(@"DeleteAllDownloadedDocuments", @"MoodlePlugin", nil);
            if (self.tmpTotalNbResourcesSize > 0) {
                cell.textLabel.enabled = YES;
                cell.selectionStyle = UITableViewCellSelectionStyleDefault;
            } else {
                cell.textLabel.enabled = NO;
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
            }
            break;
        }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kReadingSection:
            return 1; //keep docs position in general
        case kFilesSection:
            return 1; //delete all files
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.deleteAllDocsActionSheet) {
        if (buttonIndex == actionSheet.destructiveButtonIndex) {
            [self trackAction:@"DeleteAllDownloadedDocuments"];
            [[MoodleService sharedInstanceToRetain] deleteAllDownloadedMoodleFiles];
            self.tmpTotalNbResourcesSize = -2;
            [self.tableView reloadData];
        }
        self.deleteAllDocsActionSheet = nil;
        [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.moodleService cancelDownloadOfMoodleFilesForDelegate:self];
}

@end
