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

//  Created by Loïc Gardiol on 03.03.13.

@import Foundation;

extern NSString* const kPocketCampusURLNoPluginSpecified;

@class MainController;

@interface PCURLSchemeHandler : NSObject


/**
 * @return YES if url has the valid format
 * i.e. pocketcampus://<pluginIdentifier>.plugin.pocketcampus.org/(<action>?<parameters>)
 * and <pluginIdentifier> exists
 * WARNING: action/parameters are not verified
 */
- (BOOL)isValidPocketCampusURL:(NSURL*)url;

/**
 * @return plugin lower identifier for URL if valid,
 * kPocketCampusURLNoPluginSpecified if the URL has a valid PocketCampus scheme but no host,
 * nil otherwise
 */
- (NSString*)pluginIdentifierForPocketCampusURL:(NSURL*)url;

/**
 * @return action for URL if valid, nil otherwise
 */
- (NSString*)actionForPocketCampusURL:(NSURL*)url;

/**
 * @return parameters for URL if valid, nil otherwise
 */
- (NSDictionary*)parametersForPocketCampusURL:(NSURL*)url;

/**
 * @return viewcontroller executing action described in:
 * pocketcampus://<pluginIdentifier>.plugin.pocketcampus.org/<action>?<parameters>
 * nil if <pluginIdentifier> does not exist or URL is invalid.
 */
- (UIViewController*)viewControllerForPocketCampusURL:(NSURL*)url;

/*
 * Private. Do not use from plugins. See MainControllerPublic to get instance.
 */
- (id)initWithMainController:(MainController*)mainController;

@end
