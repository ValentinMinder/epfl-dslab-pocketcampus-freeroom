//
//  CompundTableCell.m
//  PocketCampus
//
//  Created by Susheng on 5/2/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "CompundTableCell.h"

@implementation CompundTableCell : NSObject
- (id)initWithDic:(NSDictionary*)data Type:(int)type Content:(NSString*)content Time:(NSString*)time Name:(NSString*)name
{
    if (self=[self init]) {
        self.data = data;
        self.content = content;
        self.type = type;
        self.name = name;
        self.time = time;
    }
    return self;
}
@end
