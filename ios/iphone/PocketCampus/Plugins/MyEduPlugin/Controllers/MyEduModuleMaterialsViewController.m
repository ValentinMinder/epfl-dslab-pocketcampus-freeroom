//
//  MyEduModuleMaterialsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleMaterialsViewController.h"

@interface MyEduModuleMaterialsViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) NSArray* materials;
@property (nonatomic, strong) UIPopoverController* materialsPopOverController;

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
        self.authController = [[AuthenticationController alloc] init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.webView.scalesPageToFit = YES;
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemBookmarks target:self action:@selector(materialsListButtonPressed)];
    self.navigationItem.rightBarButtonItem.enabled = NO; //do not enable before having the content ready
    
    UIViewController* materialsListViewController = [[UIViewController alloc] init];
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
    MyEduModuleDetailsRequest* request = [[MyEduModuleDetailsRequest alloc] initWithICourseCode:self.course.iCode iSectionId:self.section.iId iModuleId:self.module.iId];
    [self.myEduService getModuleDetailsForRequest:request myeduRequest:[self.myEduService createMyEduRequest] delegate:self];
}

#pragma mark menu actions control

- (void)materialsListButtonPressed {
    [self.materialsPopOverController presentPopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    CGRect resizedBounds = self.materialsTableView.bounds;
    resizedBounds.size.height = self.materialsTableView.contentSize.height;
    [self.materialsPopOverController setPopoverContentSize:CGSizeMake(resizedBounds.size.width, resizedBounds.size.height) animated:NO];
}

#pragma mark - MyEduService Delegate

- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduModuleDetailsReply*)reply {
    [self.loadingIndicator stopAnimating];
    self.materials = reply.iMyEduMaterials;
    if (self.materials.count > 0) {
        self.navigationItem.rightBarButtonItem.enabled = YES;
        //display content directly
        [self tableView:self.materialsTableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]]; //simulate user pressing first element
        [self.materialsTableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:UITableViewScrollPositionNone];

    } else {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoMaterial", @"MyEduPlugin", nil);
        self.centerMessageLabel.hidden = NO;
    }
}

- (void)getModuleDetailsFailedForRequest:(MyEduModuleDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest {
    //TODO
}

- (void)downloadOfMaterial:(MyEduMaterial *)meterial didFinish:(MyEduMaterialData*)materialData {
    [self.webView loadData:materialData.data MIMEType:materialData.mimeType textEncodingName:materialData.textEncoding baseURL:[NSURL URLWithString:@"https://myedu.epfl.ch"]];
    self.webView.hidden = NO;
    self.centerMessageLabel.hidden = YES;
    self.progressView.hidden = YES;
}

- (void)downloadFailedForMaterial:(MyEduMaterial *)meterial {
    //TODO
}

- (void)serviceConnectionToServerTimedOut {
    //TODO
}

#pragma mark - AuthenticationCallbackDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        NSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [self.myEduService getMyEduSessionForTequilaToken:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    //TODO
}

- (void)invalidToken {
    [self startGetModuleDetailsRequest];
}


#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.materialsPopOverController dismissPopoverAnimated:YES];
    MyEduMaterial* material = self.materials[indexPath.row];
    MyEduMaterialData* materialData = [self.myEduService materialDataIfExistsForMaterial:material];
    if (materialData) {
        [self.webView loadData:materialData.data MIMEType:materialData.mimeType textEncodingName:@"utf-8" baseURL:[NSURL URLWithString:@"https://myedu.epfl.ch"]];
        self.webView.hidden = NO;
    } else {
        [self.myEduService downloadMaterial:material myeduRequest:[self.myEduService createMyEduRequest] progressView:self.progressView delegate:self];
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MyEduPlugin", nil);
        self.centerMessageLabel.hidden = NO;
        self.progressView.hidden = NO;
        self.webView.hidden = YES;
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
    self.webView.delegate = nil;
    [self.webView stopLoading];
    [self.myEduService cancelOperationsForDelegate:self];
}

@end
