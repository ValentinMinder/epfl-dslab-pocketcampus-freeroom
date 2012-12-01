//
//  MyEduModuleVideoDownloadViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleVideoDownloadViewController.h"

#import "PCUtils.h"

@interface MyEduModuleVideoDownloadViewController ()

@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduService* myEduService;

@end

@implementation MyEduModuleVideoDownloadViewController

- (id)initWithModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleVideoDownloadView" bundle:nil];
    if (self) {
        _module = module;
        _myEduService = [MyEduService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    self.titleLabel.text = NSLocalizedStringFromTable(@"StartingDownload", @"MyEduPlugin", nil);
    self.progressionLabel.text = @"";
    self.progressView.progress = 0;
    
    MyEduModuleVideoDownloadViewController* controller __weak = self;
    
    [self.myEduService addDownloadObserver:self forVideoOfModule:self.module startDownload:NO startBlock:NULL finishBlock:^(NSURL *fileLocalURL) {
        controller.progressView.progress = 1.0;
    } progressBlock:^(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio) {
        NSString* downloadedString = [PCUtils stringFromFileSize:nbBytesDownloaded];
        NSString* toDownloadString = [PCUtils stringFromFileSize:nbBytesToDownload];
        controller.titleLabel.text = NSLocalizedStringFromTable(@"DownloadingVideo", @"MyEduPlugin", nil);
        controller.progressionLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"DownloadProgressWithFormat", @"MyEduPlugin", nil), downloadedString, toDownloadString];
        controller.progressView.progress = ratio;
    } cancelledBlock:NULL failureBlock:NULL deletedBlock:NULL];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)cancelButtonPressed {
    [self.myEduService cancelVideoDownloadForModule:self.module];
}

- (void)dealloc {
    [self.myEduService removeDownloadObserver:self forVideoModule:self.module];
}

@end
