//
//  __PluginID__Service.m
//  PocketCampus
//
//

#import "__PluginID__Service.h"

@implementation __PluginID__Service

static __PluginID__Service* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"__PluginID__Service cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"__PluginID_low__" thriftServiceClientClassName:NSStringFromClass(__PluginID__ServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark - Service methods

// ----------------------------------------  TODO ----------------------------------------  //
// Implement async methods declared in header (__PluginID__Service.h)
// This is done very easily by using the ServiceRequest class.
// 
// Examples (see definitions in __PluginID__Service.h)
//
// 1) Method with 0 argument:
//  - (void)getMealsWithDelegate:(id)delegate {    
//      ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
//      operation.serviceClientSelector = @selector(getMeals); //corresponds to Thrift method definition
//      operation.delegateDidReturnSelector = @selector(getMealsDidReturn:); //corresponding *didReturn* definition
//      operation.delegateDidFailSelector = @selector(getMealsFailed); //corresponding *Failed* definition
//      operation.returnType = ReturnTypeObject; //result type. Can be object or any standard primitive types (ReturnTypeInt, ...)
//      [self.operationQueue addOperation:operation]; //schedule operation in background
//      [operation release]; //If you do NOT use ARC: release operation (has been retained by operationQueue)
//  }
//
// 2) Method with arguments:
//  - (void)setRatingForMeal:(Id)mealId rating:(double)rating delegate:(id)delegate {
//      ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
//      operation.serviceClientSelector = @selector(setRating::); //notice double columns, because Thrift setRating has 2 arguments
//      operation.delegateDidReturnSelector = @selector(setRatingForMeal:rating:didReturn:);
//      operation.delegateDidFailSelector = @selector(setRatingFailedForMeal:rating:);
//      [operation addLongLongArgument:mealId]; //add arguments in order in which they appear
//      [operation addDoubleArgument:rating];
//      operation.returnType = ReturnTypeInt; //return type is int this time
//      [self.operationQueue addOperation:operation]; //schedule operation in background
//      [operation release]; //If you do NOT use ARC: release operation (has been retained by operationQueue)
//  } 
//
// --------------------------------------------------------------------------------------  //


#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
