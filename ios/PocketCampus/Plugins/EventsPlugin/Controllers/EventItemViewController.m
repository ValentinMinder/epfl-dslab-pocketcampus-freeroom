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

//  Created by Loïc Gardiol on 02.03.13.

#import "EventItemViewController.h"

#import "PCCenterMessageCell.h"

#import "EventsUtils.h"

#import "PCTableViewSectionHeader.h"

#import "EventItem+Additions.h"

#import "EventPoolViewController.h"

#import "MainController.h"

#import "PCURLSchemeHandler.h"

#import "PCWebViewController.h"

@interface EventItemViewController ()<EventsServiceDelegate, UIWebViewDelegate, UITableViewDelegate, UITableViewDataSource>

@property (nonatomic) int64_t eventId;
@property (nonatomic, strong) EventItem* eventItem;
@property (nonatomic, strong) EventItemReply* itemReply;
@property (nonatomic, strong) EventsService* eventsService;

@property (nonatomic, strong) NSArray* childrenPools; //array of EventPool sorted by Id

@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, strong) IBOutlet PCTableViewAdditions* tableView;
@property (nonatomic, strong) UIWebView* webView;

@end

@implementation EventItemViewController

#pragma mark - Inits

- (id)init
{
    self = [super initWithNibName:@"EventItemView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/events/item";
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
    self.tableView.backgroundColor = [UIColor clearColor];
    
    self.tableView.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle];
    };
    
    __weak __typeof(self) weakSelf = self;
    self.tableView.contentSizeCategoryDidChangeBlock = ^(PCTableViewAdditions* tableView) {
        [weakSelf repositionTableViewHeader];
    };
    
    if (self.eventItem && self.eventItem.childrenPools.count == 0) {
        [self loadEvent];
    } else {
        [self refresh];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}

#pragma mark - Public methods et properties

- (int64_t)itemId {
    return self.eventId;
}

- (void)setShowFavoriteButton:(BOOL)showFavoriteButton {
    _showFavoriteButton = showFavoriteButton;
    [self refreshFavoriteButton];
}

#pragma mark - Refresh control

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self startGetEventItemRequest];
}

- (void)startGetEventItemRequest {    
    EventItemRequest* req = [[EventItemRequest alloc] initWithEventItemId:self.eventId userToken:nil userTickets:[[self.eventsService allUserTickets] mutableCopy] lang:[PCUtils userLanguageCode]];
    [self.eventsService getEventItemForRequest:req delegate:self];
    [self.loadingIndicator startAnimating];
    self.tableView.hidden = YES;
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
        [self trackAction:PCGAITrackerActionUnmarkFavorite contentInfo:[NSString stringWithFormat:@"%lld-%@", self.eventId, self.eventItem.eventTitle]];
        [self.eventsService removeFavoriteEventItemId:self.eventItem.eventId];
    } else {
        [self trackAction:PCGAITrackerActionMarkFavorite contentInfo:[NSString stringWithFormat:@"%lld-%@", self.eventId, self.eventItem.eventTitle]];
        [self.eventsService addFavoriteEventItemId:self.eventItem.eventId];
    }
    [self refreshFavoriteButton];
}

#pragma mark - Views loads

- (void)loadEvent {
    if (!self.eventItem) {
        return;
    }
    
    self.title = self.eventItem.eventTitle;
    [self refreshFavoriteButton];
    [self loadWebView];
}

- (void)refreshFavoriteButton {
    if (!self.showFavoriteButton) {
        self.navigationItem.rightBarButtonItem = nil;
        return;
    }
    BOOL isFavorite = [self.eventsService isEventItemIdFavorite:self.eventItem.eventId];
    UIImage* image = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(addRemoveFavoritesButtonPressed)];
    button.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveEventFromFavorites", @"EventsPlugin", nil) : NSLocalizedStringFromTable(@"AddEventToFavorites", @"EventsPlugin", nil);
    self.navigationItem.rightBarButtonItem = button;
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
    replacements[@"$EVENT_ITEM_TAGS$"] = @"";
    
    
    if (self.eventItem.eventThumbnail && !self.eventItem.hideThumbnail) {
        replacements[@"$EVENT_ITEM_THUMBNAIL$"] = [NSString stringWithFormat:@"<img src='%@'>", self.eventItem.eventThumbnail];
        replacements[@"$PADDING_LEFT_TITLE$"] = @"6px";
        replacements[@"$WIDTH_TITLE$"] = @"63%";
    } else {
        replacements[@"$PADDING_LEFT_TITLE$"] = @"0px";
        replacements[@"$WIDTH_TITLE$"] = @"100%";
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
                replacements[@"$EVENT_ITEM_DATE_TIME$"] = [NSString stringWithFormat:@"<b>%@:</b> %@<br>", NSLocalizedStringFromTable(@"Date&Time", @"EventsPlugin", nil), self.eventItem.dateString];
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
            replacements[@"$EVENT_ITEM_MORE$"] = [NSString stringWithFormat:@"<a href='%@'>%@</a><br>", self.eventItem.detailsLink, NSLocalizedStringFromTable(@"MoreDetails", @"EventsPlugin", nil)];
        }
        
        /*if (self.eventItem.eventTags.count > 0) {
            replacements[@"$EVENT_ITEM_TAGS$"] = [NSString stringWithFormat:@"<b>%@:</b> %@<br>", NSLocalizedStringFromTable(@"Tags", @"EventsPlugin", nil), self.eventItem.eventTags];
        }*/
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
    
    if (!self.webView) {
        self.webView = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 1.0)];
        self.webView.delegate = self;
        self.webView.scrollView.scrollEnabled = NO;
        self.tableView.tableHeaderView = self.webView;
    }
    [self.webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:@"/"]];
    [self repositionTableViewHeader];
}

