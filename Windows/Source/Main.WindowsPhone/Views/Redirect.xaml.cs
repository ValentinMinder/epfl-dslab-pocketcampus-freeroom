// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Mvvm;

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
                Application.Current.Terminate();
                return;
            }

            string id;
            NavigationContext.QueryString.TryGetValue( TileCreator.PluginArgumentKey, out id );

            var navSvc = (INavigationService) Container.Get( typeof( INavigationService ), null );
            navSvc.NavigateTo<MainViewModel, string>( id ?? "" );
        }
    }
}