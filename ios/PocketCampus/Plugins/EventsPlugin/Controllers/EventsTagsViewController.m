//
//  EventsTagsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.05.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsTagsViewController.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCCenterMessageCell.h"

static NSString* kCellTextLabelTextStyle;

@interface EventsTagsViewController ()

@property (nonatomic, strong) IBOutlet UIView* headerView;
@property (nonatomic, strong) IBOutlet UIButton* selectAllButton;
@property (nonatomic, strong) IBOutlet UIButton* deselectAllButton;

@property (nonatomic, strong) NSArray* allTags;
@property (nonatomic, strong) NSSet* selectedInitially;

@property (nonatomic, strong) NSMutableSet* newlySelected;

@end

@implementation EventsTagsViewController

- (id)initWithTags:(NSArray*)allTags selectedInitially:(NSSet*)selectedInitially userValidatedSelectionBlock:(void (^)(NSSet* newlySelected))userValidatedSelectionBlock;
{
    self = [super initWithNibName:@"EventsTagView" bundle:nil];
    if (self) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            kCellTextLabelTextStyle = UIFontTextStyleFootnote;
        });
        [PCUtils throwExceptionIfObject:allTags notKindOfClass:[NSArray class]];
        self.allTags = allTags;
        self.selectedInitially = selectedInitially;
        self.userValidatedSelectionBlock = userValidatedSelectionBlock;
        self.newlySelected = [NSMutableSet setWithSet:selectedInitially];
        self.title = NSLocalizedStringFromTable(@"FilterByTags", @"EventsPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = (PCTableViewAdditions*)self.tableView;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForStyle:UITableViewCellStyleDefault textLabelTextStyle:kCellTextLabelTextStyle detailTextLabelTextStyle:nil]);
    };
    self.tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];
    self.tableView.backgroundView = backgroundView;
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    if (self.allTags.count > 0) {
        [self.selectAllButton setTitle:NSLocalizedStringFromTable(@"SelectAll", @"EventsPlugin", nil) forState:UIControlStateNormal];
        [self.deselectAllButton setTitle:NSLocalizedStringFromTable(@"DeselectAll", @"EventsPlugin", nil) forState:UIControlStateNormal];
        self.tableView.tableHeaderView = self.headerView;
    }
}

- (NSUInteger)supportedInterfaceOrientations
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Buttons actions

- (void)cancelButtonPressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)doneButtonPressed {
    if (!self.userValidatedSelectionBlock) {
        return;
    }
    self.userValidatedSelectionBlock(self.newlySelected);
}

- (IBAction)selectAllPressed {
    if (!self.newlySelected) {
        return;
    }
    [self.newlySelected addObjectsFromArray:self.allTags];
    self.navigationItem.rightBarButtonItem.enabled = (self.newlySelected.count > 0);
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
}

- (IBAction)deselectAllPressed {
    if (!self.newlySelected) {
        return;
    }
    [self.newlySelected removeAllObjects];
    self.navigationItem.rightBarButtonItem.enabled = (self.newlySelected.count > 0);
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.allTags.count) {
        return;
    }
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSString* tag = self.allTags[indexPath.row];
    if ([self.newlySelected containsObject:tag]) {
        [self.newlySelected removeObject:tag];
    } else {
        [self.newlySelected addObject:tag];
    }
    self.navigationItem.rightBarButtonItem.enabled = (self.newlySelected.count > 0);
    [self configureCell:[self.tableView cellForRowAtIndexPath:indexPath] atIndexPath:indexPath];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.allTags && [self.allTags count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoTag", @"EventsPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    NSString* const kTagCell = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"TagCell"];
    
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:kTagCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kTagCell];
        UIFont* font = [UIFont preferredFontForTextStyle:kCellTextLabelTextStyle];
        cell.textLabel.font = [UIFont boldSystemFontOfSize:font.pointSize];
        cell.textLabel.numberOfLines = 2;
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    
    [self configureCell:cell atIndexPath:indexPath];
    
    return cell;
}

- (void)configureCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath {
    NSString* tag = self.allTags[indexPath.row];
    cell.textLabel.text = tag;
    if ([self.newlySelected containsObject:tag]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (self.allTags) {
        NSInteger count = [self.allTags count];
        if (count == 0) {
            return 2; //no category message a row index 1
        }
        return count;
    }
    
    return 0;
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    
    if (self.allTags) {
        return 1;
    }
    return 0;
}

@end