- (void)repositionTableViewHeader {
    CGFloat height = [[self.webView stringByEvaluatingJavaScriptFromString:@"document.body.scrollHeight"] floatValue];
    
    self.webView.frame = CGRectMake(0, 0, self.webView.frame.size.width, height);
    
    self.tableView.tableHeaderView = self.webView; //makes table view look at webview's frame again and adapt first section y
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)aWebView {
    [self repositionTableViewHeader];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        id<MainControllerPublic> mainController = [MainController publicController];
        UIViewController* viewController = [[mainController urlSchemeHandlerSharedInstance] viewControllerForPocketCampusURL:request.URL];
        if (viewController) {
            [self.navigationController pushViewController:viewController animated:YES];
        } else {
            PCWebViewController* webViewController = [[PCWebViewController alloc] initWithURL:request.URL title:nil];
            [self.navigationController pushViewController:webViewController animated:YES];
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
            self.tableView.hidden = NO;
            self.eventId = reply.eventItem.eventId;
            self.eventItem = reply.eventItem;
            self.itemReply = reply;
            [self fillChildrenPools];
            [self.tableView reloadData];
            [self loadEvent];
            break;
        default:
            [self getEventItemFailedForRequest:request];
            break;
    }
}

- (void)getEventItemFailedForRequest:(EventItemRequest *)request {
    [self error];
}

- (void)error {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = YES;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    self.tableView.hidden = YES;
}

- (void)serviceConnectionToServerFailed {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = YES;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.tableView.hidden = YES;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 1.0;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.childrenPools.count) {
        return;
    }
    EventPool* eventPool = self.childrenPools[indexPath.row];
    
    UIViewController* viewController;
    
    if (eventPool.overrideLink) {
        NSURL* url = [NSURL URLWithString:eventPool.overrideLink];
        id<MainControllerPublic> mainController = [MainController publicController];
        UIViewController* viewController = [[mainController urlSchemeHandlerSharedInstance] viewControllerForPocketCampusURL:url];
        if (viewController) {
            [self.navigationController pushViewController:viewController animated:YES];
        } else {
            PCWebViewController* webViewController = [[PCWebViewController alloc] initWithURL:url title:nil];
            [self.navigationController pushViewController:webViewController animated:YES];
            [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
        }
    } else {
        viewController = [[EventPoolViewController alloc] initWithEventPool:eventPool];
    }
    
    [self.navigationController pushViewController:viewController animated:YES];
    
    [self trackAction:@"ShowEventPool" contentInfo:[NSString stringWithFormat:@"%lld-%@", eventPool.poolId, eventPool.poolTitle]];
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    EventPool* eventPool = self.childrenPools[indexPath.row];
    
    NSString* const identifier = [self.tableView autoInvalidatingReuseIdentifierForIdentifier:@"PoolCell"];
    PCTableViewCellAdditions* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
        cell.detailTextLabel.textColor = [UIColor grayColor];
    }
    
    cell.textLabel.text = eventPool.poolTitle;
    cell.detailTextLabel.text = eventPool.poolPlace;
    
    if (eventPool.overrideLink) {
        id<MainControllerPublic> mainController = [MainController publicController];
        PCURLSchemeHandler* handler = [mainController urlSchemeHandlerSharedInstance];
        if (![handler isValidPocketCampusURL:[NSURL URLWithString:eventPool.overrideLink]]) {
            cell.accessoryView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ArrowUpRightCircleAccessory"]];
        }
    }
    
    [(PCTableViewAdditions*)(self.tableView) setImageURL:[NSURL URLWithString:eventPool.poolPicture] forCell:cell atIndexPath:indexPath];
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.childrenPools.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
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
