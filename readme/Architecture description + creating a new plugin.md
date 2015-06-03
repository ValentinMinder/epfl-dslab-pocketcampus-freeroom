# The PocketCampus architecture
 
PocketCampus is a split-application, meaning it has a server-side code and a client-side code. Subsequently, each plugin should typically have its corresponding server-side and a client-side code.  
The communication between client and server is handled by the Thrift framework. Thrift is a kind of multi-platform RPC technology. You can read more about it on [its website](http://thrift.apache.org/).

The PocketCampus server is a kind of universal proxy. It has a crucial role since it unifies the communication between the different phones and different service providers. EPFL offers many IT services, which unfortunately speak different protocols. This would introduce a big mess if we were to talk to them directly from each mobile platform.  
The PocketCampus server talks to each service provider using its own language/protocol, and communicates with the different clients using Thrift.

Normally all communication performed by the PocketCampus application should go through the PocketCampus server. The latter is the application’s gateway to the whole world. The only exception exists in the authentication plugin, which, for security reasons, sends the user credentials (username and password) directly to Tequila over HTTPS.

## Android & Server

A set of features implemented on top of PocketCampus is called a PocketCampus plugin. Moodle, Camipro, and News are example of plugins in PocketCampus. Usually (but not always) a plugin has an icon on the dashboard. Exceptions are the authentication plugin and the dashboard itself, which is a plugin.

PocketCampus plugins can be executed as standalone applications, both on the server and on Android. You just have to reference the PocketCampus SDK as a library. The PocketCampus SDK is a set of Java and Android projects that let’s you develop a plugin for PocketCampus. The PocketCampus SDK consists of exactly three projects: platform.android, platform.server and platform,.shared, which respectively contain the android code, the server code, and the code that needs to be shared between the client and the server.

To develop a PocketCampus plugin you should create three new projects: the android, the server, and the shared projects. You should reference the SDK projects from your newly created project. The plugins are self contained. By creating those three projects you have created an Android app (consisting of a single plugin) that you can install on your phone, and a server program (also consisting of a single plugin) that you can run locally on your machine. This technique (running a standalone plugin) is heavily used for development purposes as it simplifies the process, since you don’t have to care about other plugins while you’re developing your own plugin. For your newly created plugin to work you need to have a config file on your sdcard that overrides the internal config of the app in order to make the mobile app connect to your own instance of the server. This file should be named pocketcampus.config and it sits on the root of the sdcard. Contact Amer if you want a sample config file.

To simplify the task of creating a new plugin, you can use a Gradle task: `gradle newPlugin -PpluginName=your_plugin_name`.

Here are the step-by-step details of how to create and develop a plugin for PocketCampus:

 1. Get the PocketCampus code
 2. Import the 3 SDK projects into your favorite IDE
 4. Write the thrift definition file for your own plugin, put the file in the def diretory of your plugin's shared project, then run `gradlew plugin-your_plugin_name-shared:generateThriftSource`.
 5. Write your code, and run your app
 6. It is that easy!

# iOS 

The plugin creation for iOS clients is a bit different than on Android. The PocketCampus app is made of one Xcode project. The classes and files architecture is however well defined. It is mainly based on the pattern Model-View-Controller (MVC).

From repository's root, the Xcode project is located in: ios/PocketCampus. The plugins are logically stored in the Plugins folder, and the naming convention is <PluginName>Plugin.

Within each plugin's folder, there is standard folders structure:

 * Controllers: this folder contains all the controllers, with at least the <PluginName>Controller class that is a subclass of PluginController.This class is the main gate to the plugin from the app core and other plugins.
 * Model: this folder contains all the model classes and the Thrift services and types (in the ThriftTypes+Services subfolder). You may also add here utility classes, like <PluginName>Utils.
 * Views: this folder contains all classes and elements that directly represent user interface elements. (Subclasses of UIView, XIBs, PNG, ...)

To facilitate the creation of new plugins, a script create_plugin.py that creates the above structure and skeletons classes is provided. It is located in ios->iphone->Tools->PluginCreator.

 1. cd into the PluginCreator folder
 2. Run `python create_plugin.py PluginName`. PluginName should be in CamelCase. This will create a folder <PluginName>Plugin into ios/PocketCampus/Plugins
 3. At that point, put the Thrift Objective-C generated files into ios/iphone/PocketCampus/Plugins/<PluginName>Plugin/Model/ThriftTypes+Services/
 4. Still from the PluginCreator folder, run `python add_plugin_to_xcode.py ../../PocketCampus/Plugins/<PluginName>Plugin`. This will add the plugin to the Xcode project.


At this point, the plugin has been added to Xcode. There are still a few steps to complete to finish the integration.

 1. Localize strings file: All strings of the UI should be localized in French and English and put in strings files. By default, the created structure comes with one .strings file for english language. To make this file recognized as a localized file, select it in Xcode, and click on "Make localized" button in the right column. To add the French localization, simply check it.
 2. Complete <PluginName>Controller and <PluginName>Service classes
 3. If needed, put plugin's icon with name <PluginName>.png and <PluginName>@2x.png (Retina icon) into Views/Images/
 4. Last but not least, add an entry for the plugin in PocketCampus/Plugins.plist (Supporting Files group). To do so, copy/paste a row and under pluginIdentifier put <PluginName>

## Windows Phone

The Windows Phone client uses Microsoft's recommended "Model-View-ViewModel" (MVVM) architecture, using a custom framework called [ThinMvvm](https://github.com/SolalPirelli/ThinMvvm). It does not use the official Thrift libraries, because the C# ones are very buggy; instead, a custom Thrift library called [Thrift#](https://github.com/SolalPirelli/ThriftSharp).

Plugins on Windows Phone are represented by two projects: one Portable Class Library (PCL) containing the models, viewmodels, and services, and one Windows Phone project containing the views.

To add a new plugin, simply create these two projects from Visual Studio. One catch: you have to edit the Windows Phone project's `csproj` file and set the language to `en` instead of `en-US`.

Unless you have no other choice, do not use bitmap files in the Windows Phone client, as they do not scale properly on different resolutions; prefer vector icons declared in XAML.