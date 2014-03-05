// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Data;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Controls;

namespace PocketCampus.Transport.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        private void StationsPicker_Loaded( object sender, RoutedEventArgs e )
        {
            var binding = new Binding
            {
                Path = new PropertyPath( "SelectedStation" ),
                Mode = BindingMode.TwoWay
            };
            StationsPicker.SetBinding( ListPicker.SelectedItemProperty, binding );
        }
    }
}