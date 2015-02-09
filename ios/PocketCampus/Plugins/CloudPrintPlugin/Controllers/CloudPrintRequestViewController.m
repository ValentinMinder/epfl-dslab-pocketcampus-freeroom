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

#import "CloudPrintExtensionInfoViewController.h"

#import "CloudPrintExtensionInfoCell.h"

#import "CloudPrintPOOL1InfoViewController.h"

#import "CloudPrintMultiPageLayoutCell.h"

#import "CloudPrintPreviewViewController.h"

static NSInteger const kPrinterInfoSectionIndex = 0;
static NSInteger const kCopiesAndRangeSectionIndex = 1;
static NSInteger const kOrientationSectionIndex = 2;
static NSInteger const kDoubleSidedSectionIndex = 3;
static NSInteger const kMultiPageSectionIndex = 4;
static NSInteger const kColorSectionIndex = 5;

static NSInteger const kNbCopiesRowIndex = 0;
static NSInteger const kPagesRangeRowIndex = 1;
static NSInteger const kPageFromRowIndex = 2;
static NSInteger const kPageToRowIndex = 3;

static NSInteger const kAllPagesSegmentIndex = 0;
static NSInteger const kSelectedPagesSegmentIndex = 1;

static NSInteger const kOrientationRowIndex = 0;

static NSInteger const kDoubleSidedRowIndex = 0;
static NSInteger const kDoubleSidedConfigRowIndex = 1;

static NSInteger const kNbPagesPerSheetRowIndex = 0;
static NSInteger const kMultiPageLayoutRowIndex = 1;

static NSInteger const kColorRowIndex = 0;

static NSInteger const kPageToTheEndValue = 10000;

@interface CloudPrintRequestViewController ()<UIActionSheetDelegate>

@property (nonatomic, strong) CloudPrintExtensionInfoCell* extensionInfoCell;
@property (nonatomic, strong) UIStepper* nbCopiesStepper;
@property (nonatomic, strong) UISegmentedControl* pageRangeSegmentedControl;
@property (nonatomic, strong) UIStepper* pageFromStepper;
@property (nonatomic, strong) UIButton* pageToChangeButton;
@property (nonatomic, strong) UIStepper * pageToStepper;
@property (nonatomic, strong) UISwitch* collateToggle;
@property (nonatomic, strong) UIActionSheet* orientationActionSheet;
@property (nonatomic, strong) UISwitch* doubleSidedToggle;
@property (nonatomic, strong) UIActionSheet* doubleSidedConfigActionSheet;
@property (nonatomic, strong) UIActionSheet* pagesPerSheetActionSheet;
@property (nonatomic, strong) UISwitch* colorToggle;

@end

@implementation CloudPrintRequestViewController

#pragma mark - Init

- (instancetype)init {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.title = @"EPFL Print";
        self.gaiScreenName = @"/cloudprint";
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelTapped)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Print", @"CloudPrintPlugin", nil) style:UIBarButtonItemStyleDone target:self action:@selector(printTapped)];
    
    UIBarButtonItem* previewButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"PrintPreview", @"CloudPrintPlugin", nil) style:UIBarButtonItemStylePlain target:self action:@selector(printPreviewTapped)];
    UIBarButtonItem* flexibleItem1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* flexibleItem2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    self.toolbarItems = @[flexibleItem1, previewButtonItem, flexibleItem2];
    
    /*PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] initWithFrame:self.tableView.frame style:self.tableView.style];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
#ifndef TARGET_IS_EXTENSION
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleValue1]);
#else
        return 44.0;
#endif
    };*/
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.navigationController setToolbarHidden:NO animated:NO];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationController setToolbarHidden:YES animated:NO];
}

#pragma mark - Public

- (void)setDocumentName:(NSString *)documentName {
    _documentName = [documentName copy];
    [self.tableView reloadData];
}

- (void)setPrintRequest:(PrintDocumentRequest *)printRequest {
    _printRequest = printRequest;
    [self.tableView reloadData];
}

#pragma mark - Actions

- (void)cancelTapped {
    if (self.userCancelledBlock) {
        [self trackAction:@"Cancel"];
        self.userCancelledBlock();
    }
}

- (void)printTapped {
    if (self.userValidatedRequestBlock) {
        [self trackAction:@"Print"];
        self.userValidatedRequestBlock(self.printRequest);
    }
}

