// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using System.Diagnostics;
#else
using GoogleAnalytics;
#endif
using ThinMvvm.Logging;
using ThinMvvm;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Logs navigations with Google Analytics.
    /// </summary>
    public sealed class GoogleAnalyticsLogger : Logger
    {
        private const string EventCategory = "UserAction";
        private const string RefreshCommandId = "Refresh";


        public GoogleAnalyticsLogger( INavigationService navigationService )
            : base( navigationService )
        {
        }

        protected override void LogAction( string viewModelId, LoggedSpecialAction action )
        {
#if DEBUG
            Debug.WriteLine( "Action on '" + viewModelId + "': " + action );
#else
            switch ( action )
            {
                case LoggedSpecialAction.ForwardsNavigation:
                case LoggedSpecialAction.BackwardsNavigation:
                    EasyTracker.GetTracker().SendView( viewModelId );
                    break;

                case LoggedSpecialAction.Refresh:
                    LogCommand( viewModelId, RefreshCommandId, null );
                    break;
            }
#endif
        }

        protected override void LogCommand( string viewModelId, string eventId, string label )
        {
#if DEBUG
            Debug.WriteLine( "Event on '" + viewModelId + "': " + eventId + " (label: " + label + ")" );
#else
            EasyTracker.GetTracker().SendEvent( EventCategory, viewModelId + "-" + eventId, label, 0 );
#endif
        }
    }
}