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

//  Created by Loïc Gardiol on 01.09.2014.

#import "PluginController.h"

#import "CloudPrintModelAdditions.h"

typedef NS_ENUM(NSInteger, CloudPrintCompletionStatusCode) {
    CloudPrintCompletionStatusCodePrintSuccess,
    CloudPrintCompletionStatusCodeUserCancelled,
    CloudPrintCompletionStatusCodeUnsupportedFile
};

typedef void (^CloudPrintCompletionBlock)(CloudPrintCompletionStatusCode printStatusCode);

@interface CloudPrintController : PluginController<PluginControllerProtocol>

/**
 * Same as sharedInstanceToRetain
 * Only indicates that sharedInstanceToRetain actually does not
 * need to be retained (singleton).
 */
+ (instancetype)sharedInstance;

/**
 * @return YES if file with localURL is accessible and has a format that is supported for printing. NO otherwise.
 */
+ (BOOL)isSupportedFileWithLocalURL:(NSURL*)localURL;

/**
 * @return a view controller that you can present to print a local or remote file. Upload will be handled and user will be able to validate/change print config and start the print. Returns nil if url is a local file that is not supported. Completion is executed with CloudPrintCompletionStatusCodeUnsupportedFile then.
 * @param url must be the URL of a readable local file or a a remote file (that will be downloaded). Cannot be nil. See isSupportedFileWithLocalURL: to know wether your local file can be printed.
 * @param printDocumentRequestOrNil you can pass a print request to pre-fill the config. The documentId attribute will be ignored.
 * @discussion you're excpected to dismiss the returned view controller when completion is executed (no matter the status code value)
 */
- (UIViewController*)viewControllerForPrintDocumentWithURL:(NSURL*)url docName:(NSString*)docName printDocumentRequestOrNil:(PrintDocumentRequest*)request completion:(CloudPrintCompletionBlock)completion;

/**
 * @return a view controller that you can present to print a document for which you already have an id for, letting user validate/change print config and start the print.
 * @param docName the name of the document, is only used to show to user that he is printing the correct thing. Not used for identification.
 * @param request documentId cannot be 0. You can create a request with default parameters using [PrintDocumentRequest createDefaultRequest] and set its id.
 * @discussion Use this method when you *already* have a PrintDocumentRequest.documentId (i.e. the document is already known by the server).
 * You're excpected to dismiss the returned view controller when completion is executed (no matter the status code value).
 * If you want to print a local file, use viewControllerForPrintDocumentWithLocalURL:docName:printDocumentRequestOrNil.
 */
- (UIViewController*)viewControllerForPrintWithDocumentName:(NSString*)docName printDocumentRequest:(PrintDocumentRequest*)request completion:(CloudPrintCompletionBlock)completion;

/**
 * @param viewController a view controller returned by one of the two methods above.
 * @discussion Must be called to release resources whenever the view controller is being dimissed/released
 * (not going to be used anymore) NOT as part of the completion block passed originally.
 * Does nothing if viewController was already cancelled or does not exist in printing view controllers.
 */
- (void)cancelPrintWithViewController:(UIViewController*)viewController;

/**
 * When plugin has been started as an extension, this property holds the etension context
 */
@property (nonatomic, strong) NSExtensionContext* extensionContext;

@end