- (void)printPreviewTapped {
    [self trackAction:@"PrintPreview"];
    CloudPrintPreviewViewController* viewController = [CloudPrintPreviewViewController new];
    viewController.printDocumentRequest = self.printRequest;
    __weak __typeof(self) welf = self;
    [viewController setCloseTappedBlock:^{
        [welf dismissViewControllerAnimated:YES completion:NULL];
    }];
    [viewController setPrintTappedBlock:^{
        welf.navigationItem.leftBarButtonItem.enabled = NO;
        welf.navigationItem.rightBarButtonItem.enabled = NO;
        [welf dismissViewControllerAnimated:YES completion:^{
            [welf printTapped];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                welf.navigationItem.leftBarButtonItem.enabled = YES;
                welf.navigationItem.rightBarButtonItem.enabled = YES;
            });
        }];
    }];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    navController.view.tintColor = self.view.tintColor; //if in extension
    navController.preferredContentSize = CGSizeZero; //so that it does not override preferredContentSize if viewController
    navController.modalPresentationStyle = UIModalPresentationCurrentContext; //present IN popover controller (on iPad)
    [self presentViewController:navController animated:YES completion:NULL];
}

- (void)valueChanged:(id)sender {
    if (sender == self.nbCopiesStepper) {
        if (!self.printRequest.multipleCopies) {
            self.printRequest.multipleCopies = [CloudPrintMultipleCopies new];
            self.printRequest.multipleCopies.collate = YES; //default, want [1,2,1,2]
        }
        self.printRequest.multipleCopies.numberOfCopies = (int)(self.nbCopiesStepper.value);
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kNbCopiesRowIndex inSection:kCopiesAndRangeSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
    } else if (sender == self.pageRangeSegmentedControl) {
        switch (self.pageRangeSegmentedControl.selectedSegmentIndex) {
            case kAllPagesSegmentIndex:
                self.printRequest.pageSelection = nil;
                break;
            case kSelectedPagesSegmentIndex:
                if (!self.printRequest.pageSelection) {
                    self.printRequest.pageSelection = [[CloudPrintPageRange alloc] initWithPageFrom:1 pageTo:kPageToTheEndValue];
                }
                self.pageToStepper.value = (double)kPageToTheEndValue;
                break;
            default:
                break;
        }
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kCopiesAndRangeSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
    } else if (sender == self.pageFromStepper) {
        self.printRequest.pageSelection.pageFrom = (int)(self.pageFromStepper.value);
        self.printRequest.pageSelection.pageTo = (int)(self.pageToStepper.value);
        if (self.printRequest.pageSelection.pageFrom >= self.printRequest.pageSelection.pageTo) {
            self.printRequest.pageSelection.pageTo = self.printRequest.pageSelection.pageFrom;
        }
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kPageFromRowIndex inSection:kCopiesAndRangeSectionIndex], [NSIndexPath indexPathForRow:kPageToRowIndex inSection:kCopiesAndRangeSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
    } else if (sender == self.pageToStepper) {
        self.printRequest.pageSelection.pageFrom = (int)(self.pageFromStepper.value);
        self.printRequest.pageSelection.pageTo = (int)(self.pageToStepper.value);
        if (self.printRequest.pageSelection.pageTo <= self.printRequest.pageSelection.pageFrom) {
            self.printRequest.pageSelection.pageFrom = self.printRequest.pageSelection.pageTo;
        }
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kPageFromRowIndex inSection:kCopiesAndRangeSectionIndex], [NSIndexPath indexPathForRow:kPageToRowIndex inSection:kCopiesAndRangeSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
    } else if (sender == self.collateToggle) {
        if (!self.printRequest.multipleCopies) {
            self.printRequest.multipleCopies = [CloudPrintMultipleCopies new];
        }
        self.printRequest.multipleCopies.collate = self.collateToggle.isOn;
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kCopiesAndRangeSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
    } else if (sender == self.doubleSidedToggle) {
        self.printRequest.doubleSided = self.doubleSidedToggle.isOn ? CloudPrintDoubleSidedConfig_LONG_EDGE : 0;
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kDoubleSidedSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
    } else if (sender == self.colorToggle) {
        self.printRequest.colorConfig = self.colorToggle.isOn ? CloudPrintColorConfig_COLOR : CloudPrintColorConfig_BLACK_WHITE;
    }
}

