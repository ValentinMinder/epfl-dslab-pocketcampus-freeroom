//
//  MyEduModuleMaterialsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleMaterialsViewController.h"

#import "MyEduController.h"

#import <MobileCoreServices/MobileCoreServices.h>

#import "UIPopoverController+Additions.h"

#import "GANTracker.h"

@interface MyEduModuleMaterialsViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) NSArray* materials;
@property (nonatomic, strong) UIPopoverController* materialsPopOverController;
@property (nonatomic, strong) UIDocumentInteractionController* docInteractionController;
@property (nonatomic, weak) UIBarButtonItem* actionButton;
@property (nonatomic) BOOL isShowingActionMenu;

@end

static NSString* kMyEduModuleMaterialCell = @"MyEduModuleMaterialCell";

@implementation MyEduModuleMaterialsViewController

- (id)initWithMyEduModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduModuleMaterialsView" bundle:nil];
    if (self) {
        // Custom initialization
        _module = module;
        _section = section;
        _course = course;
        self.myEduService = [MyEduService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/myedu/sections/modules/material" withError:NULL];
    
    self.webView.scalesPageToFit = YES;
    
    UIViewController* materialsListViewController = [[UIViewController alloc] init]; //will only be visible if more than 1 material
    materialsListViewController.view = self.materialsTableView;
    self.materialsPopOverController = [[UIPopoverController alloc] initWithContentViewController:materialsListViewController];
    
    [self startGetModuleDetailsRequest];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)startGetModuleDetailsRequest {
    [self.loadingIndicator startAnimating];
    
    VoidBlock successBlock = ^{
        MyEduModuleDetailsRequest* request = [[MyEduModuleDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode iSectionId:self.section.iId iModuleId:self.module.iId];
        [self.myEduService getModuleDetailsForRequest:request delegate:self];
    };
    if ([self.myEduService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MyEduController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self error];
        } failureBlock:^{
            [self error];
        }];
    }
}

- (void)startDownloadOfMaterial:(MyEduMaterial*)material {
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MyEduPlugin", nil);
    self.centerMessageLabel.hidden = NO;
    self.progressView.hidden = NO;
    self.webView.hidden = YES;
    self.actionButton.enabled = NO;
    
    VoidBlock successBlock = ^{
        [self.myEduService downloadMaterial:material progressView:self.progressView delegate:self];
    };
    if ([self.myEduService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MyEduController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self error];
        } failureBlock:^{
            [self error];
        }];
    }
}

- (void)presentMaterialData:(MyEduMaterialData*)materialData {
    self.docInteractionController = [[UIDocumentInteractionController alloc] init];
    self.docInteractionController.URL = materialData.localURL;
    CFStringRef MIMEType = (__bridge CFStringRef)materialData.mimeType;
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassMIMEType, MIMEType, NULL);
    self.docInteractionController.UTI = (__bridge_transfer NSString *)UTI;
    self.docInteractionController.delegate = self;
    self.actionButton.enabled = YES;
    [self.webView loadData:materialData.data MIMEType:materialData.mimeType textEncodingName:@"utf-8" baseURL:[NSURL URLWithString:@"https://myedu.epfl.ch"]];
    self.webView.hidden = NO;
}

#pragma mark menu actions control

