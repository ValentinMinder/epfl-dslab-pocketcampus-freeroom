



//  Created by Loïc Gardiol on 25.09.13.



#import <UIKit/UIKit.h>

#import "DirectoryService.h"

typedef enum {
    DirectoryPersonBaseInfoCellStyleLarge = 0,
    DirectoryPersonBaseInfoCellStyleSmall = 1
} DirectoryPersonBaseInfoCellStyle;

@interface DirectoryPersonBaseInfoCell : UITableViewCell

@property (nonatomic, strong) Person* person;

@property (nonatomic, readonly) DirectoryPersonBaseInfoCellStyle style;

@property (nonatomic, readonly) UIImage* profilePicture;

/*
 * WARNING: do not use profilePictureImageView.image to get person's profile picture, because it might be a generic one.
 * Use property profilePicture above (non-nil if actual picture exists)
 */
@property (nonatomic, strong) IBOutlet UIImageView* profilePictureImageView;

- (id)initWithDirectoryPersonBaseInfoCellStyle:(DirectoryPersonBaseInfoCellStyle)style reuseIdentifer:(NSString*)reuseIdentifier;

+ (CGFloat)heightForStyle:(DirectoryPersonBaseInfoCellStyle)style;

@end
