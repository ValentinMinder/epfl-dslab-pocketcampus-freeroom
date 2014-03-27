// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm.WindowsPhone;

namespace PocketCampus.Common
{
    /// <summary>
    /// Windows Phone specific extensions to IPlugin.
    /// </summary>
    public interface IWindowsPhonePlugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's localized name.
        /// </summary>
        string Name { get; }

        /// <summary>
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        string IconKey { get; }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        void Initialize( IWindowsPhoneNavigationService navigationService );
    }
}