- (void)materialsListButtonPressed {
    [self.docInteractionController dismissMenuAnimated:NO];
    CGRect resizedBounds = self.materialsTableView.bounds;
    resizedBounds.size.height = self.materialsTableView.contentSize.height;
    [self.materialsPopOverController setPopoverContentSize:CGSizeMake(resizedBounds.size.width, resizedBounds.size.height) animated:NO];
    [self.materialsPopOverController togglePopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
}

- (void)actionButtonPressed {
    [self.materialsPopOverController dismissPopoverAnimated:NO];
    if (self.isShowingActionMenu) {
        [self.docInteractionController dismissMenuAnimated:YES];
        return;
    }
    BOOL couldShowMenu = [self.docInteractionController presentOptionsMenuFromBarButtonItem:self.actionButton animated:YES];
    if (!couldShowMenu) {
        UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Sorry", @"MoodlePlugin", nil) message:NSLocalizedStringFromTable(@"NoActionForThisFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
    }
}

#pragma mark - MyEduService Delegate

- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request didReturn:(MyEduModuleDetailsReply*)reply {
    [self.loadingIndicator stopAnimating];
    switch (reply.iStatus) {
        case 200:
            self.materials = reply.iMyEduMaterials;
            if ([self.materials count] > 0) {
                
                NSMutableArray* rightBarButtonItems = [NSMutableArray array];
                if ([self.materials count] > 1) {
                    UIBarButtonItem* materialsListButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MyEduDrawersNavbar"] style:UIBarButtonItemStyleBordered target:self action:@selector(materialsListButtonPressed)];
                    [rightBarButtonItems addObject:materialsListButton];
                }
                
                UIBarButtonItem* actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
                self.actionButton = actionButton;
                [rightBarButtonItems addObject:actionButton];
                [self.navigationItem setRightBarButtonItems:rightBarButtonItems animated:YES];
                
                if ([self.materials count] > 1) {
                    [self materialsListButtonPressed]; //show list more than one file
                    self.actionButton.enabled = NO;
                } else { // only 1 material
                    //display only-content directly
                    [self tableView:self.materialsTableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]]; //simulate user pressing first element
                    [self.materialsTableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:UITableViewScrollPositionNone];
                }
            } else {
                self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoMaterial", @"MyEduPlugin", nil);
                self.centerMessageLabel.hidden = NO;
            }
            break;
        case 407: //need to relogin
            [self.myEduService deleteSession];
            [self startGetModuleDetailsRequest];
            break;
        default:
            [self getModuleDetailsFailedForRequest:request];
            break;
    }
}

- (void)getModuleDetailsFailedForRequest:(MyEduModuleDetailsRequest *)request {
    [self error];
}

- (void)downloadOfMaterial:(MyEduMaterial *)meterial didFinish:(MyEduMaterialData*)materialData {
    [self presentMaterialData:materialData];
}

- (void)downloadFailedForMaterial:(MyEduMaterial *)material responseStatusCode:(int)statusCode {
    switch (statusCode) {
        case 302:
            [self.myEduService deleteSession];
            [self startDownloadOfMaterial:material];
            break;
        default: //404, 500, ...
            [self error];
            break;
    }
}

- (void)serviceConnectionToServerTimedOut {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.webView.hidden = YES;
    self.progressView.hidden = YES;
}

- (void)error {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.webView.hidden = YES;
    self.progressView.hidden = YES;
}


#pragma mark - UIDocumentInteractionControllerDelegate

/* Deprecated. Necessary for iOS<=5 */
- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller canPerformAction:(SEL)action
{
    
    if (action == @selector (print:) && [UIPrintInteractionController canPrintURL:controller.URL]) {
        return YES;
    }
    return NO;
}

/* Deprecated. Necessary for iOS<=5 */
- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller performAction:(SEL)action
{
    
    bool __block success = NO;
    if (action == @selector(print:)) {
        UIPrintInteractionController *printController = [UIPrintInteractionController sharedPrintController];
        printController.printingItem = controller.URL;
        [printController presentAnimated:YES completionHandler:^(UIPrintInteractionController *printInteractionController, BOOL completed, NSError *error){
            if (completed) {
                success = YES;
            }
        }];
    }
    return success;
}

- (void)documentInteractionControllerWillPresentOptionsMenu:(UIDocumentInteractionController *)controller {
    self.isShowingActionMenu = YES;
}

- (void)documentInteractionControllerDidDismissOptionsMenu:(UIDocumentInteractionController *)controller {
    self.isShowingActionMenu = NO;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.myEduService cancelOperationsForDelegate:self];
    [self.materialsPopOverController dismissPopoverAnimated:YES];
    MyEduMaterial* material = self.materials[indexPath.row];
    MyEduMaterialData* materialData = [self.myEduService materialDataIfExistsForMaterial:material];
    if (materialData) {
        [self presentMaterialData:materialData];
    } else {
        [self startDownloadOfMaterial:material];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    MyEduMaterial* material = self.materials[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyEduModuleMaterialCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kMyEduModuleMaterialCell];
        //cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.textLabel.font = [UIFont boldSystemFontOfSize:15.0];
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
    }
    
    cell.textLabel.text = material.iName;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.materials count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [self.loadingIndicator startAnimating];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self.loadingIndicator stopAnimating];
}

- (void)dealloc {
    self.docInteractionController.delegate = nil;
    self.webView.delegate = nil;
    [self.webView stopLoading];
    [self.myEduService cancelOperationsForDelegate:self];
}

@end
