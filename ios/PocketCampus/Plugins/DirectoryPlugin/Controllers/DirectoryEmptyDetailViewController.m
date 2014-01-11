

//  Created by Loïc Gardiol on 21.12.12.


#import "DirectoryEmptyDetailViewController.h"

@interface DirectoryEmptyDetailViewController ()

@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;

@end

@implementation DirectoryEmptyDetailViewController

- (id)init
{
    self = [super initWithNibName:@"DirectoryEmptyDetailView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoContactSelected", @"DirectoryPlugin", nil);
}


@end
