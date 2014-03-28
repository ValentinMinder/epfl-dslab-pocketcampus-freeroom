// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.IsAcademia.Resources;
using PocketCampus.IsAcademia.ViewModels;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.IsAcademia
{
    /// <summary>
    /// The IS-Academia Windows Phone plugin.
    /// </summary>
    public class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
    {
        /// <summary>
        /// Gets the plugin's name.
        /// </summary>
        public string Name
        {
            get { return PluginResources.PluginName; }
        }

        /// <summary>
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        public string IconKey
        {
            get { return "IsAcademiaIcon"; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.IsAcademia.WindowsPhone;component/Views/MainView.xaml" );
        }
    }
}