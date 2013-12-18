//
//  TableCell.h
//  PocketCampus
//
//  Created by Susheng on 12/21/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TableCell : NSObject
@property int questionid;
@property (strong, nonatomic) NSDictionary* data;
@property (strong, nonatomic) NSString* content;


- (id)initWithQuestionid:(int)questionid Content:(NSString*)content;
- (id)initWithDic:(NSDictionary*)data Type:(int)type Content:(NSString*)content;
@end
