//
//  MenuCellView.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "MealCell.h"

#import "food.h"

#import "MenusListViewController.h"

#import "PCConfig.h"

static float SEPARATOR_HEIGHT = 1.0;
static float RATINGS_HEIGHT = 28.0;
static float MEAL_DESCRIPTION_FONT_SIZE = 16.0;

@implementation MealCell

- (id)initWithMeal:(Meal*)meal_ controller:(MenusListViewController*)controller_ reuseIdentifier:(NSString*)reuseIdentfier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentfier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.accessoryType = UITableViewCellAccessoryNone;
        //self.backgroundColor = [UIColor colorWithWhite:0.96 alpha:1.0];
        self.backgroundColor = [UIColor whiteColor];
        if (meal_ == nil) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"meal argument cannot be nil" userInfo:nil];
        }
        meal = [meal_ retain];
        controller = controller_;
        service = [[FoodService sharedInstanceToRetain] retain];
        voteMode = VoteModeUnset;
        [self initContent];
    }
    return self;
}

- (void)initContent {
    UIView* backgroundRatings = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, RATINGS_HEIGHT)];
    backgroundRatings.backgroundColor = [UIColor colorWithWhite:0.98 alpha:1.0];
    //backgroundRatings.backgroundColor = [UIColor yellowColor];
    
    votesLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 5, 105, 20)];
    votesLabel.font = [UIFont systemFontOfSize:12.0];
    votesLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
    votesLabel.shadowOffset = [PCValues shadowOffset1];
    votesLabel.shadowColor = [PCValues shadowColor1];
    votesLabel.textAlignment = UITextAlignmentLeft;
    votesLabel.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.0];
    [self updateRatingInfosRefreshFromServer:NO];
    
    ratingView = [[JSFavStarControl alloc] initWithCenter:CGPointMake(self.center.x, 14.0) dotImage:[UIImage imageNamed:@"RatingPoint"] starImage:[UIImage imageNamed:@"RatingStar"] emptyStarImage:[UIImage imageNamed:@"RatingStarEmpty"] editable:NO];
    if (meal.rating != nil) {
        [ratingView setRating:round(meal.rating.ratingValue)];
    }
    [ratingView addTarget:self action:@selector(ratingValueChanged) forControlEvents:UIControlEventValueChanged];
    
    NSString* voteActionText = NSLocalizedStringFromTable(@"VoteVerb", @"FoodPlugin", nil);
    CGSize voteActionTextSize = [voteActionText sizeWithFont:[UIFont systemFontOfSize:13.0]];
    voteButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
    voteButtonNormalFrame = CGRectMake(320 - (voteActionTextSize.width+16), 0, voteActionTextSize.width+16, RATINGS_HEIGHT);
    voteButtonHiddenFrame = CGRectMake(320, 0, voteActionTextSize.width+16, RATINGS_HEIGHT);
    voteButton.titleLabel.font = [UIFont systemFontOfSize:13.0];
    voteButton.layer.cornerRadius = 0;
    voteButton.showsTouchWhenHighlighted = YES;
    [self setVoteMode:VoteModeVote animated:NO];
    
    ratingActivityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
    ratingActivityIndicator.color = [UIColor colorWithWhite:0.3 alpha:1.0];
    ratingActivityIndicator.hidesWhenStopped = YES;
    ratingActivityIndicator.center = CGPointMake(voteButton.frame.origin.x-20, 15);
    
    UIView* separator = [[UIView alloc] initWithFrame:CGRectMake(0, (ratingView.center.y+ratingView.frame.size.height/2)+4, self.frame.size.width, SEPARATOR_HEIGHT)];
    separator.backgroundColor = [UIColor colorWithWhite:0.8 alpha:1.0];
    
    /*CGSize textViewSize = [meal.mealDescription sizeWithFont:[UIFont systemFontOfSize:MEAL_DESCRIPTION_FONT_SIZE] constrainedToSize:CGSizeMake(self.frame.size.width, 1000)];*/
    
    textView = [[self.class textViewForMeal:meal] retain];
    [self.contentView addSubview:textView];
    [self.contentView addSubview:backgroundRatings];
    [self.contentView addSubview:votesLabel];
    [self.contentView addSubview:ratingView];
    [self.contentView addSubview:ratingActivityIndicator];
    [self.contentView addSubview:voteButton];
    [self.contentView addSubview:separator];
    
    [backgroundRatings release];
    [separator release];
    
    //WARNING : release views;
}

+ (CGFloat)requiredHeightForMeal:(Meal*)meal_ {
    if (![meal_ isKindOfClass:[Meal class]]) {
        @throw [NSException exceptionWithName:@"bad argument" reason:@"meal argument is nil or not kind of class Meal" userInfo:nil];
    }
    UITextView* textView = [self textViewForMeal:meal_];
    return RATINGS_HEIGHT+SEPARATOR_HEIGHT+textView.frame.size.height;
}

