// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using Windows.System;

namespace PocketCampus.Common
{
    /// <summary>
    /// Advanced version of Launcher that supports in-app protocol registration.
    /// </summary>
    public static class LauncherEx
    {
        private const string ProtocolHostSeparator = "://";

        private static readonly Dictionary<string, Action<Uri>> _protocolHandlers = new Dictionary<string, Action<Uri>>();

        /// <summary>
        /// Registers the specified protocol handler..
        /// </summary>
        public static void RegisterProtocol( string protocolName, Action<Uri> action )
        {
            _protocolHandlers.Add( protocolName, action );
        }

        /// <summary>
        /// Launches an URI, delegating to the app or the system as needed.
        /// </summary>
        public static async void Launch( Uri uri )
        {
            if ( _protocolHandlers.ContainsKey( uri.Scheme ) )
            {
                _protocolHandlers[uri.Scheme]( uri );
            }
            else
            {
                await Launcher.LaunchUriAsync( uri );
            }
        }
    }
}