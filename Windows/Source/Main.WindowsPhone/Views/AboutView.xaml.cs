// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Input;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using PocketCampus.Main.Resources;

namespace PocketCampus.Main.Views
{
    public partial class AboutView : BasePage
    {
        public AboutView()
        {
            InitializeComponent();
        }

        private void PrivacyPolicyButton_Tap( object sender, GestureEventArgs e )
        {
            MessageBoxEx.ShowDialog( AppResources.PrivacyPolicyCaption, AppResources.PrivacyPolicyMessage );
        }
    }
}