- (void)pageToChangeTapped {
    self.printRequest.pageSelection.pageTo = self.printRequest.pageSelection.pageFrom;
    [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kPageToRowIndex inSection:kCopiesAndRangeSectionIndex]] withRowAnimation:UITableViewRowAnimationFade];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    NSString* buttonTitle = [actionSheet buttonTitleAtIndex:buttonIndex];
    if (actionSheet == self.doubleSidedConfigActionSheet) {
        if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_LONG_EDGE]]) {
            self.printRequest.doubleSided = CloudPrintDoubleSidedConfig_LONG_EDGE;
        } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_SHORT_EDGE]]) {
            self.printRequest.doubleSided = CloudPrintDoubleSidedConfig_SHORT_EDGE;
        }
        [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kDoubleSidedConfigRowIndex inSection:kDoubleSidedSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
        self.doubleSidedConfigActionSheet = nil;
    } else if (actionSheet == self.orientationActionSheet) {
        if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_PORTRAIT]]) {
            self.printRequest.orientation = CloudPrintOrientation_PORTRAIT;
        } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_LANDSCAPE]]) {
            self.printRequest.orientation = CloudPrintOrientation_LANDSCAPE;
        }
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kOrientationSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
        self.orientationActionSheet = nil;
    } else if (actionSheet == self.pagesPerSheetActionSheet) {
        if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:1]]) {
            self.printRequest.multiPageConfig = nil;
        } else if (buttonIndex != actionSheet.cancelButtonIndex) {
            if (!self.printRequest.multiPageConfig) {
                self.printRequest.multiPageConfig = [CloudPrintMultiPageConfig new];
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_TWO; // default
                self.printRequest.multiPageConfig.layout = CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM; // default
            }
            if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_TWO]]) {
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_TWO;
            } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_FOUR]]) {
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_FOUR;
            } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIX]]) {
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_SIX;
            } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_NINE]]) {
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_NINE;
            } else if ([buttonTitle isEqualToString:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIXTEEN]]) {
                self.printRequest.multiPageConfig.nbPagesPerSheet = CloudPrintNbPagesPerSheet_SIXTEEN;
            }
        }
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kMultiPageSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
        self.pagesPerSheetActionSheet = nil;
    }
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.section == kPrinterInfoSectionIndex && indexPath.row == [self extensionInfoCellRowIndex]) {
        return [self.extensionInfoCell preferredHeightInTableView:tableView];
    }
    
    if (indexPath.section == kMultiPageSectionIndex && indexPath.row == kMultiPageLayoutRowIndex) {
        return [CloudPrintMultiPageLayoutCell preferredHeight];
    }
/*#ifndef TARGET_IS_EXTENSION
    return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleValue1];
#else
    return 44.0;
#endif*/
    return 44.0;
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kPrinterInfoSectionIndex:
        {
            [self trackAction:@"Info"];
            CloudPrintPOOL1InfoViewController* viewController = [CloudPrintPOOL1InfoViewController new];
            [self.navigationController pushViewController:viewController animated:YES];
            break;
        }
        default:
            break;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    UIActionSheet* actionSheet = nil;
    UIAlertController* alertController = nil;
    __weak __typeof(self) welf = self;
    switch (indexPath.section) {
        case kPrinterInfoSectionIndex:
        {
            if (indexPath.row == [self extensionInfoCellRowIndex]) {
                [self trackAction:@"ViewExtensionInfo"];
                CloudPrintExtensionInfoViewController* viewController = [CloudPrintExtensionInfoViewController new];
                __weak __typeof(self) welf = self;
                [viewController setDoneButtonTapped:^{
                    [welf dismissViewControllerAnimated:YES completion:NULL];
                }];
                PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
                navController.preferredContentSize = CGSizeZero;
                navController.modalPresentationStyle = UIModalPresentationCurrentContext;
                [self presentViewController:navController animated:YES completion:NULL];
            }
            break;
        }
        case kOrientationSectionIndex:
            switch (indexPath.row) {
                case kOrientationRowIndex:
                    if ([UIAlertController class]) {
                        alertController = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"Orientation", @"CloudPrintPlugin", nil) message:nil preferredStyle:UIAlertControllerStyleActionSheet];
                        UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) style:UIAlertActionStyleCancel handler:NULL];
                        [alertController addAction:cancelAction];
                        UIAlertAction* portraitAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_PORTRAIT] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            welf.printRequest.orientation = CloudPrintOrientation_PORTRAIT;
                            [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kOrientationSectionIndex] withRowAnimation:UITableViewRowAnimationNone];
                        }];
                        [alertController addAction:portraitAction];
                        UIAlertAction* landscapeAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_LANDSCAPE] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            welf.printRequest.orientation = CloudPrintOrientation_LANDSCAPE;
                            [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kOrientationSectionIndex] withRowAnimation:UITableViewRowAnimationNone];
                        }];
                        [alertController addAction:landscapeAction];
                    } else {
#ifndef TARGET_IS_EXTENSION
                        actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"Orientation", @"CloudPrintPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:
                                       [CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_PORTRAIT],
                                       [CloudPrintModelAdditions localizedTitleForOrientation:CloudPrintOrientation_LANDSCAPE],
                                       nil];
                        self.orientationActionSheet = actionSheet;
