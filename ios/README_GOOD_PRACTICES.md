#Good practices for iOS and OSX development#

Author : Loïc Gardiol

The purpose of this document is document is to expose some good structural and coding practices when developing for iOS and OSX.

##Third-party libraries##

###Use CocoaPods [http://cocoapods.org]###

Dependencies management is a hassle. CocoaPods makes it easy by installing and keeping them up-to-date (almost) automatically. In short, the original Xcode project is transformed into an Xcode workspace, made of the original Xcode project plus an additional project Pods that includes the sources of the third-party libraries.

The dependencies (libraries that CocoaPods should install) are described in a _Podfile_, stored in the workspace root folder. This file contains the names of all third-party libraries that should be included. Of course, those libraries must be compatible with CocoaPods, but it is often the case. CocoaPods (`pod` command) should be installed on the machine, so that `pod install` can be run at project root, to install/update the dependencies.

**Important: open `<ProjectName>.xcworkspace`** instead of `<ProjectName>.xcodeproj` after having installed CocoaPods.

*Speaking of third-party libraries: if your app has any network activity (i.e. makes network requests), it is strongly encouraged to use [AFNetworking](http://afnetworking.com). It is very popular and constantly updated.*

###External frameworks (not CocoaPods supported)###

Third-party libraries that are not supported by CocoaPods should be put in `/External frameworks/`and imported into the corresponding group in Xcode.

##MVC##
###Views & controllers###
####Files####

iOS follows the  [Model View Controller pattern](http://developer.apple.com/library/ios/#documentation/general/conceptual/devpedia-cocoacore/MVC.html). Each class/file is stored within the corresponding folder (or subfolder) or Misc folder if none of the three is appropriate.

If you do not use Storyboards (which is likely to be the case if your project is rather complex), use XIBs. If both a controller and a view are to be created, the naming convention is the following:

* **Controller:** `MyCoolViewController.h/m`

	* Placed in a `Controllers` folder (or subfolder)

* **View:**	`MyCoolView.xib`
		
	* Placed in a `Views` folder (or subfolder)

By default, when creating a `UIViewController` subclass and checking "with XIB for User Interface", Xcode names the view like `MyCoolViewController.xib` which is not good because this XIB is a view, not a controller, and it is placed within the same folder (instead of Controllers and Views respectively).

So when creating a `UIViewController` subclass, **uncheck** "with XIB for User Interface" and create it yourself afterwards (new File -> User Interface -> View). This is an extra-step but is cleaner regarding MVC.

####Code####

Each view controller subclass should override at least `init` (or more custom method like `initWithPatient:` for example). In the override, instantiate the view controller with the related view.

	- (id)init {
		self = [super initWithNibName:@"SettingsView" bundle:nil];
		if (self) {
			//init code
		}
		return self;
	}

and then instantiate simply by doing:

	[[SettingsViewController alloc] init]	or	[SettingViewController new] (contraction)

This is better than doing:

	[[SettingsViewController alloc] initWithNibName:@”SettingsView” bundle:nil]

Because:

* Encapsulation: the class that instantiates `SettingViewsController` does not need to know how the view is created
* Logic: `SettingsViewController` is coded (most likely) to support the `SettingsView` only
* Repetition: if the name of `SettingsView` had to be changed, it should be changed at all instantiation places, instead of just in init method.

####Images####

Images are stored in `Views/Images`. Each image should have two versions: the standard 1:1 pixel ratio for non-retina devices and the retina version 2:1 which is 4 times the size (twice the width and twice the height) and post-fixed by `@2x.ext` 
Example: `MyImage.png` and `MyImage@2x.png`. iOS automatically picks the right version based on the device that runs the app.

##Objective-C conventions##

###General syntax####

####Methods####

Compared to Java for example, in Objective-C conventions, getters **method are not prefixed with `get`**. Also, arguments are named, and the whole **method definition should be "readable" like a sentence.** 

So for example, for a method that returns medications for a specific patient:


<span style="color:red;">**bad:** `-(NSArray*)getMedications:(Patient*)patient;`</span>


<span style="color:green;">**GOOD** `-(NSArray*)medicationsForPatient:(Patient*)patient;`</span>

####If blocks####

**Always put braces, even if the code inside is only 1-line long.** This is mainly for maintenance: it is a well-known trap for the next developer to come later, add another line, and forget to add the braces. Spend two seconds and help your successor save minutes (or even hours).

Often, 1-line if-statements are conditional value assignments. Use the very clean ternary operator for that !

	self.name = self.isNew ? @"New" : @"Old";
	
####Use literals####

Modern Objective-C brings many syntactic sugar to help developers write less code to do the same. Here are the most useful literals:

**NSArray**	

	NSArray* array = @[obj1, obj2];
	id obj = array[0];
	
	//is equivalent to
	
	NSArray* array = [NSArray arrayWithObjects:obj1, obj2, nil];
	id obj = [array objectAtIndex:0];
	
**NSDictionary**

	NSDictionary* dictionary = @{@"key1":obj1, @"key2":obj2};
	id obj = dictionary[@"key1"]
	
	//is equivalent to

	[NSDictionary dictionaryWithObjectsAndKeys:@"key1", obj1, @"key2", obj2];
	id obj = [dictionary objectForKey:@"key1"]
	
**NSNumber**

	NSNumber* number = @42;
	NSNumber* boolean = @YES;
	
	//is equivalent to

	NSNumber* number = [NSNumber numberWithInteger:42];
	NSSNumber* boolean = [NSNumber numberWithBool:YES];

###Use properties###

Avoid using instance variable of the form:

	@interface MyClass : NSObject {
		NSString* oldSchoolInstanceVariable;
	}

Use properties instead:

	@property (nonatomic, strong) NSString* modernInstanceVariable;

Then, access them from within your class with `self.propertyName` and set them the same `self.propertyName = ...`

####Define private properties in class extension in .m file####

Unless they are needed as public accessors, declare the properties inside the .m file, in a class extension (which is actually a private category on your class).

	#import "MyClass.h"

	@interface MyClass ()

	@property (nonatomic, strong) NSString* aVariable;

	@end

	@implementation MyClass

	//implementation...

	@end

####Do not write @synthesize, it is done automatically ;)####

All properties _propertyName_ are by default synthesized as classic instance variable with name _\_propertyName_ (note the prefixing underscore).

**The compiler will automatically insert** ("synthesize") the getter and the setter as follow:

**Getter**

	- (propertyType)propertyName{
		return _propertyName;
	}

**Setter** (only if not readonly)

	- (void)setPropertyName:(propertyType)propertyName {
		_propertyName = propertyName;
	}

####Override getter or setter####

If you want to override the getter or the setter for a property called `alpha` for example, do it like this (e.g. setter here):

	- (void)setAlpha:(CGFloat)alpha {
		_alpha = alpha;
		//other custom actions
	}

The compiler will understand it and will __not__ insert the default setter.

####Readonly properties####

If you want a property to be _only_ a getter (readonly) add the *readonly* attribute.

__MyClass.h__
	
	@interface MyClass : NSObject

	@property (nonatomic, readonly) NSString* myVariable;

	@end


If you still want this property to be a private setter as well, simply declare it as readwrite in your private class extension (see above).

__MyClass.m__
	
	@interface MyClass ()

	@property (nonatomic, readwrite) NSString* myVariable;

	@end

	@implementation MyClass

	//implementation...

	@end

Yes, you declare it twice. It's not very nice, but it's how it's done commonly.

###Forget NullPointerExceptions###

**In Objective-C, calling anything on `nil` simply returns `nil`**. No exception is thrown.

Put differently, depending on the context, it is perfectly fine *not* to check for `nil` before an action on a object. This helps keep the code cleaner.

So imagine for example that you want to know how many objects are in an array, but you don't know whether this array is initialized:

	/*
	 * Returns 0 (== nil) both if self.elements is nil
	 * or self.elements contains 0 element.
	 */
	NSUInteger count = self.elements.count;

###Types###

####Booleans####

In Objective-C, booleans are `YES` and `NO`, **not** `true/false`.

####Numbers#####

Use typedefs defined by Apple in their APIs for primitive types. For example:

~~**float** frameHeight = 3.0~~		-->	**CGFloat** frameHeight = 3.0

~~**int** count = [array count]~~	-->	**NSUInteger** count = [array count]


Use `NSNumber` to store numbers as objects.

####`nil` vs `NULL`####

* Use `nil` to indicate a pointer to a null Objective-C instance
* Use `NULL` to indicate a null double-pointer, a null block, a null structure, ... or anything that is purely C related

**Tip:** you don't need check for nil before calling a method on an object (like in Java for example). In Objective-C it simply does nothing if you do so (no exception). This makes the code lighter :)

###Dealloc###

Since ARC (Automatic Reference Counting), overriding `dealloc` is not  necessary to `release` (free) instance variables/properties since it's done automatically. **BUT** it does not mean that `dealloc` is never overridden.

	- (void)dealloc {
		//Typically cancel operations that have been started by this instance
		//Or remove the instance as observer
	}

You still need to do it in some scenarios:

* Your instance is registered as observer, on `NSNotificationCenter` for example. Then you need to do `[[NSNotificationCenter defaultCenter] removeObserver:self]`.
* Your instance has started a background operation (for e.g. network) that will call it back later, via selector or block for example. Then you might want to cancel this operation when your instance is deallocated.

###Comment your class structure with #pragma ###

So that your class structure is more readable, you should add "chapters" to it. It declares the different sections. To do it, use 

**#pragma mark - section_name** (line in class explorer) 

or 

**#pragma mark section_name** (no line in class explorer)

For example:

	#pragma mark - Inits

	- (void)initWith.... {
		....
	}

	#pragma mark - Data refresh

	- (void)refresh {
		....
	}

	#pragma mark - UITableViewDataSource

	- (UITableViewCell*)tableView:..... {
		....
	}

	#pragma mark - Dealloc

	- (void)dealloc {
		....
	}

Xcode understands these declarations and they appear in bold in the class explorer.

##Debugging##

###Xcode breakpoints are powerful###

You probably already know about `NSLog(@"...", ...)`. Well it's great for _logging_.

But Xcode provides very powerful breakpoints, that do not only stop the code, but can precisely print log message, variables values, stop only after X passages, break on exceptions, etc.

Breakpoints can be either:

* **Private** (linked to your user/machine): only you will see them in the breakpoints explorer. That's great to have personal NSLog in a sense
* **Public:** everybody can see and use them

[A great tutorial on Xcode breakpoints](http://www.raywenderlich.com/28289/debugging-ios-apps-in-xcode-4-5)

###Use #warning ###

If you want to leave some logic for later as TODO, or insert a piece of code that should be there only for debugging purposes, use `#warning`.

For example:

	#warning TODO deal with error

	#warning REMOVE this code

This will generate a warning a compile time and so you don't forget about this part of the code.

	