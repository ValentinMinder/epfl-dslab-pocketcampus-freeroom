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

#import "CloudPrintModelAdditions.h"

#import "CloudPrintController.h"

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
static NSInteger const kNbPagesPerSheetRowIndex = 1;
static NSInteger const kMultiPageLayoutRowIndex = 2;

static NSInteger const kBlackAndWhiteRowIndex = 0;


@interface CloudPrintRequestViewController ()

@property (nonatomic, strong) NSString* documentName;
@property (nonatomic, strong) PrintDocumentRequest* printRequest;

@property (nonatomic, strong) UIStepper* nbCopiesStepper;
@property (nonatomic, strong) UISegmentedControl* pageRangeSegmentedControl;
@property (nonatomic, strong) UIStepper* pageFromStepper;
@property (nonatomic, strong) UIStepper * pageToStepper;
@property (nonatomic, strong) UISwitch* doubleSidedToggle;
@property (nonatomic, strong) UIActionSheet* pagesPerSheetActionSheet;
@property (nonatomic, strong) UIActionSheet* multiPagesLayoutActionSheet;
@property (nonatomic, strong) UISwitch* blackAndWhiteToggle;

@end

@implementation CloudPrintRequestViewController

#pragma mark - Init

- (instancetype)initWithDocumentName:(NSString*)docName printRequest:(PrintDocumentRequest*)printRequest {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.title = [CloudPrintController localizedName];
        self.documentName = docName;
        self.printRequest = printRequest ?: [PrintDocumentRequest createDefaultRequest];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] initWithFrame:self.tableView.frame style:self.tableView.style];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault]);
    };
    if (self.documentName) {
        UITableViewHeaderFooterView* header = [[UITableViewHeaderFooterView alloc] init];
        header.textLabel.text = self.documentName;
        header.textLabel.textAlignment = NSTextAlignmentCenter;
        header.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
        self.tableView.tableHeaderView = header;
    }
}

#pragma mark - Actions

/*
 
 @property (nonatomic, strong) UIStepper* nbCopiesStepper;
 @property (nonatomic, strong) UISegmentedControl* pageRangeSegmentedControl;
 @property (nonatomic, strong) UIStepper* pageFromStepper;
 @property (nonatomic, strong) UIStepper * pageToStepper;
 @property (nonatomic, strong) UISwitch* doubleSidedToggle;
 @property (nonatomic, strong) UIActionSheet* pagesPerSheetActionSheet;
 @property (nonatomic, strong) UIActionSheet* multiPagesLayoutActionSheet;
 @property (nonatomic, strong) UISwitch* blackAndWhiteToggle;
 
 */

- (void)valueChanged:(id)sender {
    if (sender == self.nbCopiesStepper) {
        self.printRequest.numberOfCopies = (int)(self.nbCopiesStepper.value);
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kNbCopiesRowIndex inSection:kSection0Index]] withRowAnimation:UITableViewRowAnimationNone];
    } else if (sender == self.pageRangeSegmentedControl) {
        switch (self.pageRangeSegmentedControl.selectedSegmentIndex) {
            case kAllPagesSegmentIndex:
                self.printRequest.pageSelection = nil;
                break;
            case kSelectedPagesSegmentIndex:
                if (!self.printRequest.pageSelection) {
                    self.printRequest.pageSelection = [[CloudPrintPageRange alloc] initWithPageFrom:1 pageTo:2];
                }
                break;
            default:
                break;
        }
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kSection0Index] withRowAnimation:UITableViewRowAnimationAutomatic];
    } else if (sender == self.pageFromStepper || sender == self.pageToStepper) {
        self.printRequest.pageSelection.pageFrom = (int)(self.pageFromStepper.value);
        self.printRequest.pageSelection.pageTo = (int)(self.pageToStepper.value);
        if (self.printRequest.pageSelection.pageFrom >= self.printRequest.pageSelection.pageTo) {
            self.printRequest.pageSelection.pageTo = self.printRequest.pageSelection.pageFrom;
        }
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kPageFromRowIndex inSection:kSection0Index], [NSIndexPath indexPathForRow:kPageToRowIndex inSection:kSection0Index]] withRowAnimation:UITableViewRowAnimationNone];
    } else if (sender == self.doubleSidedToggle) {
        self.printRequest.doubleSided = self.doubleSidedToggle.isOn;
    } else if (sender == self.blackAndWhiteToggle) {
        self.printRequest.blackAndWhite = self.blackAndWhiteToggle.isOn;
    }
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
#warning TODO
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (section == kSection2Index) {
        return NSLocalizedStringFromTable(@"BlackAndWhiteExplanations", @"CloudPrintPlugin", nil);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* identifier = [NSString stringWithFormat:@"%d-%d", indexPath.section, indexPath.row];
    PCTableViewCellAdditions* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
    }
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
                        self.nbCopiesStepper.value = self.printRequest.numberOfCopies;
                        [self.nbCopiesStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    cell.accessoryView = self.nbCopiesStepper;
                    break;
                case kPagesRangeRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Pages", @"CloudPrintPlugin", nil);
                    if (!self.pageRangeSegmentedControl) {
                        self.pageRangeSegmentedControl = [[UISegmentedControl alloc] initWithItems:@[NSLocalizedStringFromTable(@"All", @"CloudPrintPlugin", nil), NSLocalizedStringFromTable(@"Selected", @"CloudPrintPlugin", nil)]];
                        self.pageRangeSegmentedControl.bounds = CGRectMake(0, 0, 180.0, self.pageRangeSegmentedControl.bounds.size.height);
                        [self.pageRangeSegmentedControl addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageRangeSegmentedControl.selectedSegmentIndex = self.printRequest.pageSelection ? kSelectedPagesSegmentIndex : kAllPagesSegmentIndex;
                    cell.accessoryView = self.pageRangeSegmentedControl;
                    break;
                case kPageFromRowIndex:
                    cell.textLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FromPageWithFormat", @"CloudPrintPlugin", nil), self.printRequest.pageSelection.pageFrom];
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
                    cell.textLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"ToPageWithFormat", @"CloudPrintPlugin", nil), self.printRequest.pageSelection.pageTo];
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
                        [self.doubleSidedToggle addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.doubleSidedToggle.on = self.printRequest.doubleSided;
                    cell.accessoryView = self.doubleSidedToggle;
                    break;
                case kNbPagesPerSheetRowIndex:
                    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"PagesPerSheet", @"CloudPrintPlugin", nil);
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", self.printRequest.multiPageConfig.nbPagesPerSheet];
                    break;
                case kMultiPageLayoutRowIndex:
                    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"LayoutDirection", @"CloudPrintPlugin", nil);
                    cell.detailTextLabel.text = [CloudPrintModelAdditions localizedTitleForForMultiPageLayout:self.printRequest.multiPageConfig.layout];
                    break;
                default:
                    break;
            }
            break;
        case kSection2Index:
            switch (indexPath.row) {
                case kBlackAndWhiteRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"BlackAndWhite", @"CloudPrintPlugin", nil);
                    if (!self.blackAndWhiteToggle) {
                        self.blackAndWhiteToggle = [UISwitch new];
                        [self.blackAndWhiteToggle addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.blackAndWhiteToggle.on = self.printRequest.blackAndWhite;
                    cell.accessoryView = self.blackAndWhiteToggle;
                    break;
                default:
                    break;
            }
            break;
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
            return self.printRequest.multiPageConfig ? 3 : 2;
        case kSection2Index:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

@end
