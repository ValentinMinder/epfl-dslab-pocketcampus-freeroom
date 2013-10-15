//
//  PCUnkownPersonViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryService.h"

@interface DirectoryPersonViewController : UIViewController

@property (nonatomic, strong) Person* person;

/*
 * If YES and person.office is not nil, office will ne displayed in a cell
 * and tapping it pushes map with office name as query.
 * If NO office info is shown as non-tappable info.
 * If delegate is not instance iteself, this property has no effect.
 * Default: YES
 */
@property (nonatomic) BOOL allowShowOfficeOnMap;


- (id)initWithPerson:(Person*)person;

/*
 * Tries to load person with fullName, as first result returned by directory search.
 * If no result, will display a message saying so.
 * WARNING: YOU take the responsability for providing a fullName that is precise enough so that the first result is the correct one.
 */
- (id)initAndLoadPersonWithFullName:(NSString*)fullName;

@end
