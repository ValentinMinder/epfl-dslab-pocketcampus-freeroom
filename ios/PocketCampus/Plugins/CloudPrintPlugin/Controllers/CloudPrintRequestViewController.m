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

//  Created by Loïc Gardiol on 02.09.14.

#import "CloudPrintRequestViewController.h"

static NSInteger const kSection0Index = 0;
static NSInteger const kSection1Index = 1;
static NSInteger const kSection2Index = 2;

static NSInteger const kNbCopiesRowIndex = 0;
static NSInteger const kPagesRangeRowIndex = 1;
static NSInteger const kPageFromRowIndex = 2;
static NSInteger const kPageToRowIndex = 3;

static NSInteger const kAllPagesSegmentIndex = 0;
static NSInteger const kSelectedPagesSegmentIndex = 1;

static NSInteger const kDoubleSidedRowIndex = 0;
static NSInteger const kPagesPerSheetRowIndex = 1;

static NSInteger const kBlackAndWhiteRowIndex = 0;


@interface CloudPrintRequestViewController ()

@property (nonatomic, strong) PrintDocumentRequest* printRequest;

@property (nonatomic, strong) UIStepper* nbCopiesStepper;
@property (nonatomic, strong) UISegmentedControl* pageRangeSegmentedControl;
@property (nonatomic, strong) UIStepper* pageFromStepper;
@property (nonatomic, strong) UIStepper * pageToStepper;
@property (nonatomic, strong) UISwitch* doubleSidedToggle;
@property (nonatomic, strong) UIActionSheet* pagesPerSheetActionSheet;
@property (nonatomic, strong) UIActionSheet* multiPagesLayoutActionSheet;

@end

@implementation CloudPrintRequestViewController

#pragma mark - Init

- (instancetype)initWithDocumentName:(NSString*)docName printRequestOrNil:(PrintDocumentRequest*)printRequestOrNil {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        if (printRequestOrNil) {
            self.printRequest = printRequestOrNil;
        } else {
            self.printRequest = [PrintDocumentRequest new];
            self.printRequest.doubleSided = YES; // Default
            self.printRequest.blackAndWhite = YES; // Default
            self.printRequest.numberOfCopies = 1; // Default
        }
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
}

#pragma mark - Actions

- (void)valueChanged:(id)sender {
    
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PCTableViewCellAdditions* cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
    switch (indexPath.section) {
        case kSection0Index:
            switch (indexPath.row) {
                case kNbCopiesRowIndex:
                    cell.textLabel.text = self.printRequest.numberOfCopies == 1 ? NSLocalizedStringFromTable(@"1Copy", @"CloudPrintPlugin", nil) : [NSString stringWithFormat:NSLocalizedStringFromTable(@"NbCopiesWithFormat", @"CloudPrintPlugin", nil), self.printRequest.numberOfCopies];
                    if (!self.nbCopiesStepper) {
                        self.nbCopiesStepper = [UIStepper new];
                        self.nbCopiesStepper.stepValue = 1.0;
                        self.nbCopiesStepper.minimumValue = 1;
                        self.nbCopiesStepper.maximumValue = 1000;
                        [self.nbCopiesStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.nbCopiesStepper.value = self.printRequest.numberOfCopies;
                    cell.accessoryView = self.nbCopiesStepper;
                    break;
                case kPagesRangeRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Pages", @"CloudPrintPlugin", nil);
                    if (!self.pageRangeSegmentedControl) {
                        self.pageRangeSegmentedControl = [[UISegmentedControl alloc] initWithItems:@[NSLocalizedStringFromTable(@"All", @"CloudPrintPlugin", nil), NSLocalizedStringFromTable(@"Selected", @"CloudPrintPlugin", nil)]];
                        [self.pageRangeSegmentedControl addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageRangeSegmentedControl.selectedSegmentIndex = self.printRequest.pageSelection ? kAllPagesSegmentIndex : kSelectedPagesSegmentIndex;
                    cell.accessoryView = self.pageRangeSegmentedControl;
                    break;
                case kPageFromRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"From", @"CloudPrintPlugin", nil);
                    if (!self.pageFromStepper) {
                        self.pageFromStepper = [UIStepper new];
                        self.pageFromStepper.stepValue = 1.0;
                        self.pageFromStepper.minimumValue = 1;
                        self.pageFromStepper.maximumValue = 1000;
                        [self.pageFromStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageFromStepper.value = (double)(self.printRequest.pageSelection.pageFrom);
                    cell.accessoryView = self.pageFromStepper;
                    break;
                case kPageToRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"To", @"CloudPrintPlugin", nil);
                    if (!self.pageToStepper) {
                        self.pageToStepper = [UIStepper new];
                        self.pageToStepper.stepValue = 1.0;
                        self.pageToStepper.minimumValue = 1;
                        self.pageToStepper.maximumValue = 1000;
                        [self.pageToStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageToStepper.value = (double)(self.printRequest.pageSelection.pageTo);
                    cell.accessoryView = self.pageToStepper;
                    break;
                default:
                    break;
            }
            break;
        case kSection1Index:
            switch (indexPath.row) {
                case kDoubleSidedRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"DoubleSided", @"CloudPrintPlugin", nil);
                    if (!self.doubleSidedToggle) {
                        self.doubleSidedToggle = [UISwitch new];
                    }
                    self.doubleSidedToggle.on = self.printRequest.doubleSided;
                    cell.accessoryView = self.doubleSidedToggle;
                    break;
                case kPagesPerSheetRowIndex:
                    break;
                default:
                    break;
            }
            break;
        case kSection2Index:
            switch (indexPath.row) {
                case kBlackAndWhiteRowIndex:
                    
                    break;
                default:
                    break;
            }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kSection0Index:
            return self.printRequest.pageSelection ? 4 : 2;
        case kSection1Index:
            return 2;
        case kSection2Index:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

@end