#endif
                    }
                    break;
                default:
                    break;
            }
            break;
        case kDoubleSidedSectionIndex:
            switch (indexPath.row) {
                case kDoubleSidedConfigRowIndex:
                    if ([UIAlertController class]) {
                        alertController = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"FlipOn", @"CloudPrintPlugin", nil) message:nil preferredStyle:UIAlertControllerStyleActionSheet];
                        UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) style:UIAlertActionStyleCancel handler:NULL];
                        [alertController addAction:cancelAction];
                        UIAlertAction* longEdgeAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_LONG_EDGE] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            welf.printRequest.doubleSided = CloudPrintDoubleSidedConfig_LONG_EDGE;
                            [welf.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kDoubleSidedConfigRowIndex inSection:kDoubleSidedSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
                        }];
                        [alertController addAction:longEdgeAction];
                        UIAlertAction* shortEdgeAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_SHORT_EDGE] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            welf.printRequest.doubleSided = CloudPrintDoubleSidedConfig_SHORT_EDGE;
                            [welf.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:kDoubleSidedConfigRowIndex inSection:kDoubleSidedSectionIndex]] withRowAnimation:UITableViewRowAnimationNone];
                        }];
                        [alertController addAction:shortEdgeAction];
                    } else {
#ifndef TARGET_IS_EXTENSION
                        actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"FlipOn", @"CloudPrintPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:
                            [CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_LONG_EDGE],
                            [CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:CloudPrintDoubleSidedConfig_SHORT_EDGE],
                            nil];
                        self.doubleSidedConfigActionSheet = actionSheet;
#endif
                    }
                    break;
                default:
                    break;
            }
            break;
        case kMultiPageSectionIndex:
            switch (indexPath.row) {
                case kNbPagesPerSheetRowIndex:
                    if ([UIAlertController class]) {
                        alertController = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"PagesPerSheet", @"CloudPrintPlugin", nil) message:nil preferredStyle:UIAlertControllerStyleActionSheet];
                        UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) style:UIAlertActionStyleCancel handler:NULL];
                        [alertController addAction:cancelAction];
                        
                        void (^handleNbPagesPerSheet)(int) = ^void(int nbPagesPerSheet) {
                            if (nbPagesPerSheet == 1) {
                                welf.printRequest.multiPageConfig = nil;
                            } else {
                                if (!welf.printRequest.multiPageConfig) {
                                    welf.printRequest.multiPageConfig = [CloudPrintMultiPageConfig new];
                                    welf.printRequest.multiPageConfig.layout = CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM; // default
                                }
                                welf.printRequest.multiPageConfig.nbPagesPerSheet = nbPagesPerSheet; // default
                            }
                            [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kMultiPageSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
                        };
                        
                        UIAlertAction* oneAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:1] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(1);
                        }];
                        [alertController addAction:oneAction];
                        
                        UIAlertAction* twoAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_TWO] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(CloudPrintNbPagesPerSheet_TWO);
                        }];
                        [alertController addAction:twoAction];
                        UIAlertAction* fourAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_FOUR] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(CloudPrintNbPagesPerSheet_FOUR);
                        }];
                        [alertController addAction:fourAction];
                        UIAlertAction* sixAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIX] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(CloudPrintNbPagesPerSheet_SIX);
                        }];
                        [alertController addAction:sixAction];
                        UIAlertAction* nineAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_NINE] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(CloudPrintNbPagesPerSheet_NINE);
                        }];
                        [alertController addAction:nineAction];
                        UIAlertAction* sixteenAction = [UIAlertAction actionWithTitle:[CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIXTEEN] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                            handleNbPagesPerSheet(CloudPrintNbPagesPerSheet_SIXTEEN);
                        }];
                        [alertController addAction:sixteenAction];
                        
                    } else {
#ifndef TARGET_IS_EXTENSION
                        actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"PagesPerSheet", @"CloudPrintPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:1],
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_TWO],
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_FOUR],
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIX],
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_NINE],
                            [CloudPrintModelAdditions localizedTitleForNbPagesPerSheet:CloudPrintNbPagesPerSheet_SIXTEEN],
                            nil];
                        self.pagesPerSheetActionSheet = actionSheet;
