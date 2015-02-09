/*
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//  Created by Loïc Gardiol on 06.10.14.

#import "RecommendedAppsForCategoryCell.h"

#import "RecommendedAppCollectionViewCell.h"

static NSString* const kCellsReuseIdentifier = @"RecommendedApp";

@interface RecommendedAppsForCategoryCell ()<UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, weak) IBOutlet UICollectionView* collectionView;

@property (nonatomic, copy) NSArray* recommendedApps;
@property (nonatomic, strong) RecommendedAppCategory* category;
@property (nonatomic, copy) void (^appTappedBlock)(RecommendedApp* app);

@end

@implementation RecommendedAppsForCategoryCell

#pragma mark - Init

- (instancetype)initWithRecommendedApps:(NSArray*)recommendedApps forCategory:(RecommendedAppCategory*)category appTappedBlock:(void (^)(RecommendedApp* app))appTappedBlock {
    [PCUtils throwExceptionIfObject:recommendedApps notKindOfClass:[NSArray class]];
    [PCUtils throwExceptionIfObject:category notKindOfClass:[RecommendedAppCategory class]];
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.recommendedApps = recommendedApps;
        self.category = category;
        self.appTappedBlock = appTappedBlock;
        [self.collectionView registerNib:[UINib nibWithNibName:NSStringFromClass([RecommendedAppCollectionViewCell class]) bundle:nil] forCellWithReuseIdentifier:kCellsReuseIdentifier];
    }
    return self;
}

#pragma mark - UIView overrides

- (void)layoutSubviews {
    [super layoutSubviews];
    UICollectionViewFlowLayout* layout = (UICollectionViewFlowLayout*)(self.collectionView.collectionViewLayout);
    if (self.recommendedApps.count == 1) {
        layout.itemSize = CGSizeMake(self.collectionView.frame.size.width - layout.sectionInset.left - layout.sectionInset.right, layout.itemSize.height);
    } else {
        layout.itemSize = CGSizeMake(ceilf(0.66 * self.collectionView.frame.size.width), layout.itemSize.height);
    }
    self.collectionView.collectionViewLayout = layout;
}

#pragma mark - Public

+ (CGFloat)preferredHeight {
    return 110.0;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    RecommendedApp* app = self.recommendedApps[indexPath.item];
    if (self.appTappedBlock) {
        self.appTappedBlock(app);
    }
    __weak __typeof(self) welf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [welf.collectionView deselectItemAtIndexPath:indexPath animated:YES];
    });
}

#pragma mark - UICollectionViewDataSource

- (UICollectionViewCell*)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    RecommendedAppCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:kCellsReuseIdentifier forIndexPath:indexPath];
    cell.app = self.recommendedApps[indexPath.item];
    return cell;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.recommendedApps.count;
}

@end
