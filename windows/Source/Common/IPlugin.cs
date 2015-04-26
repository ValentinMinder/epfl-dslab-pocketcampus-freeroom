// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThinMvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// Describes a plugin.
    /// </summary>
    public interface IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        /// <remarks>
        /// This is a non-localized string that must correspond to the server plugin ID.
        /// </remarks>
        string Id { get; }

        /// <summary>
        /// Gets a value indicating whether this plugin is visible in the application's main menu.
        /// </summary>
        bool IsVisible { get; }

        /// <summary>
        /// Gets a value indicating whether the plugin requires that the user be authenticated.
        /// </summary>
        bool RequiresAuthentication { get; }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        void Initialize( INavigationService navigationService );

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        void NavigateTo( INavigationService navigationService );

        /// <summary>
        /// Navigates to the plugin from an external source, with a destination and parameters.
        /// </summary>
        void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService );
    }
}