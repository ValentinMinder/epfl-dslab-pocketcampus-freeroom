// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Input;
using PocketCampus.Common.Controls;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        private void RestaurantItem_Tap( object sender, GestureEventArgs e )
        {
            var item = (FrameworkElement) sender;
            var group = (RestaurantAsGroup) item.DataContext;
            Messenger.Send( new EventLogRequest( "ShowRestaurant", group.Restaurant.Name ) );
        }
    }
}