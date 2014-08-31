/*
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of PocketCampus.Org nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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

//  Created by ___FULLUSERNAME___ on ___DATE___.

@import Foundation;

#import "PCService.h"

#import "__PluginID_low__.h"

@interface __PluginID__Service : PCService <PCServiceProtocol>

// ---------------------------------------- TODO ----------------------------------------  //
// Prototype an async method for each method provided by Thrift interface, defined in protocol
// __PluginID__Service in file __PluginID_low__.h
//
// Examples:
//
// 1) Method with 0 argument:
// If the Thrift method is: - (NSArray*)getMeals;
// Your definition must be: - (void)getMealsWithDelegate:(id)delegate;
// 
// 2) Method with arguments:
// If the Thrift method is: - (int)setRating:(Id)mealId:(double)rating;
// Your definition must be: - (void)setRatingForMeal:(Id)mealId rating:(double)rating delegate:(id)delegate;
// --------------------------------------------------------------------------------------  //

@end

@protocol __PluginID__ServiceDelegate <PCServiceDelegate>

@optional

// ---------------------------------------- TODO ----------------------------------------  //
// Prototype 2 delegate methods (*didReturn* and *Failed*) for each service method defined above.
// 
// IMPORTANT: 
// * The *didReturn* method MUST contain ALL arguments of service method (minus delegate) plus the didReturn:(type)result at the end.
// * The *Failed* method MUST contain ALL arguments of service method (minus delegate).
// 
// (WHY? So that the delegate can differentiate between 2 results when it has called the same service method
// twice with different arguments.)
//
// Examples (see above for corresponding service methods):
//
// 1) Method with 0 argument:
// Your *didReturn* definition must be: - (void)getMealsDidReturn:(NSArray*)meals;
// Your *Failed* definition must be: 		- (void)getMealsFailed;
// 
// 2) Method with arguments:
// Your *didReturn* definition must be: - (void)setRatingForMeal:(Id)mealId rating:(double)rating didReturn:(int)status;
// Your *Failed* definition must be: 		- (void)setRatingFailedForMeal:(Id)mealId rating:(double)rating;
//
// HANDLE CONNECTION TIMEOUT:
// *Failed* methods will be called on delegate if *the server WAS reachable but returned an error*
// On the other hand, if the server was NOT reachable (timeout), a generic method will be called on delegate:
//
// - (void)serviceConnectionToServerFailed;
//
// This method obviously does not differentiate between requests. This is because it assumes that 
// if the server was not reachable, no request could have succeeded.
// --------------------------------------------------------------------------------------  //

@end
