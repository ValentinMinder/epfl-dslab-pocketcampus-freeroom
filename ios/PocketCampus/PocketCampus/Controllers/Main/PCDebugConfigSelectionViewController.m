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

//  Created by Loïc Gardiol on 24.03.15.

#import "PCDebugConfigSelectionViewController.h"

static NSInteger const kBundleBoolRow = 0;
static NSInteger const kPersistedServerBoolRow = 1;
static NSInteger const kServerBoolRow = 2;
static NSInteger const kAppSupportBoolRow = 3;

static NSInteger const kServerAddressRow = 4;
static NSInteger const kProtocolRow = 5;
static NSInteger const kPortRow = 6;
static NSInteger const kURIRow = 7;

static NSInteger const kCurrentConfigSection = 0;
static NSInteger const kConfigsListSection = 1;

@interface PCDebugConfigSelectionViewController ()

@property (nonatomic, copy) NSArray* configPaths;

@end

@implementation PCDebugConfigSelectionViewController

#pragma mark - Init

- (instancetype)init {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        //Nothing
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
#ifdef DEBUG
    self.configPaths = [PCConfig bundledDebugConfigsPaths];
#endif
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
#ifdef DEBUG
    switch (indexPath.section) {
        case kConfigsListSection:
        {
            if (indexPath.row == 0) {

                [PCConfig applyAndDieConfigWithPath:nil];
            } else {
                NSString* configPath = self.configPaths[indexPath.row-1];
                [PCConfig applyAndDieConfigWithPath:configPath];
            }
            break;
        }
        default:
            
            break;
    }
#endif
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        case kConfigsListSection:
            return @"Selecting a config applies it and crashes the app.\nThe new config is loaded at next launch.";
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kCurrentConfigSection:
        {
            UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            switch (indexPath.row) {
                case kBundleBoolRow:
                    cell.textLabel.text = @"Bundle";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY]];
                    break;
                case kPersistedServerBoolRow:
                    cell.textLabel.text = @"Persisted";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG_KEY]];
                    break;
                case kServerBoolRow:
                    cell.textLabel.text = @"Server";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_SERVER_KEY]];
                    break;
                case kAppSupportBoolRow:
                    cell.textLabel.text = @"AppSupport";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", [[PCConfig defaults] boolForKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT]];
                    break;
                case kServerAddressRow:
                    cell.textLabel.text = @"Server address";
                    cell.detailTextLabel.text = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
                    break;
                case kProtocolRow:
                    cell.textLabel.text = @"Protocol";
                    cell.detailTextLabel.text = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
                    break;
                case kPortRow:
                    cell.textLabel.text = @"Port";
                    cell.detailTextLabel.text = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PORT_KEY];
                    break;
                case kURIRow:
                    cell.textLabel.text = @"URI";
                    cell.detailTextLabel.text = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_URI_KEY];
                    break;
            }
            return cell;
        }
        case kConfigsListSection:
        {
            UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.textAlignment = NSTextAlignmentCenter;
            cell.textLabel.textColor = [PCValues pocketCampusRed];
            if (indexPath.row == 0) {
                cell.textLabel.text = @"Delete custom config";
            } else {
                NSString* configPath = self.configPaths[indexPath.row-1];
                cell.textLabel.text = [configPath lastPathComponent];
            }
            return cell;
        }
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kCurrentConfigSection:
            return 8;
        case kConfigsListSection:
            return self.configPaths.count+1; //available configs + 1 for no config
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}


@end
