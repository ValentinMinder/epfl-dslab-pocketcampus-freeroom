// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Satellite.Models;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Input;

namespace PocketCampus.Satellite.Views
{
    public sealed partial class MainView
    {
        public MainView()
        {
            InitializeComponent();
        }

        private async void Beer_Tapped( object sender, TappedRoutedEventArgs e )
        {
            var beer = (Beer) ( (FrameworkElement) sender ).DataContext;
            await new MessageDialog( beer.Description, beer.Name ).ShowAsync();
        }
    }
}