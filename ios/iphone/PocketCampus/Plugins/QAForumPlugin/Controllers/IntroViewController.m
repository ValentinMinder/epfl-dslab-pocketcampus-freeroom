//
//  IntroViewController.m
//  PocketCampus
//
//  Created by Susheng on 5/5/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "IntroViewController.h"

@interface IntroViewController ()

@end

@implementation IntroViewController
@synthesize scrollView, pageControl, pageArray;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    //Put the names of our image files in our array.
    //Put the names of our image files in our array.
    qaforumService = [[QAForumService sharedInstanceToRetain] retain];
    self.title = NSLocalizedStringFromTable(@"Intro", @"QAForumPlugin", nil);
    pageArray = [[NSArray alloc] initWithObjects:@"qaforum_intro1.png", @"qaforum_intro2.png", @"qaforum_intro3.png", @"qaforum_intro4.png", @"qaforum_intro5.png", nil];
    
    for (int i = 0; i < [pageArray count]; i++) {
        //We'll create an imageView object in every 'page' of our scrollView.
        CGRect frame;
        frame.origin.x = self.scrollView.frame.size.width * i;
        frame.origin.y = 0;
        frame.size = self.scrollView.frame.size;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
        imageView.image = [UIImage imageNamed:[pageArray objectAtIndex:i]];
        [self.scrollView addSubview:imageView];
    }
    //Set the content size of our scrollview according to the total width of our imageView objects.
    scrollView.contentSize = CGSizeMake(scrollView.frame.size.width * [pageArray count], scrollView.frame.size.height);
    self.navigationItem.leftBarButtonItem =
    [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Skip", @"QAForumPlugin",nil)
                                     style:UIBarButtonItemStyleBordered
                                    target:self
                                    action:@selector(handleBack:)];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)handleBack:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:TRUE];
}

- (void)startQAForum {
    //ask users where to close introduction permenently.
    NSString* message = NSLocalizedStringFromTable(@"Introduction", @"QAForumPlugin", nil);
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Intro", @"QAForumPlugin", nil) message:message delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"QAForumPlugin", nil) otherButtonTitles:NSLocalizedStringFromTable(@"YES", @"QAForumPlugin", nil), NSLocalizedStringFromTable(@"NO", @"QAForumPlugin", nil),nil];
    [alert show];
    [alert release];
}

- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    // Update the page when more than 50% of the previous/next page is visible
    CGFloat pageWidth = self.scrollView.frame.size.width;
    int page = floor((self.scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.pageControl.currentPage = page;
    if(page==4) {
        UIBarButtonItem *newBackButton = [[UIBarButtonItem alloc] initWithTitle: NSLocalizedStringFromTable(@"Start", @"QAForumPlugin", nil) style: UIBarButtonItemStyleBordered target: self action: @selector(startQAForum)];
        [[self navigationItem] setRightBarButtonItem: newBackButton];
        [newBackButton release];
    }
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 1) {
        [qaforumService closeIntro:[QAForumService lastSessionId].sessionid delegate:self];
        [self.navigationController popToRootViewControllerAnimated:TRUE];
    }
    else if (buttonIndex == 2) {
        [self.navigationController popToRootViewControllerAnimated:TRUE];
    }
}

- (void)closeIntro:(NSString *)userid didReturn:(NSString *)result {
    [QAForumService lastSessionId].intro = 1;
}
- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
}

- (void)dealloc {
    [scrollView release];
    [pageControl release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setScrollView:nil];
    [self setPageControl:nil];
    [super viewDidUnload];
}
@end
