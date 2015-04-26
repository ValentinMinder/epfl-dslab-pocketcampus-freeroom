// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.IsAcademia.ViewModels;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;

namespace PocketCampus.IsAcademia.Views
{
    public sealed partial class MainView
    {
        public MainView()
        {
            InitializeComponent();
        }

        private void OnRoomHyperlinkTapped( object sender, TappedRoutedEventArgs e )
        {
            string room = (string) ( (Control) sender ).DataContext;
            var command = ( (MainViewModel) Root.DataContext ).ViewRoomOnMapCommand;
            if ( command.CanExecute( room ) )
            {
                command.Execute( room );
            }
        }
    }
}