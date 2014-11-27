// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using PocketCampus.Common.Controls;
using PocketCampus.Satellite.Models;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Satellite.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        // Doesn't break MVVM since it's an implementation detail of the view
        private void Beer_Tap( object sender, EventArgs e )
        {
            var beer = (Beer) ( (FrameworkElement) sender ).DataContext;
            MessageBox.Show( beer.Description, beer.Name, MessageBoxButton.OK );
            Messenger.Send( new EventLogRequest( "ViewBeerDescription", beer.Name ) );
        }
    }
}