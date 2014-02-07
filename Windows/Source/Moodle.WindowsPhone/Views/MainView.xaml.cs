// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Controls;
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
        // DO NOT REMOVE THE NAMESPACE IN THE SECOND PARAMETER, it's System.Windows.Input, not Microsoft.Phone.Controls
        private async void FileButton_Tap( object sender, System.Windows.Input.GestureEventArgs e )
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