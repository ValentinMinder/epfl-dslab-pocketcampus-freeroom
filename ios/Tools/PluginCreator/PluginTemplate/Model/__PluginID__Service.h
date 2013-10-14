//
//  __PluginID__Service.h
//  PocketCampus
//
//  Created by Loïc Gardiol (loic.gardiol@gmail.com)
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "__PluginID_low__.h"

@interface __PluginID__Service : Service <ServiceProtocol>

// ----------------------------------------  TODO ----------------------------------------  //
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

@protocol __PluginID__ServiceDelegate <ServiceDelegate>

@optional

// ----------------------------------------  TODO ----------------------------------------  //
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
// - (void)serviceConnectionToServerTimedOut;
//
// This method obviously does not differentiate between requests. This is because it assumes that 
// if the server was not reachable, no request could have succeeded.
// --------------------------------------------------------------------------------------  //

@end
