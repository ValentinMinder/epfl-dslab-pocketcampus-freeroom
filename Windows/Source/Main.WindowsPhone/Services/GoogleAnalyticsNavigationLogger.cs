// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

#if !DEBUG
using GoogleAnalytics;
#endif
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Logs navigations with Google Analytics.
    /// </summary>
    public sealed class GoogleAnalyticsNavigationLogger : NavigationLogger
    {
        private const string EventCategory = "UserAction";

        /// <summary>
        /// Logs a navigation with the specified ID.
        /// </summary>
        protected override void LogNavigation( string id )
        {
#if !DEBUG
            EasyTracker.GetTracker().SendView( id );
#endif
        }

        /// <summary>
        /// Logs a command execution on the specified ViewModel with the specified ID.
        /// </summary>
        protected override void LogEvent( string viewModelId, string eventId, string label )
        {
#if !DEBUG
            EasyTracker.GetTracker().SendEvent( EventCategory, viewModelId + "-" + eventId, label, 0 );
#endif
        }
    }
}