



//  Created by Loïc Gardiol on 15.01.13.



#import "DirectoryProfilePictureViewController.h"

#import <QuartzCore/QuartzCore.h>

#import "PCValues.h"

@interface DirectoryProfilePictureViewController ()

@property (nonatomic, strong) UIImage* image;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* imageViewTopLayoutConstraint;

@end

@implementation DirectoryProfilePictureViewController

- (id)initWithImage:(UIImage*)image
{
    self = [super initWithNibName:@"DirectoryProfilePictureView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/directory/personPicture";
        self.image = image;
        self.title = NSLocalizedStringFromTable(@"Photo", @"DirectoryPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    if (!self.navigationController) {
        self.imageViewTopLayoutConstraint.constant = 0.0;
    }
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismiss)];
    self.preferredContentSize = CGSizeMake(self.image.size.width < 500 ? self.image.size.width : 500.0, self.image.size.height < 500 ? self.image.size.height : 500.0);
    self.imageView.image = self.image;
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.preferredContentSize = self.imageView.frame.size;
    
    self.imageView.userInteractionEnabled = YES;
    UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(saveProfilePictureToCameraRoll)];
    gesture.numberOfTouchesRequired = 2;
    gesture.numberOfTapsRequired = 3;
    [self.imageView addGestureRecognizer:gesture];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)saveProfilePictureToCameraRoll {
    NSLog(@"-> Saving profile picture");
    UIImageWriteToSavedPhotosAlbum(self.image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo; {
    if (error) {
        [[[UIAlertView alloc] initWithTitle:@"Error" message:@"Error when saving image\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    } else {
        [[[UIAlertView alloc] initWithTitle:@"Image saved" message:@"Image correctly saved\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    
}

@end
