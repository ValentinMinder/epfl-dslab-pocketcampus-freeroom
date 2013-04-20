//
//  CamiproViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CamiproService.h"

#import "AuthenticationController.h"

#import "authentication.h"

@interface CamiproViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate, UIAlertViewDelegate, AuthenticationCallbackDelegate, CamiproServiceDelegate>

@end