+ (UITextView*)textViewForMeal:(Meal*)meal_ {
    if (![meal_ isKindOfClass:[Meal class]]) {
        @throw [NSException exceptionWithName:@"bad argument" reason:@"meal argument is nil or not kind of class Meal" userInfo:nil];
    }
    UITextView* textView = [[UITextView alloc] initWithFrame:CGRectMake(0, RATINGS_HEIGHT+SEPARATOR_HEIGHT, 320.0, 1000.0)];
    textView.text = meal_.mealDescription;
    textView.font = [UIFont systemFontOfSize:MEAL_DESCRIPTION_FONT_SIZE];
    textView.contentInset = UIEdgeInsetsMake(-2, 5, 2, 5);
    textView.scrollEnabled = NO;
    textView.editable = NO;
    textView.textColor = [PCValues textColor1];
    //textView.backgroundColor = [UIColor yellowColor];
    [textView sizeToFit];
    return [textView autorelease];
}

- (void)setMeal:(Meal*)newMeal {
    if (newMeal == nil) {
        @throw [NSException exceptionWithName:@"illegal argument" reason:@"meal argument cannot be nil" userInfo:nil];
    }
    [meal release];
    meal = [newMeal retain];
    [self updateRatingInfosRefreshFromServer:NO];
    textView.frame = [[self class] textViewForMeal:newMeal].frame;
    textView.text = newMeal.mealDescription;
}

