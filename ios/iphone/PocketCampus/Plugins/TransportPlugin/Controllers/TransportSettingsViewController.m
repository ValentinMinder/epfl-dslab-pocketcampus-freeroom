//
//  TransportSettingsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 27.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "TransportSettingsViewController.h"

#import "PCValues.h"

#import "TransportController.h"

#import "TransportService.h"

@implementation TransportSettingsViewController

static int kBestResultSwitchTag = 2;

@synthesize tableView;

- (id)init
{
    self = [super initWithNibName:@"TransportSettingsView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/transport/settings" withError:NULL];
    self.title = NSLocalizedStringFromTable(@"TransportSettings", @"TransportPlugin", nil);
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(doneBarButtonPressed)];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}


- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)doneBarButtonPressed {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
}

/* Setting changes delegation */

- (void)settingChanged:(id)sender {
    if ([sender isKindOfClass:[UIView class]]) {
        int tag = [(UIView*)sender tag];
        if (tag == kBestResultSwitchTag) {
            [TransportController saveObjectSetting:[NSNumber numberWithBool:[(UISwitch*)sender isOn]] forKey:kTransportSettingsKeyBestResult];
        } else {
            NSLog(@"!! Unknown sender UIView tag");
        }
    }
}

/* UIAlertViewDelegate delegation */

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 1) { //OK
        TransportService* transportService = [TransportService sharedInstanceToRetain];
        [transportService saveUserFavoriteTransportStations:nil];
        [transportService saveUserManualDepartureStation:nil];
        UITableViewCell* cell = [tableView cellForRowAtIndexPath:[tableView indexPathForSelectedRow]];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.enabled = NO;
    }
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:YES];
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:[tableView indexPathForSelectedRow]];
    if (!cell.textLabel.enabled) {
        return;
    }
    switch (indexPath.section) {
        case 1:
        {
            UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Reset", @"TransportPlugin", nil) message:NSLocalizedStringFromTable(@"ResetFavStationsExplanations", @"TransportPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:@"OK", nil];
            [alertView show];
            [alertView release];
            break;
        }
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 0) {
        NSString* explanation = NSLocalizedStringFromTable(@"BestResultsExplanation", @"TransportPlugin", nil);
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [explanation sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        return reqSize.height-30.0;
    }
    return 0.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if(section == 0) {
        UILabel* label = [[UILabel alloc] init];
        NSString* explanation = NSLocalizedStringFromTable(@"BestResultsExplanation", @"TransportPlugin", nil);
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [explanation sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        
        label.frame = CGRectMake(0, 0, 260.0, reqSize.height);
        label.numberOfLines = 5;
        label.textAlignment = UITextAlignmentCenter;
        label.backgroundColor = [UIColor clearColor];
        label.font = font;
        label.textColor = [PCValues textColor1];
        label.shadowOffset = [PCValues shadowOffset1];
        label.shadowColor = [UIColor whiteColor];
        label.adjustsFontSizeToFitWidth = NO;
        label.text = explanation;
        return [label autorelease];
    }
    
    return nil;
}

/* UITableViewDataSource delegation */

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return NSLocalizedStringFromTable(@"Schedules", @"TransportPlugin", nil);
            break;
        case 1:
            return NSLocalizedStringFromTable(@"Reset", @"TransportPlugin", nil);
            break;
        default:
            return @"";
            break;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case 0: // best result cells setting
        {
            cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"BestResults", @"TransportPlugin", nil);
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            
            NSNumber* switchNumber = (NSNumber*)[TransportController objectSettingForKey:kTransportSettingsKeyBestResult];
            BOOL switchVal = YES; //default settings
            if (switchNumber != nil) { 
                switchVal = [switchNumber boolValue];
            }
            UISwitch* switchView = [[UISwitch alloc] init];
            switchView.tag = kBestResultSwitchTag;
            [switchView setOn:switchVal];
            [switchView addTarget:self action:@selector(settingChanged:) forControlEvents:UIControlEventValueChanged];
            cell.accessoryView = switchView;
            [switchView release];
            break;
        }
        case 1: //restore stations section
        {
            cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"ResetFavStations", @"TransportPlugin", nil);
            cell.textLabel.textAlignment = UITextAlignmentCenter;
            break;
        }
        default:
            cell = nil;
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0: // schedules section
            return 1;
            break;
        case 1: //restore default fav stations
            return 1;
            break;
        default:
            return 0; //should not happen
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

@end
