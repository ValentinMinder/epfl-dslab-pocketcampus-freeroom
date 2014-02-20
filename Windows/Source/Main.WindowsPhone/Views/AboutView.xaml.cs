// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Phone.Shell;
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

            // HACK: BindableApplicationBar doesn't use its Tap events, it creates items
            //       so we have to wait until the items are created and set their Click handlers
            //       if we want to do anything but a command binding (as is the case here since this is platform-specific)
            Loaded += ( _, __ ) =>
            {
                var privacyPolicyItem = ( (ApplicationBarMenuItem) ApplicationBar.MenuItems[1] );
                privacyPolicyItem.Click += ( ___, ____ ) => MessageBoxEx.ShowDialog( AppResources.PrivacyPolicyCaption, AppResources.PrivacyPolicyMessage );
            };
        }
    }
}