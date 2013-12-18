//
//  QAFListCell.m
//  PocketCampus
//
//  Created by Susheng on 12/24/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "QAFListCell.h"

@implementation QAFListCell


- (id)initWithContent:(NSString *)content Data:(NSString *)data {
    if (self=[self init]) {
        self.data = data;
        self.content = content;
    }
    return self;
}

@end
