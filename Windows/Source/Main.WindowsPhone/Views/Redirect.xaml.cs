// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
using System.Windows;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Main.Views
{
    /// <summary>
    /// Entry point into the application.
    /// Handles redirections to plugins (for plugin Live Tiles).
    /// </summary>
    public partial class Redirect : PhoneApplicationPage
    {
        public Redirect()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Occurs when the user opens the application, or goes back from the main ViewModel.
        /// </summary>
        protected override async void OnNavigatedTo( NavigationEventArgs e )
        {
            base.OnNavigatedTo( e );

            if ( e.NavigationMode == NavigationMode.Back )
            {
                Application.Current.Terminate();
                return;
            }

            // TODO: This is ugly. Really ugly.

            var navSvc = (INavigationService) Container.Get( typeof( INavigationService ), null );

            string id;
            if ( NavigationContext.QueryString.TryGetValue( TileCreator.PluginArgumentKey, out id ) )
            {
                var access = (IServerAccess) Container.Get( typeof( IServerAccess ), null );
                var settings = (IMainSettings) Container.Get( typeof( IMainSettings ), null );

                ServerConfiguration config;
                try
                {
                    config = await access.LoadConfigurationAsync();
                    settings.ServerConfiguration = config;
                }
                catch
                {
                    // something went wrong, use the cached config
                }

                access.CurrentConfiguration = settings.ServerConfiguration;

                var loader = (IPluginLoader) Container.Get( typeof( IPluginLoader ), null );
                var plugin = loader.GetPlugins().First( p => p.Id == id );


                if ( plugin.RequiresAuthentication && !settings.IsAuthenticated )
                {
                    navSvc.NavigateToDialog<AuthenticationViewModel>();
                }

                plugin.NavigateTo( navSvc );
            }
            else
            {
                navSvc.NavigateTo<MainViewModel>();
            }
        }
    }
}