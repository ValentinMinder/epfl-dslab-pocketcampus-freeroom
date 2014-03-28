// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Net;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;

namespace PocketCampus.Main.Views
{
    /// <summary>
    /// Entry point into the application.
    /// Redirects to the main ViewModel, with the proper parameter if needed.
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
        protected override void OnNavigatedTo( NavigationEventArgs e )
        {
            base.OnNavigatedTo( e );

            if ( e.NavigationMode == NavigationMode.Back )
            {
                App.Current.Terminate();
                return;
            }

            string id;
            if ( NavigationContext.QueryString.TryGetValue( TileCreator.PluginKey, out id ) )
            {
                App.Current.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest( id ) );
                return;
            }

            string redirect;
            if ( NavigationContext.QueryString.TryGetValue( PocketCampusUriMapper.RedirectRequestKey, out redirect ) )
            {
                redirect = HttpUtility.UrlDecode( redirect );
                App.Current.UriMapper.NavigateToCustomUri( redirect );
                return;
            }

            App.Current.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest() );
        }
    }
}