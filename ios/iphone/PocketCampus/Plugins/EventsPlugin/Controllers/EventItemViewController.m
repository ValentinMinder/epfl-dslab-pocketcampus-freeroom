//
//  EventItemViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItemViewController.h"

#import "PCUtils.h"

#import "PCCenterMessageCell.h"

#import "EventItemCell.h"

#import "GANTracker.h"

#import "EventsUtils.h"

#import "PCTableViewSectionHeader.h"

#import "PCValues.h"

#import "EventItem+Additions.h"

#import "EventPoolViewController.h"

#import "MainController.h"

#import "PCURLSchemeHandler.h"

#import "PCTableViewWithRemoteThumbnails.h"

#import "ASIHTTPRequest.h"

@interface EventItemViewController ()

@property (nonatomic) int64_t eventId;
@property (nonatomic, strong) EventItem* eventItem;
@property (nonatomic, strong) EventItemReply* itemReply;
@property (nonatomic, strong) EventsService* eventsService;

//@property (nonatomic, strong) ASIHTTPRequest* pictureRequest __unused;

@property (nonatomic, strong) NSArray* childrenPools; //array of EventPool sorted by Id

@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;

@end

static NSString* kPoolCell = @"PoolCell";

@implementation EventItemViewController

#pragma mark - Inits

- (id)init
{
    self = [super initWithNibName:@"EventItemView" bundle:nil];
    if (self) {
        self.eventId = 0;
        self.eventsService = [EventsService sharedInstanceToRetain];
    }
    return self;
}

- (id)initWithEventItem:(EventItem*)item {
    [PCUtils throwExceptionIfObject:item notKindOfClass:[EventItem class]];
    self = [self init];
    if (self) {
        self.eventId = item.eventId;
        self.eventItem = item;
        self.title = item.eventTitle;
    }
    return self;
}

- (id)initAndLoadEventItemWithId:(int64_t)eventId {
    self = [self init];
    if (self) {
        self.eventId = eventId;
    }
    return self;
}

#pragma mark - Standard view controller methods

- (void)viewDidLoad {
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:[NSString stringWithFormat:@"/v3r1/events/%lld", self.eventId] withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    self.tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    self.tableView.backgroundView = backgroundView;
    
    if (self.eventItem) {
        [self loadEvent];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    if (!self.eventItem || ([self.eventItem.childrenPools count] > 0 && !self.itemReply)) {
        [self refresh];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

#pragma mark - Refresh control

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self startGetEventItemRequest];
}

- (void)startGetEventItemRequest {
    EventItemRequest* req = [[EventItemRequest alloc] initWithEventItemId:self.eventId userToken:[self.eventsService lastUserToken] lang:[PCUtils userLanguageCode] period:EventsPeriods_SIX_MONTHS];
    [self.eventsService getEventItemForRequest:req delegate:self];
    if (!self.loadingIndicator) {
        self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        self.loadingIndicator.color = [UIColor colorWithWhite:0.3 alpha:1.0];
        [self.scrollView addSubview:self.loadingIndicator];
        self.loadingIndicator.center = self.scrollView.center;
        [self.loadingIndicator startAnimating];
    }
}

#pragma mark - Collections fill

- (void)fillChildrenPools {
    if (!self.itemReply) {
        return;
    }
    // Sort pools by Id
    NSArray* sortedPoolIds = [[self.itemReply.childrenPools allKeys] sortedArrayUsingSelector:@selector(compare:)];
    NSMutableArray* sortedPools = [NSMutableArray arrayWithCapacity:[self.itemReply.childrenPools count]];
    for (NSNumber* poolId in sortedPoolIds) {
        [sortedPools addObject:self.itemReply.childrenPools[poolId]];
    }
    self.childrenPools = [sortedPools copy]; //non-mutale copy
}

#pragma mark - Buttons actions

- (void)addRemoveFavoritesButtonPressed {
    if ([self.eventsService isEventItemIdFavorite:self.eventItem.eventId]) {
        [self.eventsService removeFavoriteEventItemId:self.eventItem.eventId];
        [self refreshFavoriteButton];
    } else {
        [self.eventsService addFavoriteEventItemId:self.eventItem.eventId];
        [self refreshFavoriteButton];
    }
}


#pragma mark - Views loads

- (void)loadEvent {
    if (!self.eventItem) {
        return;
    }
    
    [self refreshFavoriteButton];
    
    if ([self.eventItem.childrenPools count] == 0) {
        self.webView.frame = self.view.frame;
        self.tableView.hidden = YES;
    }
    [self loadWebView];
}

- (void)refreshFavoriteButton {
    UIImage* image = nil;
    if ([self.eventsService isEventItemIdFavorite:self.eventItem.eventId]) {
        image = [UIImage imageNamed:@"FavoriteGlowNavBarButton"];
    } else {
        image = [UIImage imageNamed:@"FavoriteNavBarButton"];
    }
    
    if (!self.navigationItem.rightBarButtonItem) {        
        UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 42.0, 42.0)];
        [button setImage:image forState:UIControlStateNormal];
        button.adjustsImageWhenHighlighted = NO;
        button.showsTouchWhenHighlighted = NO;
        [button addTarget:self action:@selector(addRemoveFavoritesButtonPressed) forControlEvents:UIControlEventTouchDown];        
        [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithCustomView:button] animated:NO];
        
    } else {
        [(UIButton*)(self.navigationItem.rightBarButtonItem.customView) setImage:image forState:UIControlStateNormal];
    }
}

