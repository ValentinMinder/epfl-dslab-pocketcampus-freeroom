//
//  CompundTableCell.h
//  PocketCampus
//
//  Created by Susheng on 5/2/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CompundTableCell : NSObject
@property int type;
@property (strong, nonatomic) NSDictionary* data;
@property (strong, nonatomic) NSString* content;
@property (strong, nonatomic) NSString* time;
@property (strong, nonatomic) NSString* name;

- (id)initWithDic:(NSDictionary*)data Type:(int)type Content:(NSString*)content Time:(NSString*)time Name:(NSString*)name;

@end
