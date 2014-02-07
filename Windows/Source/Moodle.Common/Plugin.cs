// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Moodle.Services;
using PocketCampus.Moodle.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// The Moodle plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "Moodle"; }
        }

        /// <summary>
        /// This plugin requires authentication.
        /// </summary>
        public bool RequiresAuthentication
        {
            get { return true; }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IMoodleService, MoodleService>();
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel>();
        }
    }
}