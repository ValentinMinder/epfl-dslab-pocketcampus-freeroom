// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Controls;
using System.Windows.Input;
using PocketCampus.Common.Controls;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.ViewModels;

namespace PocketCampus.Moodle.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        // see XAML for explanation
        private async void FileButton_Tap( object sender, GestureEventArgs e )
        {
            var param = (CourseFile) ( (Button) sender ).CommandParameter;
            var cmd = ( (MainViewModel) DataContext ).DownloadAndOpenCommand;
            if ( cmd.CanExecute( param ) )
            {
                await cmd.ExecuteAsync( param );
            }
        }
    }
}