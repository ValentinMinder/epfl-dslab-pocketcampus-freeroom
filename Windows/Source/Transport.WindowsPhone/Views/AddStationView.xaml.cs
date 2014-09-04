// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Controls;
using PocketCampus.Common.Controls;

namespace PocketCampus.Transport.Views
{
    public partial class AddStationView : BasePage
    {
        public AddStationView()
        {
            InitializeComponent();
        }

        // WP8 doesn't have UpdateSourceTrigger=PropertyChanged -_-''
        private void TextBox_TextChanged( object sender, System.Windows.Controls.TextChangedEventArgs e )
        {
            SearchBox.GetBindingExpression( TextBox.TextProperty ).UpdateSource();
        }
    }
}