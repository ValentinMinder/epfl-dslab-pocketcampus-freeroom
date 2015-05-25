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

//  Created by Loïc Gardiol on 06.03.13.

@import UIKit;

@interface PCTableViewAdditions : UITableView

#pragma mark - Cell remote images

/**
 * If image for URL is cached, sets cell image immediately, using cellsImageViewSelectorString to know which image view
 * to update. If not cached, starts a request for the image and sets it when request returns using indexPath to get pointer to cell.
 * If the request fails (no internet, timeout, etc.) the indexPath is marked as failed and started again later (when internet comes
 * back for e.g.).
 * IMPORTANT: if you reloadData/Sections/Rows, add, remove or reorder rows, requests are cancelled and
 * failed index paths are invalidated. It is *your* responsability to call this method again (typically in cellForRowAtIndexPath).
 * IMPORTANT 2: the 3 following methods are *not* thread-safe
 * IMPORTANT 3: if temporaryImage (below) is nil, it is *your* responsability to clear the cell's image when reusing it,
 * otherwise a wrong image will be temporarily displayed until the url request for the new one completes.
 */
- (void)setImageURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath;

/**
 * Cancels image download request for all indexPaths.
 * Does not do anything if no request exists (or finished) for this indexPath.
 */
- (void)cancelImageDownloadForIndexPaths:(NSArray*)indexPaths;

/**
 * Cancels image download request for all indexPaths in each sections.
 * Does not do anything if no request exists (or finished) for this indexPath.
 */
- (void)cancelImageDownloadForSections:(NSIndexSet*)sections;

/**
 * Cancels all image download requests
 */
- (void)cancelAllImageDownloads;

/**
 * Image that is displayed while image download is still in progress
 * or if the download fails or returns empty data.
 */
@property (nonatomic, strong) UIImage* temporaryImage;

/**
 * If image as been downloaded, returns it, nil otherwise.
 * If imageProcessingBlock is not NULL, this method returns the processed image.
 * INFO: can return nil if image was downloaded but later evicted because of memory limitations
 */
- (UIImage*)cachedImageAtIndexPath:(NSIndexPath*)indexPath;

/**
 * Same as previous but returns image as it was before being processed by imageProcessingBlock
 * INFO: can return nil if image was downloaded but later evicted because of memory limitations
 */
- (UIImage*)cachedRawImageAtIndexPath:(NSIndexPath*)indexPath;

/**
 * Specify the keypath of the UIImageView in the cells.
 * Default: @"imageView"
 */
@property (nonatomic, strong) NSString* cellsImageViewSelectorString;

/**
 * This block will be executed when image is downloaded but before caching it.
 * If this block is not NULL, returned image replaces downloaded image.
 * IMPORTANT 1: when set, all existing (if any) cached images are discarded and re-processed on main queue from rawImage.
 * IMPORTANT 2: if you use table view's row height, be sure to use tableView.rowHeight in the block and not a pre-computed value
 * if you want support for automatic content size update (see below)
 * Default: NULL
 */
typedef UIImage* (^ImageProcessingBlock)(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image);
@property (nonatomic, copy) ImageProcessingBlock imageProcessingBlock;

#pragma mark - Content size updates

/**
 * This section adds features to support user content size category changes (UIContentSizeCategoryDidChangeNotification)
 * The sequence is the following:
 * 1) contentSizeCategoryDidChangeBlock is executed if not NULL.
 * 2) rowHeightBlock is executed if not NULL.
 * 3) imageProcessingBlock is executed again for all cached images, if reprocessesImageWhenContentSizeCategoryChanges is YES.
 * 4) [self reloadData] is executed if reloadsDataWhenContentSizeCategoryChanges is YES.
 */

/**
 * 1)
 * Default: NULL
 */
typedef void (^ContentSizeCategoryDidChangeBlock)(PCTableViewAdditions* tableView);
@property (nonatomic, copy) ContentSizeCategoryDidChangeBlock contentSizeCategoryDidChangeBlock;

/**
 * 2)
 * Should return rowHeight for tableView.
 * Automatically sets tableView.rowHeight to result of block when set.
 * Default: NULL
 */
typedef CGFloat (^RowHeightBlock)(PCTableViewAdditions* tableView);
@property (nonatomic, copy) RowHeightBlock rowHeightBlock;

/**
 * 3)
 * Default: NO
 */
@property (nonatomic) BOOL reprocessesImagesWhenContentSizeCategoryChanges;

/**
 * 4)
 * Default: YES
 */
@property (nonatomic) BOOL reloadsDataWhenContentSizeCategoryChanges;

/**
 * Use this method to get for cells a reuseIdentifier that is automatically modified
 * when the cell should be reconstructed (typically when content size category changes).
 * Returns nil if identifier is nil.
 */
- (NSString*)autoInvalidatingReuseIdentifierForIdentifier:(NSString*)identifier;

#pragma mark - Other utils

/**
 * Saves (in memory) current contentOffset under identifier.
 * identifier cannot be nil
 * You can the user restoreContentOffsetForIdentifier: to restore the contentOffset
 * This method automatically deals with the portrait/landscape problem.
 * @return a dictionary that represents the state, and that you can persist (user defaults compatible) if needed,
 * to pass it later to restoreContentOffsetWithStateDictionary:
 */
- (NSDictionary*)saveContentOffsetForIdentifier:(NSString*)identifier;

/**
 * See saveContentOffsetForIdentifier:
 * identifier cannot be nil
 * Does nothing if not saved state found for identifier
 */
- (void)restoreContentOffsetForIdentifier:(NSString*)identifier;

/**
 * Similar to restoreContentOffsetForIdentifier: except that yourself take care
 * or providing the content offset state. This info must have been built previously
 * by saveContentOffsetForIdentifier:.
 * Does nothing if stateDictionary is invalid.
 */
- (void)restoreContentOffsetWithStateDictionary:(NSDictionary*)stateDictionary;

@end
