//
//  __PluginID__Service.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "__PluginID_low__.h"

@interface __PluginID__Service : Service <ServiceProtocol>

//TODO: prototype methods with delegate for each method of __PluginID__Service.h   

@end

@protocol __PluginID__ServiceDelegate <ServiceDelegate>

@optional
//TODO: prototy 2 callbacks methods (success and failure) for each service method defined above  

@end
