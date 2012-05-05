
#import "CredentialsViewController.h"

#import "CoursesListViewController.h"

@implementation CredentialsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        //typeOfService = TypeOfService_SERVICE_MOODLE;
    }
    return self;
}

/*- (id)initWithTypeOfService:(int)aTypeOfService {
    self = [super initWithNibName:@"CredentialsView" bundle:nil];
    if (self) {
        self.title = @"Login for";
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        typeOfService = aTypeOfService;
    }
    return self;
}*/

- (id)initWithCallback:(UIViewController<AuthenticationCallbackDelegate>*)aViewController {
    self = [super initWithNibName:@"CredentialsView" bundle:nil];
    if (self) {
        self.title = @"Login for";
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        iViewController = aViewController;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    //[centerActivityIndicator startAnimating];
    //centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterLabelLoadingText", @"AuthenticationPlugin", @"Tell the user that the list of restaurants is loading");
    if(iViewController != nil && [iViewController getTypeOfService] == TypeOfService_SERVICE_MOODLE) {
        serviceTitleLabel.text = @"Login to service: Moodle";
    } else if(iViewController != nil && [iViewController getTypeOfService] == TypeOfService_SERVICE_CAMIPRO) {
        serviceTitleLabel.text = @"Login to service: Camipro";
    } else if(iViewController != nil && [iViewController getTypeOfService] == TypeOfService_SERVICE_ISA) {
        serviceTitleLabel.text = @"Login to service: IS-Academia";
    } else if(iViewController != nil && [iViewController getTypeOfService] == TypeOfService_SERVICE_POCKETCAMPUS) {
        serviceTitleLabel.text = @"Login to service: PocketCampus";
    } else {
        serviceTitleLabel.text = @"Login to service: Unknown";
    }
    /* TEST */
    /*
    Rating* rating = [[Rating alloc] initWithRatingValue:3.0 numberOfVotes:10 sumOfRatings:20];
    Location* location = [[Location alloc] initWithLatitude:0 longitude:0 altitude:0];
    Restaurant* restaurant1 = [[Restaurant alloc] initWithRestaurantId:1 name:@"Le Corbusier" location:location];
    Restaurant* restaurant2 = [[Restaurant alloc] initWithRestaurantId:1 name:@"Cafétéria BC" location:location];

    
    Meal* meal1 = [[Meal alloc] initWithMealId:111 name:@"Assiette 1" mealDescription:@"Filet de lieu noir (DE) à l'Italienne\nBrocoli ou salade ou potage\nPommes vapeurs" restaurant:restaurant1 rating:rating price:7.0];
    Meal* meal2 = [[Meal alloc] initWithMealId:222 name:@"Assiette végétarienne" mealDescription:@"Escalope de légumes panée, sauce dips au séré\nSalade, potage" restaurant:restaurant1 rating:rating price:8.0];
    
    Meal* meal3 = [[Meal alloc] initWithMealId:333 name:@"Assiette végétarienne" mealDescription:@"Escalope de légumes panée, sauce dips au séré\nSalade, potage" restaurant:restaurant1 rating:rating price:9.0];
    
    Meal* meal4 = [[Meal alloc] initWithMealId:444 name:@"Fourchette Verte" mealDescription:@"Boulettes de volailles (BR), sauce curry\nLégumes ou salade ou potage\nBoulgour" restaurant:restaurant2 rating:rating price:7.0];
    
    NSArray* meals2 = [NSArray arrayWithObjects:meal1, meal2, meal3, meal4, nil];
    
    [self getMealsDidReturn:meals2];
    */
    /* END OF TEST */
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillAppear:(BOOL)animated {
    //[authenticationService getMealsWithDelegate:self];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}



/* IBActions */

- (IBAction)usernameFieldEditingDidEnd:(id)sender {
    UITextField *theTextField = (UITextField *)sender;
    username = theTextField.text;
    state =0;
    //NSLog(@"%@", theTextField.text);
}

- (IBAction)passwordFieldEditingDidEnd:(id)sender {
    UITextField *theTextField = (UITextField *)sender;
    password = theTextField.text;
    state =0;
    //NSLog(@"%@", theTextField.text);
}

- (IBAction)loginButtonTouchUpInside:(id)sender {
    [centerActivityIndicator startAnimating];
    [authenticationService loginToTequilaWithUser:username password:password delegate:self];
    if(state == 0) {
    } else if(state == 1) {
    } else if(state == 2) {
    } else if(state == 3) {
    }
    state++;
}

- (IBAction)cancelButtonTouchUpInside:(id)sender {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}


/* UITextViewDelegate delegation */

-(BOOL)textFieldShouldReturn:(UITextField*)textField;
{
    NSInteger nextTag = textField.tag + 1;
    // Try to find next responder
    UIResponder* nextResponder = [textField.superview viewWithTag:nextTag];
    if (nextResponder) {
        // Found next responder, so set it.
        [nextResponder becomeFirstResponder];
    } else {
        // Not found, so remove keyboard.
        [textField resignFirstResponder];
    }
    return NO; // We do not want UITextField to insert line-breaks.
}


/* AuthenticationServiceDelegate delegation */

- (void)getTequilaKeyForService:(int)aService didReturn:(TequilaKey*)aTequilaKey {
    applicationTequilaKey = [aTequilaKey retain];
    centerMessageLabel.text = [aTequilaKey iTequilaKey];
    [authenticationService authenticateToken:[applicationTequilaKey iTequilaKey] withTequilaCookie:tequilaCookie delegate:self];
    //[authenticationService getSessionIdForService:aTequilaKey WithDelegate:self];
}

- (void)getTequilaKeyFailedForService:(int)aService {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)aTequilaKey didReturn:(SessionId*)aSessionId {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = [aSessionId moodleCookie];
    // call back the bita3
    
    //[self.navigationController popToViewController:iViewController animated:YES];
    [iViewController gotSessionId:aSessionId];
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    /*if([aSessionId tos] == TypeOfService_SERVICE_MOODLE) {
        CoursesListViewController* controller = [[CoursesListViewController alloc] initWithSessionId:aSessionId];
        //[self.navigationController popToRootViewControllerAnimated:NO];
        [self.navigationController pushViewController:controller animated:YES];
        //[self.navigationController popToViewController:controller animated:YES];
        [controller release];
    } else {
        centerMessageLabel.text = @"Unknown TypeOfService";
    }*/
    
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaKey*)aTequilaKey {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request {
    //centerMessageLabel.text = [[request responseHeaders] valueForKey:@"Location"];
    //centerMessageLabel.text = [[request responseHeaders] valueForKey:@"Set-Cookie"];
    tequilaCookie = nil;
    for(NSHTTPCookie* c in [request responseCookies]) {
        if([TEQUILA_COOKIE_NAME isEqualToString:[c name]]) {
            tequilaCookie = [[c value] retain];
        }
    }
    if(tequilaCookie == nil) {
        [centerActivityIndicator stopAnimating];
        centerMessageLabel.text = @"Bad credentials, please try again";
    }else{
        centerMessageLabel.text = tequilaCookie;
        //[authenticationService getTequilaKeyForService:TypeOfService_SERVICE_MOODLE WithDelegate:self];
        //[authenticationService getTequilaKeyForService:typeOfService WithDelegate:self];
        [authenticationService getTequilaKeyForService:[iViewController getTypeOfService] delegate:self];
    }
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request{
    NSString* redir = [[request responseHeaders] valueForKey:@"Location"];
    if(redir == nil) {
        [centerActivityIndicator stopAnimating];
        centerMessageLabel.text = @"Unexpected error occured";
    } else {
        centerMessageLabel.text = @"Token authenticated successfully";
        [authenticationService getSessionIdForServiceWithTequilaKey:applicationTequilaKey delegate:self];
    }
}

- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
}


- (void)dealloc
{
    [authenticationService cancelOperationsForDelegate:self];
    [authenticationService release];
    [super dealloc];
}

@end