- (void)votePressed {
    [ratingView setEditable:YES resetRating:YES];
    UIBarButtonItem* cancelButton = [[[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(cancelPressed)] autorelease];
    [controller.navigationItem setRightBarButtonItem:cancelButton animated:YES];
    [controller setForAllCellsVoteMode:VoteModeDisabled exceptCell:nil animated:YES];
}

- (void)ratingValueChanged {
    if (ratingView.rating > 0) {
        [self setVoteMode:VoteModeOK animated:YES];
    } else {
        [self setVoteMode:VoteModeDisabled animated:YES];
    }
}

- (void)cancelPressed {
    [ratingActivityIndicator stopAnimating];
    [service cancelOperationsForDelegate:self];
    [controller setForAllCellsVoteMode:VoteModeVote exceptCell:nil animated:YES];
    [self updateRatingInfosRefreshFromServer:NO];
    [controller.navigationItem setRightBarButtonItem:nil animated:YES];
    [controller showMapButtonIfPossible];
}

- (void)okPressed {    
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/food/restaurant/click/rate" withError:NULL];
    [ratingActivityIndicator startAnimating];
    [self setVoteMode:VoteModeDisabled animated:YES];
    if ([[PCConfig defaults] boolForKey:PC_OPTIONAL_CONFIG_ALLOW_MEALS_MULTI_VOTES_KEY]) {
        [service setRatingForMeal:meal.mealId rating:(double)ratingView.rating deviceId:[NSString stringWithFormat:@"%ld",time(NULL)]  delegate:self];
    } else {
        [service setRatingForMeal:meal.mealId rating:(double)ratingView.rating deviceId:[[UIDevice currentDevice] uniqueDeviceIdentifier]  delegate:self];
    }
}

- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId didReturn:(int)status {
    
    [ratingActivityIndicator stopAnimating];
    
    UIAlertView* alert = nil;
    
    switch (status) {
        case SubmitStatus_ERROR:
            [self setRatingFailedForMeal:mealId rating:rating deviceId:deviceId];
            break;
        case SubmitStatus_TOO_EARLY:
            [controller setForAllCellsVoteMode:VoteModeVote exceptCell:nil animated:YES];
            [controller.navigationItem setRightBarButtonItem:nil animated:YES];
            [controller showMapButtonIfPossible];
            [self updateRatingInfosRefreshFromServer:NO];
            alert = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"RatingTooEarly", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            break;
        case SubmitStatus_ALREADY_VOTED: //should not happen, as checked before already
            [controller.navigationItem setRightBarButtonItem:nil animated:YES];
            [controller showMapButtonIfPossible];
            [controller setForAllCellsVoteMode:VoteModeDisabled exceptCell:nil animated:YES];
            [self updateRatingInfosRefreshFromServer:NO];
            alert = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"RatingAlreadyDone", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            break;
        case SubmitStatus_VALID:
            [controller.navigationItem setRightBarButtonItem:nil animated:YES];
            [controller showMapButtonIfPossible];
            [controller setForAllCellsVoteMode:VoteModeDisabled exceptCell:nil animated:YES];
            [self updateRatingInfosRefreshFromServer:YES];
            break;
        default:
            //Not supported
            break;
    }
    [alert release];
    
}

- (void)setVoteMode:(VoteMode)newMode animated:(BOOL)animated {
    if (newMode == voteMode) {
        return;
    }
    
    [voteButton removeTarget:nil action:NULL forControlEvents:UIControlEventAllEvents];
    
    typedef void (^b1_t)(void);
    
    switch (newMode) {
        case VoteModeVote: 
        {
            [voteButton addTarget:self action:@selector(votePressed) forControlEvents:UIControlEventTouchUpInside];
            b1_t animBlock = ^{
                voteButton.frame = voteButtonNormalFrame;
                NSString* voteActionText = NSLocalizedStringFromTable(@"VoteVerb", @"FoodPlugin", nil);
                [voteButton setTitle:voteActionText forState:UIControlStateNormal];
                [voteButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
                voteButton.backgroundColor = [UIColor colorWithWhite:0.6 alpha:1.0];
            };
            if (animated) {
                [UIView transitionWithView:voteButton duration:0.1 options:UIViewAnimationTransitionNone animations:animBlock completion:NULL];
            } else {
                animBlock();
            }
            break;
        }
        case VoteModeOK: 
        {
            [voteButton addTarget:self action:@selector(okPressed) forControlEvents:UIControlEventTouchUpInside];
            b1_t animBlock = ^{
                voteButton.backgroundColor = [UIColor colorWithRed:0.3137 green:1.0 blue:0.3372 alpha:1.0];
                [voteButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
                [voteButton setTitle:@"OK" forState:UIControlStateNormal];
                voteButton.frame = voteButtonNormalFrame;
            };
            if (animated) {
                [UIView transitionWithView:voteButton duration:0.1 options:UIViewAnimationTransitionNone animations:animBlock completion:NULL];
            } else {
                animBlock();
            }
            break;
        }
        case VoteModeDisabled: 
        {
            b1_t animBlock = ^{
                voteButton.frame = voteButtonHiddenFrame;
            };
            if (animated) {
                [UIView transitionWithView:voteButton duration:0.1 options:UIViewAnimationTransitionFlipFromLeft animations:animBlock completion:NULL];
            } else {
                animBlock();
            }
            break;
        }
        default:
            NSLog(@"VoteMode cannot be VoteModeUnset");
            break;
    }
}

- (void)updateRatingInfosRefreshFromServer:(BOOL)refreshFromServer {
    if (meal == nil) {
        return;
    }
    if (refreshFromServer) {
        [service getRating:meal delegate:self];
        return;
    }
    ratingView.rating = rint(meal.rating.ratingValue);
    [ratingView setEditable:NO resetRating:NO];
    if (meal.rating.numberOfVotes > 1) {
        votesLabel.text = [NSString stringWithFormat:@"  %d %@", meal.rating.numberOfVotes, NSLocalizedStringFromTable(@"VoteNamePlural", @"FoodPlugin", nil)];
    } else if (meal.rating.numberOfVotes == 1) {
        votesLabel.text = [NSString stringWithFormat:@"  %d %@", meal.rating.numberOfVotes, NSLocalizedStringFromTable(@"VoteNameSingular", @"FoodPlugin", nil)];
    } else { //0 vote
        votesLabel.text = [NSString stringWithFormat:@"  %@", NSLocalizedStringFromTable(@"NoVote", @"FoodPlugin", nil)];
    }
}

- (void)getRatingFor:(Meal*)meal_ didReturn:(Rating*)rating {
    if (rating.numberOfVotes > 0) {
        votesLabel.text = [NSString stringWithFormat:@"  %d %@", rating.numberOfVotes, NSLocalizedStringFromTable(@"VoteNamePlural", @"FoodPlugin", nil)];
    } else {
        votesLabel.text = [NSString stringWithFormat:@"  %@", NSLocalizedStringFromTable(@"NoVote", @"FoodPlugin", nil)];
    }
    ratingView.rating = rint(rating.ratingValue);
    [controller setUpdatedRating:rating forMeal:meal];
    [ratingView setEditable:NO resetRating:NO];
}

- (void)setRatingFailedForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId {
    [ratingActivityIndicator stopAnimating];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"RatingSendingError", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
    [service cancelOperationsForDelegate:self];
    [controller setForAllCellsVoteMode:VoteModeVote exceptCell:nil animated:YES];
    [self updateRatingInfosRefreshFromServer:NO];
    [controller.navigationItem setRightBarButtonItem:nil animated:YES];
    [controller showMapButtonIfPossible];
}

- (void)serviceConnectionToServerTimedOut {
    [ratingActivityIndicator stopAnimating];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.") delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
    [service cancelOperationsForDelegate:self];
    [controller setForAllCellsVoteMode:VoteModeVote exceptCell:nil animated:YES];
    [self updateRatingInfosRefreshFromServer:NO];
    [controller.navigationItem setRightBarButtonItem:nil animated:YES];
    [controller showMapButtonIfPossible];
}

/*
- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
*/

- (void)dealloc
{
    [service cancelOperationsForDelegate:self];
    [service release];
    [meal release];
    [votesLabel release];
    [ratingView release];
    [textView release];
    [ratingActivityIndicator release];
    [voteButton release];
    [super dealloc];
}

@end
