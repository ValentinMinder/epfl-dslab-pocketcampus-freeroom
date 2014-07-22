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

//  Created by Loïc Gardiol on 20.07.14.

#import "MoodleFolderViewController.h"

#import "MoodleService.h"

#import "PCCenterMessageCell.h"

#import "MoodleResourceCell.h"

#import "MoodleSplashDetailViewController.h"

#import "MoodleFileViewController.h"

#import "PCWebViewController.h"

@interface MoodleFolderViewController ()

@property (nonatomic, strong, readwrite) MoodleFolder2* folder;
@property (nonatomic, readonly) NSArray* files;
@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSMapTable* cellForMoodleFile; //Key: MoodleFile2, value: cell
@property (nonatomic, strong) MoodleFile2* selectedFile;

@end

@implementation MoodleFolderViewController

#pragma mark - Init

- (instancetype)initWithFolder:(MoodleFolder2*)folder {
    [PCUtils throwExceptionIfObject:folder notKindOfClass:[MoodleFolder2 class]];
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/moodle/course/folder";
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.folder = folder;
        self.title = folder.name;
        [self fillCellForMoodleFile];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    
    RowHeightBlock rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle]*1.2);
    };
    tableViewAdditions.rowHeightBlock = rowHeightBlock;
    __weak __typeof(self) welf = self;
    tableViewAdditions.contentSizeCategoryDidChangeBlock = ^(PCTableViewAdditions* tableView) {
        //need to do it manually because UISearchDisplayController does not support using a custom table view (PCTableViewAdditions in this case)
        welf.searchDisplayController.searchResultsTableView.rowHeight = tableView.rowHeightBlock(tableView);
        [welf fillCellForMoodleFile];
        [welf.searchDisplayController.searchResultsTableView reloadData];
    };
    
    self.tableView.allowsMultipleSelection = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(favoriteMoodleResourcesUpdated:) name:kMoodleFavoritesMoodleItemsUpdatedNotification object:self.moodleService];
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - Properties

- (NSArray*)files {
    return self.folder.files;
}

#pragma mark - Notifications listening

- (void)favoriteMoodleResourcesUpdated:(NSNotification*)notif {
    id item = notif.userInfo[kMoodleFavoritesStatusMoodleItemUpdatedUserInfoKey];
    if (!item) {
        return;
    }
    
    for (MoodleFile2* file in self.cellForMoodleFile) {
        if ([item isEqual:file]) {
            MoodleResourceCell* cell = [self.cellForMoodleFile objectForKey:file];
            cell.favoriteIndicationVisible = [self.moodleService isFavoriteMoodleItem:file];
            break;
        }
    }
}

#pragma mark - Collections fill & utils

- (void)fillCellForMoodleFile {
    if (!self.files) {
        return;
    }
    
    NSMapTable* cellsTemp = [NSMapTable mapTableWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory];

    for (MoodleFile2* file in self.files) {
        
        MoodleResource2* tmpResource = [[MoodleResource2 alloc] initWithFile:file folder:nil url:nil]; //just created to pass to MoodleResourceCell. Useless otherwise.
        MoodleResourceCell* cell = [[MoodleResourceCell alloc] initWithMoodleResource:tmpResource];
        cell.durablySelected = [file isEqual:self.selectedFile];
        [cellsTemp setObject:cell forKey:file];
        
        __weak typeof(cell) weakCell = cell;
        __weak typeof(self) welf = self;

        [self.moodleService removeMoodleFileObserver:self forFile:file];
        [self.moodleService addMoodleFileObserver:self forFile:file eventBlock:^(MoodleResourceEvent event) {
            if (!weakCell) {
                return;
            }
            if (event == MoodleResourceEventDeleted) {
                weakCell.durablySelected = NO;
                if (welf.splitViewController && [welf.selectedFile isEqual:file]) { //iPad //file deleted => hide ResourceViewController
                    [welf.tableView deselectRowAtIndexPath:[welf.tableView indexPathForSelectedRow] animated:YES];
                    welf.selectedFile = nil;
                    MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                    welf.splitViewController.viewControllers = @[welf.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:splashViewController]];
                    [NSTimer scheduledTimerWithTimeInterval:0.2 target:welf selector:@selector(showMasterViewController) userInfo:nil repeats:NO];
                }
            }
            [weakCell setNeedsLayout];
        }];
        
    }
    
    self.cellForMoodleFile = cellsTemp;
}

- (void)showMasterViewController {
    [(PluginSplitViewController*)self.splitViewController setMasterViewControllerHidden:NO animated:YES];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    MoodleFile2* file = self.files[indexPath.row];
    
    if (self.splitViewController && [file isEqualToMoodleFile:self.selectedFile]) {
        return;
    }
    
    UIViewController* viewController = [[MoodleFileViewController alloc] initWithMoodleFile:file];
    
    [self trackAction:@"DownloadAndOpenFile" contentInfo:file.name];
    
    if (self.splitViewController) { // iPad
        if (self.selectedFile) {
            MoodleResourceCell* prevCell = [self.cellForMoodleFile objectForKey:self.selectedFile];
            prevCell.durablySelected = NO;
        }
        self.selectedFile = file;
        
        MoodleResourceCell* newCell = [self.cellForMoodleFile objectForKey:file];
        newCell.durablySelected = YES;
        
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController]; //to have nav bar
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else { // iPhone or iPad folder
        [self.navigationController pushViewController:viewController animated:YES];
    }
    
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleResourceCell* cell = (MoodleResourceCell*)[tableView cellForRowAtIndexPath:indexPath];
    if (cell.isDownloadedIndicationVisible) {
        return UITableViewCellEditingStyleDelete;
    }
    return UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tableView willBeginEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    //nothing to do, just prevent table view to enter editing mode (would show delete control in other cells which we don't want)
    //see http://stackoverflow.com/questions/6437916/how-to-avoid-swipe-to-delete-calling-setediting-at-the-uitableviewcell
}

- (void)tableView:(UITableView *)tableView didEndEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    //see tableView:willBeginEditingRowAtIndexPath:
    [[tableView cellForRowAtIndexPath:indexPath] setEditing:NO];
}

#pragma mark - UITableViewDataSource

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MoodleFile2* file = self.files[indexPath.row];
        if (!file) {
            //should not happen
            return;
        }
        [self trackAction:PCGAITrackerActionDelete contentInfo:file.name];
        if ([self.moodleService deleteDownloadedMoodleFile:file]) {
            [tableView setEditing:NO animated:YES];
        } else {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            return;
        }
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.files && self.files.count == 0) {
        if (indexPath.row == 1) {
            NSString* message = NSLocalizedStringFromTable(@"MoodleEmptyFolder", @"MoodlePlugin", nil);
            return [[PCCenterMessageCell alloc] initWithMessage:message];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    MoodleFile2* file = self.files[indexPath.row];
    MoodleResourceCell* cell = [self.cellForMoodleFile objectForKey:file];
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.files && self.files.count == 0) {
        return 2; //empty cell + message
    }
    return self.folder.files.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.moodleService removeMoodleFileObserver:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];

}

@end
