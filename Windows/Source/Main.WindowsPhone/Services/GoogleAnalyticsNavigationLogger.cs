// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using GoogleAnalytics;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Logs navigations with Google Analytics.
    /// </summary>
    public sealed class GoogleAnalyticsNavigationLogger : NavigationLogger
    {
        private readonly Tracker _tracker;

        /// <summary>
        /// Creates a new GoogleAnalyticsNavigationLogger.
        /// </summary>
        public GoogleAnalyticsNavigationLogger()
        {
            _tracker = EasyTracker.GetTracker();
        }

        /// <summary>
        /// Logs a navigation with the specified ID.
        /// </summary>
        protected override void LogNavigation( string id )
        {
            _tracker.SendView( id );
        }

        /// <summary>
        /// Logs a command execution on the specified ViewModel with the specified ID.
        /// </summary>
        protected override void LogEvent( string viewModelId, string eventId )
        {
            _tracker.SendEvent( viewModelId, viewModelId + "-" + eventId, "", 0 );
        }
    }
}