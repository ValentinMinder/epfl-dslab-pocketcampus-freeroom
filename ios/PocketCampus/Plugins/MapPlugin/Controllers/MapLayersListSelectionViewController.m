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

//  Created by Loïc Gardiol on 09.10.14.

#import "MapLayersListSelectionViewController.h"

#import "MapService.h"

@interface MapLayersListSelectionViewController ()

@property (nonatomic, weak) IBOutlet UIView* headerView;
@property (nonatomic, weak) IBOutlet UIButton* selectAllButton;
@property (nonatomic, weak) IBOutlet UIButton* deselectAllButton;

@property (nonatomic, strong) MapService* mapService;
@property (nonatomic, strong) NSArray* sortedMapLayers;
@property (nonatomic, copy) void (^doneButtonTappedBlock)();

@end

@implementation MapLayersListSelectionViewController

#pragma mark - Init

- (instancetype)initWithAllSelectableMapLayers:(NSArray*)allMapLayers doneButtonTappedBlock:(void (^)())doneButtonTappedBlock {
    [PCUtils throwExceptionIfObject:allMapLayers notKindOfClass:[NSArray class]];
    self = [super initWithNibName:@"MapLayersListSelectionView" bundle:nil];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MapLayersToDisplay", @"MapPlugin", nil);
        self.mapService = [MapService sharedInstanceToRetain];
        self.sortedMapLayers = [allMapLayers sortedArrayUsingComparator:^NSComparisonResult(MapLayer* layer1, MapLayer* layer2) {
            return [layer1.name compare:layer2.name];
        }];
        self.doneButtonTappedBlock = doneButtonTappedBlock;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    /*self.tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];
    self.tableView.backgroundView = backgroundView;*/
    
    PCTableViewAdditions* tableViewAdditions = (PCTableViewAdditions*)self.tableView;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault]);
    };
    
    if (![PCUtils isIdiomPad]) {
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonTapped)];
    }
    [self.selectAllButton setTitle:NSLocalizedStringFromTable(@"SelectAll", @"MapPlugin", nil) forState:UIControlStateNormal];
    [self.deselectAllButton setTitle:NSLocalizedStringFromTable(@"DeselectAll", @"MapPlugin", nil) forState:UIControlStateNormal];
    self.tableView.tableHeaderView = self.headerView;
}

#pragma mark - Actions

- (void)doneButtonTapped {
    if (self.doneButtonTappedBlock) {
        self.doneButtonTappedBlock();
    }
}

- (IBAction)selectAllTapeed:(id)sender {
    NSMutableSet* set = [NSMutableSet set];
    for (MapLayer* layer in self.sortedMapLayers) {
        [set addObject:@(layer.layerId)];
    }
    self.mapService.selectedMapLayerIds = set;
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
}

- (IBAction)deselectAllTapped:(id)sender {
    self.mapService.selectedMapLayerIds = [NSSet set];
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    MapLayer* layer = self.sortedMapLayers[indexPath.row];
    NSNumber* nsLayerId = @(layer.layerId);
    NSMutableSet* updatedSet = [self.mapService.selectedMapLayerIds mutableCopy];
    if ([self.mapService.selectedMapLayerIds containsObject:nsLayerId]) {
        [updatedSet removeObject:nsLayerId];
    } else {
        [updatedSet addObject:nsLayerId];
    }
    self.mapService.selectedMapLayerIds = updatedSet;
    [self configureCell:[self.tableView cellForRowAtIndexPath:indexPath] atIndexPath:indexPath];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString* const identifier = @"LayerCell";
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    [self configureCell:cell atIndexPath:indexPath];
    return cell;
}

- (void)configureCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath {
    MapLayer* layer = self.sortedMapLayers[indexPath.row];
    cell.textLabel.text = layer.name;
    if ([self.mapService.selectedMapLayerIds containsObject:@(layer.layerId)]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.sortedMapLayers.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
