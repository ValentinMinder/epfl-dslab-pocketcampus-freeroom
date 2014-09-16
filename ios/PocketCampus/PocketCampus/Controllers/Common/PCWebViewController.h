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

//  Created by Loïc Gardiol on 14.06.14.

@interface PCWebViewController : UIViewController

/**
 * @return web view controller loading url
 * @param url url to load
 * @param title optional title to us as view controller's title
 * @discussion: important: PCWebViewController should be used with a navigation controller
 */
- (instancetype)initWithURL:(NSURL*)url title:(NSString*)title;

/**
 * @return web view controller showing the HTML.
 * @param url url to load
 * @param title optional title to us as view controller's title
 * @discussion: important: PCWebViewController should be used with a navigation controller
 */
- (instancetype)initWithHTMLString:(NSString*)htmlString title:(NSString*)title;

/**
 * If YES, each request will hit MainController viewControllerForURL:
 * if a view controller is available, it will automatically be pushed on the navigation controller
 * Default: YES
 */
@property (nonatomic) BOOL automaticallyHandlesInternallyRecognizedURLs;

/**
 * Equivalent of UIWebViewDelegate webView:shouldLoad...:
 * Called *after* dealing with automaticallyHandlesInternallyRecognizedURLs if URL was not recognized.
 * Defaut: nil (default behavior)
 */
@property (nonatomic, copy) BOOL (^shouldLoadRequest)(NSURLRequest* request, UIWebViewNavigationType navigationType);

@end