- (void)loadWebView {
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"EventItemInfo" ofType:@"html"];
    NSError* error = nil;
    NSString* html __block = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        [self error];
        return;
    }
    
    NSMutableDictionary* replacements = [NSMutableDictionary dictionaryWithCapacity:10];
    
    replacements[@"$EVENT_ITEM_THUMBNAIL$"] = @"";
    replacements[@"$EVENT_ITEM_TITLE$"] = @"";
    replacements[@"$PADDING_LEFT_TITLE_PX$"] = @"0";
    replacements[@"$EVENT_ITEM_SECOND_LINE$"] = @"";
    replacements[@"$EVENT_ITEM_DATE_TIME$"] = @"";
    replacements[@"$EVENT_ITEM_PLACE$"] = @"";
    replacements[@"$EVENT_ITEM_SPEAKER$"] = @"";
    replacements[@"$EVENT_ITEM_MORE$"] = @"";
    
    
    if (self.eventItem.eventThumbnail && !self.eventItem.hideThumbnail) {
        replacements[@"$EVENT_ITEM_THUMBNAIL$"] = [NSString stringWithFormat:@"<img src='%@'>", self.eventItem.eventThumbnail];
        replacements[@"$PADDING_LEFT_TITLE_PX$"] = @"6";
    }
    
    if (self.eventItem.eventTitle && !self.eventItem.hideTitle) {
        replacements[@"$EVENT_ITEM_TITLE$"] = [NSString stringWithFormat:@"%@", self.eventItem.eventTitle];
    }
    
    if (self.eventItem.secondLine) {
        replacements[@"$EVENT_ITEM_SECOND_LINE$"] = [NSString stringWithFormat:@"%@", self.eventItem.secondLine];
    }
    
    if (!self.eventItem.hideEventInfo) {
        
        if (self.eventItem.startDate) {
            if (self.eventItem.timeSnippet) {
                replacements[@"$EVENT_ITEM_DATE_TIME$"] = [NSString stringWithFormat:@"%@<br>", self.eventItem.timeSnippet];
            } else {
                replacements[@"$EVENT_ITEM_DATE_TIME$"] = [NSString stringWithFormat:@"<b>%@:</b> %@<br>", NSLocalizedStringFromTable(@"Date&Time", @"EventsPlugin", nil), [self.eventItem dateString:EventItemDateStyleLong]];
            }
        }
        
        if (self.eventItem.eventPlace) {
            if (self.eventItem.locationHref) {
                replacements[@"$EVENT_ITEM_PLACE$"] = [NSString stringWithFormat:@"<b>%@:</b> <a href='%@'>%@</a><br>", NSLocalizedStringFromTable(@"Place", @"EventsPlugin", nil), self.eventItem.locationHref, self.eventItem.eventPlace];
            } else {
                replacements[@"$EVENT_ITEM_PLACE$"] = [NSString stringWithFormat:@"<b>%@:</b> %@<br>", NSLocalizedStringFromTable(@"Place", @"EventsPlugin", nil), self.eventItem.eventPlace];
            }
        }
        
        if (self.eventItem.eventSpeaker) {
            replacements[@"$EVENT_ITEM_SPEAKER$"] = [NSString stringWithFormat:@"<b>%@:</b> %@<br>", NSLocalizedStringFromTable(@"By", @"EventsPlugin", nil), self.eventItem.eventSpeaker];
        }
        
        if (self.eventItem.detailsLink) {
            replacements[@"$EVENT_ITEM_MORE$"] = [NSString stringWithFormat:@"<a href='%@'>%@</a>", self.eventItem.detailsLink, NSLocalizedStringFromTable(@"MoreDetails", @"EventsPlugin", nil)];
        }
    }
    
    replacements[@"$EVENT_ITEM_CENTER_IMAGE$"] = @"";
    if (self.eventItem.eventPicture) {
        replacements[@"$EVENT_ITEM_CENTER_IMAGE$"] = [NSString stringWithFormat:@"<img src='%@'>", self.eventItem.eventPicture];
    }
    
    replacements[@"$EVENT_ITEM_DETAILS$"] = @"";
    if (self.eventItem.eventDetails) {
        replacements[@"$EVENT_ITEM_DETAILS$"] = [NSString stringWithFormat:@"<p>%@</p>", self.eventItem.eventDetails];
    }
    
    [replacements enumerateKeysAndObjectsUsingBlock:^(NSString* key, NSString* replacement, BOOL *stop) {
        html = [html stringByReplacingOccurrencesOfString:key withString:replacement];
    }];
    
    self.webView.delegate = self;
    
    [self.webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:@"/"]];
}