#endif
                    }
                    break;
                default:
                    break;
            }
            break;
        default:
            break;
    }
    if (alertController) {
        [self presentViewController:alertController animated:YES completion:NULL];
    } else {
        actionSheet.delegate = self;
        [actionSheet showInView:self.view];
    }
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == kCopiesAndRangeSectionIndex && self.documentName) {
        return self.documentName;
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        /*case kCopiesAndRangeSectionIndex:
            if (self.printRequest.multipleCopies.numberOfCopies > 1 && self.printRequest.multipleCopies.collate) {
                return NSLocalizedStringFromTable(@"CollateExplanations", @"CloudPrintPlugin", nil);
            }
            break;*/
        case kColorSectionIndex:
            return NSLocalizedStringFromTable(@"ColorExplanations", @"CloudPrintPlugin", nil);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* identifier = [NSString stringWithFormat:@"%d-%d", indexPath.section, indexPath.row];
    PCTableViewCellAdditions* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell && !(indexPath.row == kMultiPageLayoutRowIndex && indexPath.section == kMultiPageSectionIndex)) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
    switch (indexPath.section) {
        case kPrinterInfoSectionIndex:
        {
            if (indexPath.row == [self printerCellRowIndex]) {
                cell.textLabel.text = NSLocalizedStringFromTable(@"Printer", @"CloudPrintPlugin", nil);
                cell.detailTextLabel.text = @"POOL1";
                cell.accessoryType = UITableViewCellAccessoryDetailButton;
            } else if (indexPath.row == [self extensionInfoCellRowIndex]) {
                CloudPrintExtensionInfoCell* infoCell = [CloudPrintExtensionInfoCell new];
                __weak __typeof(self) welf = self;
                [infoCell setCloseButtonTapped:^{
                    [welf saveExtensionInfoHidden:YES];
                    [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kPrinterInfoSectionIndex] withRowAnimation:UITableViewRowAnimationAutomatic];
                }];
                cell = infoCell;
            }
            break;
        }
        case kCopiesAndRangeSectionIndex:
            switch (indexPath.row) {
                case kNbCopiesRowIndex:
                    cell.textLabel.text = self.printRequest.multipleCopies.numberOfCopies > 1 ? [NSString stringWithFormat:NSLocalizedStringFromTable(@"NbCopiesWithFormat", @"CloudPrintPlugin", nil), self.printRequest.multipleCopies.numberOfCopies] : NSLocalizedStringFromTable(@"1Copy", @"CloudPrintPlugin", nil);
                    if (!self.nbCopiesStepper) {
                        self.nbCopiesStepper = [UIStepper new];
                        self.nbCopiesStepper.translatesAutoresizingMaskIntoConstraints = NO;
                        self.nbCopiesStepper.stepValue = 1.0;
                        self.nbCopiesStepper.minimumValue = 1;
                        self.nbCopiesStepper.maximumValue = 10000;
                        self.nbCopiesStepper.value = self.printRequest.multipleCopies.numberOfCopies;
                        [self.nbCopiesStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    cell.accessoryViewViaContentView = self.nbCopiesStepper;
                    break;
                case kPagesRangeRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Pages", @"CloudPrintPlugin", nil);
                    if (!self.pageRangeSegmentedControl) {
                        self.pageRangeSegmentedControl = [[UISegmentedControl alloc] initWithItems:@[NSLocalizedStringFromTable(@"All", @"CloudPrintPlugin", nil), NSLocalizedStringFromTable(@"Selected", @"CloudPrintPlugin", nil)]];
                        self.pageRangeSegmentedControl.translatesAutoresizingMaskIntoConstraints = NO;
                        [self.pageRangeSegmentedControl addConstraints:[NSLayoutConstraint width:180.0 height:self.pageRangeSegmentedControl.bounds.size.height constraintsForView:self.pageRangeSegmentedControl]];
                        [self.pageRangeSegmentedControl addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageRangeSegmentedControl.selectedSegmentIndex = self.printRequest.pageSelection ? kSelectedPagesSegmentIndex : kAllPagesSegmentIndex;
                    cell.accessoryViewViaContentView = self.pageRangeSegmentedControl;
                    break;
                case kPageFromRowIndex:
                {
                    NSString* numberString = [NSString stringWithFormat:@"%d", self.printRequest.pageSelection.pageFrom];
                    NSString* fullString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FromPageWithFormat", @"CloudPrintPlugin", nil), numberString];
                    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
                    [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:[fullString rangeOfString:numberString]];
                    cell.textLabel.attributedText = attrString;
                    if (!self.pageFromStepper) {
                        self.pageFromStepper = [UIStepper new];
                        self.pageFromStepper.translatesAutoresizingMaskIntoConstraints = NO;
                        self.pageFromStepper.stepValue = 1.0;
                        self.pageFromStepper.minimumValue = 1;
                        self.pageFromStepper.maximumValue = kPageToTheEndValue;
                        [self.pageFromStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageFromStepper.value = (double)(self.printRequest.pageSelection.pageFrom);
                    cell.accessoryViewViaContentView = self.pageFromStepper;
                    break;
                }
                case kPageToRowIndex:
                {
                    NSString* numberString = self.printRequest.pageSelection.pageTo == kPageToTheEndValue ? NSLocalizedStringFromTable(@"TheEnd", @"CloudPrintPlugin", nil) : [NSString stringWithFormat:@"%d", self.printRequest.pageSelection.pageTo];
                    NSString* fullString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"ToPageWithFormat", @"CloudPrintPlugin", nil), numberString];
                    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
                    [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:[fullString rangeOfString:numberString]];
                    cell.textLabel.attributedText = attrString;
                    if (!self.pageToChangeButton) {
                        self.pageToChangeButton = [UIButton buttonWithType:UIButtonTypeSystem];
                        self.pageToChangeButton.translatesAutoresizingMaskIntoConstraints = NO;
                        //[self.pageToChangeButton addConstraints:[NSLayoutConstraint width:100.0 height:40.0 constraintsForView:self.pageToChangeButton]];
                        [self.pageToChangeButton setTitle:NSLocalizedStringFromTable(@"Change", @"CloudPrintPlugin", nil) forState:UIControlStateNormal];
                        [self.pageToChangeButton addTarget:self action:@selector(pageToChangeTapped) forControlEvents:UIControlEventTouchUpInside];
                        [self.pageToChangeButton sizeToFit];
                    }
                    if (!self.pageToStepper) {
                        self.pageToStepper = [UIStepper new];
                        self.pageToStepper.translatesAutoresizingMaskIntoConstraints = NO;
                        self.pageToStepper.stepValue = 1.0;
                        self.pageToStepper.minimumValue = 1;
                        self.pageToStepper.maximumValue = kPageToTheEndValue;
                        [self.pageToStepper addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.pageToStepper.value = (double)(self.printRequest.pageSelection.pageTo);
                    cell.accessoryViewViaContentView = self.printRequest.pageSelection.pageTo == kPageToTheEndValue ? self.pageToChangeButton : self.pageToStepper;
                    break;
                }
                /*case kCollateRowIndex:
                {
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Collate", @"CloudPrintPlugin", nil);
                    if (!self.collateToggle) {
                        self.collateToggle = [UISwitch new];
                        [self.collateToggle addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.collateToggle.on = self.printRequest.multipleCopies.collate;
                    cell.accessoryViewViaContentView = self.collateToggle;
                    break;
                }*/
                default:
                    break;
            }
            break;
        case kOrientationSectionIndex:
            switch (indexPath.row) {
                case kOrientationRowIndex:
                {
                    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Orientation", @"CloudPrintPlugin", nil);
                    cell.detailTextLabel.text = [CloudPrintModelAdditions localizedTitleForOrientation:self.printRequest.orientation];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    break;
                }
                default:
                    break;
            }
            break;
        case kDoubleSidedSectionIndex:
            switch (indexPath.row) {
                case kDoubleSidedRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"DoubleSided", @"CloudPrintPlugin", nil);
                    if (!self.doubleSidedToggle) {
                        self.doubleSidedToggle = [UISwitch new];
                        self.doubleSidedToggle.translatesAutoresizingMaskIntoConstraints = NO;
                        [self.doubleSidedToggle addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.doubleSidedToggle.on = self.printRequest.doubleSided;
                    cell.accessoryViewViaContentView = self.doubleSidedToggle;
                    break;
                case kDoubleSidedConfigRowIndex:
                    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"FlipOn", @"CloudPrintPlugin", nil);
                    cell.detailTextLabel.text = [CloudPrintModelAdditions localizedTitleForDoubleSidedConfig:self.printRequest.doubleSided];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    break;
                default:
                    break;
            }
            break;
        case kMultiPageSectionIndex:
            switch (indexPath.row) {
                case kNbPagesPerSheetRowIndex:
                    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"PagesPerSheet", @"CloudPrintPlugin", nil);
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", self.printRequest.multiPageConfig ? self.printRequest.multiPageConfig.nbPagesPerSheet : 1];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    break;
                case kMultiPageLayoutRowIndex:
                {
                    if (!cell) {
                        cell = [[CloudPrintMultiPageLayoutCell alloc] initWithReuseIdentifier:identifier];
                    }
                    [(CloudPrintMultiPageLayoutCell*)cell setSelectedMultiPageLayout:self.printRequest.multiPageConfig.layout];
                    __weak __typeof(self) welf = self;
                    [(CloudPrintMultiPageLayoutCell*)cell setUserSelectedMultiPageLayout:^(NSInteger multiPageLayout) {
                        welf.printRequest.multiPageConfig.layout = (int)multiPageLayout;
                    }];
                    break;
                }
                default:
                    break;
            }
            break;
        case kColorSectionIndex:
            switch (indexPath.row) {
                case kColorRowIndex:
                    cell.textLabel.text = NSLocalizedStringFromTable(@"Color", @"CloudPrintPlugin", nil);
                    if (!self.colorToggle) {
                        self.colorToggle = [UISwitch new];
                        self.colorToggle.translatesAutoresizingMaskIntoConstraints = NO;
                        [self.colorToggle addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
                    }
                    self.colorToggle.on = (self.printRequest.colorConfig == CloudPrintColorConfig_COLOR);
                    cell.accessoryViewViaContentView = self.colorToggle;
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
        case kPrinterInfoSectionIndex:
            if ([self extensionInfoCellRowIndex] >= 0) {
                return 2;
            }
            return 1;
        case kCopiesAndRangeSectionIndex:
            return self.printRequest.pageSelection ? 4 : 2;
        case kOrientationSectionIndex:
            return 1;
        case kDoubleSidedSectionIndex:
            return self.printRequest.doubleSided ? 2 : 1;
        case kMultiPageSectionIndex:
            return self.printRequest.multiPageConfig ? 2 : 1;
        case kColorSectionIndex:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 6;
}

#pragma mark - Private

- (CloudPrintExtensionInfoCell*)extensionInfoCell {
    if (!_extensionInfoCell) {
        _extensionInfoCell = [CloudPrintExtensionInfoCell new];
    }
    return _extensionInfoCell;
}

static NSString* const kHideExtensionInfoBoolKey = @"HideExtensionInfoBool";

- (void)saveExtensionInfoHidden:(BOOL)hidden {
    [[PCPersistenceManager userDefaultsForPluginName:@"cloudprint"] setBool:hidden forKey:kHideExtensionInfoBoolKey];
}

- (BOOL)shouldHideExtensionInfo {
    if ([PCUtils isOSVersionSmallerThan:8.0] || [CloudPrintController sharedInstance].extensionContext) {
        //Widgets only available on iOS 8
        return YES;
    }
    return [[PCPersistenceManager userDefaultsForPluginName:@"cloudprint"] boolForKey:kHideExtensionInfoBoolKey];
}

- (NSInteger)extensionInfoCellRowIndex {
    if ([self shouldHideExtensionInfo]) {
        return -1;
    }
    return 0;
}

- (NSInteger)printerCellRowIndex {
    if ([self extensionInfoCellRowIndex] >= 0) {
        return 1;
    }
    return 0;
}

@end
