//
//  EventItemViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItemViewController.h"

#import "PCUtils.h"

#import "PCRefreshControl.h"

#import "PCCenterMessageCell.h"

#import "EventItemCell.h"

#import "GANTracker.h"

#import "EventsUtils.h"

#import "PCTableViewSectionHeader.h"

#import "PCValues.h"

#import "EventItem+Additions.h"

@interface EventItemViewController ()

@property (nonatomic) int64_t eventId;
@property (nonatomic, strong) EventItem* eventItem;
@property (nonatomic, strong) EventItemReply* itemReply;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) EventsService* eventsService;

@end

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
    [PCUtils throughExceptionIfObject:item notKindOfClass:[EventItem class]];
    self = [self init];
    if (self) {
        self.eventId = item.eventId;
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

#pragma mark - View load and visibility

- (void)viewDidLoad {
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/events/item" withError:NULL];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.itemReply) {
        [self refresh];
    }
}

#pragma mark - Refresh control

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingEventPool", @"EventsPlugin", nil)];
    [self startGetEventItemRequest];
}

- (void)startGetEventItemRequest {
    EventItemRequest* req = [[EventItemRequest alloc] initWithEventItemId:self.eventId userToken:[self.eventsService lastUserToken] lang:[PCUtils userLanguageCode] period:EventsPeriods_SIX_MONTHS];
    [self.eventsService getEventItemForRequest:req delegate:self];
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
    
    if (self.eventItem.eventThumbnail) {
        replacements[@"$EVENT_ITEM_THUMBNAIL$"] = [NSString stringWithFormat:@"<img src='%@'>", self.eventItem.eventThumbnail];
    }
    
    if (self.eventItem.eventTitle) {
        replacements[@"$EVENT_ITEM_TITLE$"] = [NSString stringWithFormat:@"%@", self.eventItem.eventTitle];
    }
    
    if (YES) { //TODO check hideInfo
        if (self.eventItem.startDate) {
            replacements[@"$EVENT_ITEM_DATE_TIME$"] = [NSString stringWithFormat:@"%@: %@", NSLocalizedStringFromTable(@"Date", @"EventsPlugin", nil), [self.eventItem shortDateString]];
        }
        
        if (self.eventItem.eventPlace) {
            if (self.eventItem.locationHref) {
                replacements[@"$EVENT_ITEM_PLACE$"] = [NSString stringWithFormat:@"%@: <a href='%@'>%@</a>", NSLocalizedStringFromTable(@"Place", @"EventsPlugin", nil), self.eventItem.locationHref, self.eventItem.eventPlace];
            } else {
                replacements[@"$EVENT_ITEM_PLACE$"] = [NSString stringWithFormat:@"%@: %@", NSLocalizedStringFromTable(@"Place", @"EventsPlugin", nil), self.eventItem.eventPlace];
            }
        }
        
        if (self.eventItem.eventSpeaker) {
            replacements[@"$EVENT_ITEM_SPEAKER$"] = [NSString stringWithFormat:@"%@: %@", NSLocalizedStringFromTable(@"Speaker", @"EventsPlugin", nil), self.eventItem.eventSpeaker];
        }
        
        if (self.eventItem.detailsLink) {
            replacements[@"$EVENT_ITEM_MORE$"] = [NSString stringWithFormat:@"%@: <a href='%@'>%@</a>", NSLocalizedStringFromTable(@"More", @"EventsPlugin", nil), self.eventItem.detailsLink, NSLocalizedStringFromTable(@"Details", @"EventsPlugin", nil)];
        }
    }
    
    if (self.eventItem.eventPicture) {
        replacements[@"$EVENT_ITEM_CENTER_IMAGE$"] = [NSString stringWithFormat:@"<img src='%@'>", self.eventItem.eventPicture];
    }
    
    if (self.eventItem.eventDetails) {
        replacements[@"$EVENT_ITEM_DETAILS$"] = [NSString stringWithFormat:@"<p>%@</p>", self.eventItem.eventDetails];
    }
    
    [replacements enumerateKeysAndObjectsUsingBlock:^(NSString* key, NSString* replacement, BOOL *stop) {
        html = [html stringByReplacingOccurrencesOfString:key withString:replacement];
    }];
    

    
    if (self.newsItem.imageUrl) {
        NSString* imageSrc = self.newsItem.imageUrl;
        NSString* path = [self pathForImage];
        if ([[NSFileManager defaultManager] fileExistsAtPath:path]) { //then image was saved to disk (in viewDidLoad)
            imageSrc = path;
        }
        html = [html stringByReplacingOccurrencesOfString:@"$NEW_ITEM_IMAGE_SRC$" withString:imageSrc];
        html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"inline"];
    } else {
        html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"none"];
    }
    html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_CONTENT$" withString:content];
    
    html = [NewsUtils htmlReplaceWidthWith100PercentInContent:html ifWidthHeigherThan:self.webView.frame.size.width-20.0];
    
    [self.webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:@"/"]];
}

#pragma mark - EventsServiceDelegate

- (void)getEventItemForRequest:(EventItemRequest *)request didReturn:(EventItemReply *)reply {
    switch (reply.status) {
        case 200:
            self.itemReply = reply;
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
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
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil);
    [PCUtils showServerErrorAlert];
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

- (void)serviceConnectionToServerTimedOut {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil);
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

@end