- (void)finalizeElementsPositionAndSize {
    self.tableView.scrollEnabled = NO;
    self.tableView.frame = CGRectMake(0, self.webView.frame.origin.y + self.webView.frame.size.height, self.tableView.frame.size.width, self.tableView.contentSize.height);
    [self.scrollView setContentSize: CGSizeMake(self.webView.frame.size.width, self.webView.frame.size.height + self.tableView.frame.size.height)];
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)aWebView {
    if ([self.eventItem.childrenPools count] == 0) {
        return;
    }
    aWebView.scrollView.scrollEnabled = NO;    // Property available in iOS 5.0 and later
    CGRect frame = aWebView.frame;
    
    frame.size.width = self.view.frame.size.width;       // Your desired width here.
    frame.size.height = 1;        // Set the height to a small one.
    
    aWebView.frame = frame;       // Set webView's Frame, forcing the Layout of its embedded scrollView with current Frame's constraints (Width set above).
    
    frame.size.height = aWebView.scrollView.contentSize.height;  // Get the corresponding height from the webView's embedded scrollView.
    
    aWebView.frame = frame;

    self.webView.hidden = NO;
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        id<MainControllerPublic> mainController = [MainController publicController];
        UIViewController* viewController = [[mainController urlSchemeHandlerSharedInstance] viewControllerForPocketCampusURLScheme:request.URL];
        if (viewController) {
            [self.navigationController pushViewController:viewController animated:YES];
        } else {
            [[UIApplication sharedApplication] openURL:request.URL];
        }
        return NO;
    }
    return YES;
}

#pragma mark - EventsServiceDelegate

- (void)getEventItemForRequest:(EventItemRequest *)request didReturn:(EventItemReply *)reply {
    switch (reply.status) {
        case 200:
            [self.loadingIndicator stopAnimating];
            self.eventItem = reply.eventItem;
            self.itemReply = reply;
            [self loadEvent];
            [self fillChildrenPools];
            [self.tableView reloadData];
            [self finalizeElementsPositionAndSize];
            break;
            
        default:
            [self getEventItemFailedForRequest:request];
            break;
    }
}

- (void)getEventItemFailedForRequest:(EventPoolRequest *)request {
    [self error];
}

- (void)error {
    [self.loadingIndicator stopAnimating];
    [PCUtils addCenteredLabelInView:self.scrollView withMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil)];
    self.tableView.hidden = YES;
}

- (void)serviceConnectionToServerTimedOut {
    [self.loadingIndicator stopAnimating];
    [PCUtils addCenteredLabelInView:self.scrollView withMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil)];
    self.tableView.hidden = YES;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    EventPool* eventPool = self.childrenPools[indexPath.row];
    [self.navigationController pushViewController:[[EventPoolViewController alloc] initWithEventPool:eventPool] animated:YES];
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    EventPool* eventPool = self.childrenPools[indexPath.row];
    
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kPoolCell];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kPoolCell];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    cell.textLabel.text = eventPool.poolTitle;
    cell.detailTextLabel.text = eventPool.poolPlace;
    
    [(PCTableViewWithRemoteThumbnails*)(self.tableView) setThumbnailURL:[NSURL URLWithString:eventPool.poolPicture] forCell:cell atIndexPath:indexPath];
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.childrenPools count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.childrenPools) {
        return 0;
    }
    return 1;
}

#pragma mark - dealloc

- (void)dealloc {
    self.webView.delegate = nil;
    [self.eventsService cancelOperationsForDelegate:self];
}




@end
