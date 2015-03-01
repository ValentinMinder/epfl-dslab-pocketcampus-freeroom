// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using System.Diagnostics;
#else
using GoogleAnalytics;
#endif
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.Services
{
    public sealed class GoogleAnalyticsLogger : Logger
    {
        private const string EventCategory = "UserAction";
        private const string RefreshCommandId = "Refresh";


        public GoogleAnalyticsLogger( INavigationService navigationService ) : base( navigationService ) { }

        protected override void LogAction( string viewModelId, LoggedSpecialAction action )
        {
#if DEBUG
            Debug.WriteLine( "Action on '{0}': {1}", viewModelId, action );
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
            Debug.WriteLine( "Command on '{0}': '{1}' with label '{2}'", viewModelId, eventId, label );
#else
            EasyTracker.GetTracker().SendEvent( EventCategory, viewModelId + "-" + eventId, label, 0 );
#endif
        }
    }
}