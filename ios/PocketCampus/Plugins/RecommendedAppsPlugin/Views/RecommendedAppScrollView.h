//
//  LOChatPartnerBubblesScrollView.h
//  Nowy
//
//  Created by Loïc Gardiol on 22.07.14.
//  Copyright (c) 2014 Locus. All rights reserved.
//

@class RecommendedAppThumbView;

@interface RecommendedAppScrollView : UIScrollView

@property (nonatomic, strong) NSString* title;

@property (nonatomic, strong) NSArray* appItems;

@property (nonatomic, copy) void (^appThumbTapped)(RecommendedAppThumbView* thumbView);

@end
