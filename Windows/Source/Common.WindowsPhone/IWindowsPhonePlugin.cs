// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;

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
        /// Gets the plugin's icon (as an Uri).
        /// </summary>
        Uri Icon { get; }

        /// <summary>
        /// Gets the plugin's small icon (as an Uri).
        /// </summary>
        Uri SmallIcon { get; }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        void Initialize( IWindowsPhoneNavigationService navigationService );
    }
}