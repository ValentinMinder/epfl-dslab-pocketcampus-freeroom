//
//  MyEduCourseListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduCourseListViewController.h"

@interface MyEduCourseListViewController ()

@end

static NSString* kMyEduCourseListCell = @"MyEduCourseListCell";

@implementation MyEduCourseListViewController

- (id)init
{
    self = [super initWithNibName:@"MyEduCourseListView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    if ([self respondsToSelector:@selector(refreshControl)]) {
        UIRefreshControl* refresh = [[UIRefreshControl alloc] init];
        refresh.attributedTitle = [[NSAttributedString alloc] initWithString:@""];
        [refresh addTarget:self action:@selector(refresh) forControlEvents:UIControlEventValueChanged];
        self.refreshControl = refresh;
        [self refresh];
    } else {
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    }
   
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark refresh control

- (void)refresh {
    if ([self respondsToSelector:@selector(refreshControl)]) {
        if (!self.refreshControl.refreshing) {
            [self.refreshControl beginRefreshing];
        }
        self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"DownloadingCourseList", @"MyEduPlugin", nil)];
        [NSTimer scheduledTimerWithTimeInterval:2.0 target:self.refreshControl selector:@selector(endRefreshing) userInfo:nil repeats:NO];
    }
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //TODO
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyEduCourseListCell forIndexPath:indexPath];
    
    // Configure the cell...
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{

    // Return the number of sections.
    return 0;
}


@end
