// Copyright (c) PocketCampus.Org 2014
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

        private static Dictionary<string, Action<string>> _protocolHandlers = new Dictionary<string, Action<string>>();

        /// <summary>
        /// Registers the specified protocol handler..
        /// </summary>
        public static void RegisterProtocol( string protocolName, Action<string> action )
        {
            _protocolHandlers.Add( protocolName, action );
        }

        /// <summary>
        /// Launches an URI, delegating to the app or the system as needed.
        /// </summary>
        public static async void Launch( string uri )
        {
            string protocol = uri.Split( new[] { ProtocolHostSeparator }, StringSplitOptions.None )[0];

            if ( _protocolHandlers.ContainsKey( protocol ) )
            {
                _protocolHandlers[protocol]( uri );
            }
            else
            {
                await Launcher.LaunchUriAsync( new Uri( uri, UriKind.Absolute ) );
            }
        }
    }
}