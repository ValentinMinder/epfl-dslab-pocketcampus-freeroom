//
//  TableCell.m
//  PocketCampus
//
//  Created by Susheng on 12/21/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TableCell.h"

@implementation TableCell

- (id)initWithQuestionid:(int)questionid Content:(NSString*)content {
    if (self=[self init]) {
        self.questionid = questionid;
        self.content = content;
        self.data = nil;
    }
    return self;
}

- (id)initWithDic:(NSDictionary *)data Type:(int)type Content:(NSString *)content {
    if (self=[self init]) {
        self.data = data;
        self.content = content;
        self.questionid = type;
    }
    return self;
}

@end
