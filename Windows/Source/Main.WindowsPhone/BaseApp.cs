using System;
using System.Diagnostics;
using System.IO.IsolatedStorage;
using System.Windows;
using System.Windows.Markup;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace PocketCampus.Main
{
    /// <summary>
    /// Base application class that does most of the boilerplate, and abstracts away some concepts.
    /// </summary>
    public abstract class BaseApp : Application
    {
        private const string FirstRunSettingKey = "MVVM_FirstRun";

        /// <summary>
        /// Gets the current BaseApp instance.
        /// </summary>
        public new static BaseApp Current
        {
            get { return (BaseApp) Application.Current; }
        }

        /// <summary>
        /// Gets the root frame of the app.
        /// </summary>
        public PhoneApplicationFrame RootFrame { get; private set; }

        /// <summary>
        /// Creates a new BaseApp.
        /// </summary>
        public BaseApp()
        {
            UnhandledException += OnUnhandledException;

            RootFrame = CreateRootFrame();
            RootFrame.Navigated += OnNavigated;
            RootFrame.NavigationFailed += OnNavigationFailed;

            ApplicationLifetimeObjects.Add( new PhoneApplicationService() );

            InitializeLanguage();
        }

        /// <summary>
        /// Creates the root frame of the app.
        /// </summary>
        protected abstract PhoneApplicationFrame CreateRootFrame();

        /// <summary>
        /// Initializes the app, e.g. by binding interfaces to concrete types and ViewModels to Views.
        /// </summary>
        protected abstract void Initialize();

        /// <summary>
        /// Called when the app runs for the very first time after installation.
        /// </summary>
        protected abstract void OnFirstRun();

        /// <summary>
        /// Gets the language and flow direction of the app.
        /// </summary>
        protected abstract Tuple<string, string> GetLanguageAndFlowDirection();


        /// <summary>
        /// Occurs when a navigation fails.
        /// </summary>
        protected virtual void OnNavigationFailed( object sender, NavigationFailedEventArgs e )
        {
            if ( !e.Handled && Debugger.IsAttached )
            {
                Debugger.Break();
            }
        }

        /// <summary>
        /// Occurs when an unhandled exception is raised.
        /// </summary>
        protected virtual void OnUnhandledException( object sender, ApplicationUnhandledExceptionEventArgs e )
        {
            if ( !e.Handled && Debugger.IsAttached )
            {
                Debugger.Break();
            }
        }

        /// <summary>
        /// Sets the root frame's language and flow direction from the resources.
        /// </summary>
        private void InitializeLanguage()
        {
            try
            {
                var langAndDirection = GetLanguageAndFlowDirection();
                RootFrame.Language = XmlLanguage.GetLanguage( langAndDirection.Item1 );
                RootFrame.FlowDirection = (FlowDirection) Enum.Parse( typeof( FlowDirection ), langAndDirection.Item2 );
            }
            catch
            {
                if ( Debugger.IsAttached )
                {
                    Debugger.Break();
                }

                throw;
            }
        }

        /// <summary>
        /// Occurs after the first navigation.
        /// </summary>
        private void OnNavigated( object sender, NavigationEventArgs e )
        {
            // Set the root visual to allow the application to render
            RootVisual = RootFrame;
            Initialize();
            RootFrame.Navigated -= OnNavigated;

            // Do first run stuff now, message boxes (a common use case) can't be displayed before
            bool dummy;
            if ( !IsolatedStorageSettings.ApplicationSettings.TryGetValue( FirstRunSettingKey, out dummy ) )
            {
                OnFirstRun();
                IsolatedStorageSettings.ApplicationSettings.Add( FirstRunSettingKey, false );
            }
        }


        /// <summary>
        /// Settings used when a debugger is attached.
        /// </summary>
        protected static class DebugSettings
        {
            /// <summary>
            /// Gets or sets a value indicating whether frame rate counters are enabled or disabled.
            /// </summary>
            public static bool EnableFrameRateCounter
            {
                get { return Application.Current.Host.Settings.EnableFrameRateCounter; }
                set { DoWhenDebuggerAttached( () => Application.Current.Host.Settings.EnableFrameRateCounter = value ); }
            }

            /// <summary>
            /// Gets or sets a value indicating whether areas of the app that are being redrawn in each frame should be displayed specially.
            /// </summary>
            public static bool EnableRedrawRegions
            {
                get { return Application.Current.Host.Settings.EnableRedrawRegions; }
                set { DoWhenDebuggerAttached( () => Application.Current.Host.Settings.EnableRedrawRegions = value ); }
            }

            /// <summary>
            /// Gets or sets a value indicating whether the areas of a page that are handed off to the GPU should be displayed with a colored overlay.
            /// </summary>
            public static bool EnableCacheVisualization
            {
                get { return Application.Current.Host.Settings.EnableCacheVisualization; }
                set { DoWhenDebuggerAttached( () => Application.Current.Host.Settings.EnableCacheVisualization = value ); }
            }

            /// <summary>
            /// Gets or sets the idle detection mode for the user.
            /// </summary>
            public static IdleDetectionMode UserIdleDetectionMode
            {
                get { return PhoneApplicationService.Current.UserIdleDetectionMode; }
                set { DoWhenDebuggerAttached( () => PhoneApplicationService.Current.UserIdleDetectionMode = value ); }
            }


            /// <summary>
            /// Executes the specified action if a debugger is attached.
            /// </summary>
            private static void DoWhenDebuggerAttached( Action action )
            {
                if ( Debugger.IsAttached )
                {
                    action();
                }
            }
        }
    }
}