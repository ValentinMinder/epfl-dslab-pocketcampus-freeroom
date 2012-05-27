//
//  TransportSettingsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 27.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportSettingsViewController.h"

#import "PCValues.h"

#import "TransportController.h"

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
    self.title = NSLocalizedStringFromTable(@"TransportSettings", @"TransportPlugin", nil);
    tableView.backgroundColor = [PCValues backgroundColor1];
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneBarButtonPressed)] autorelease];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
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

/* UITableViewDelegate delegation */

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
        default:
            return @"";
            break;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell;
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
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0: // best result cells setting
            return 1;
            break;
        default:
            return 0; //should not happen
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
