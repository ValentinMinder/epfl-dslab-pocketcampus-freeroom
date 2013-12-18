//
//  QAFListCell.h
//  PocketCampus
//
//  Created by Susheng on 12/24/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QAFListCell : NSObject

@property (strong, nonatomic) NSString* content;
@property (strong, nonatomic) NSString* data;

- (id)initWithContent:(NSString*)content Data:(NSString*)data;
@end
