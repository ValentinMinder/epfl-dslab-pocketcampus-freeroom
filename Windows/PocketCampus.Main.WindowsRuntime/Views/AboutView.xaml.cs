using System;
using PocketCampus.Common;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Main.Views
{
    public sealed partial class AboutView : Page
    {
        public AboutView()
        {
            this.InitializeComponent();
        }

        private async void AppBarButton_Click( object sender, RoutedEventArgs e )
        {
            var loader = LocalizationHelper.GetLoaderForCurrentAssembly( "About" );
            string content = loader.GetString( "PrivacyPolicyContent" );
            string title = loader.GetString( "PrivacyPolicyTitle" );
            await new MessageDialog( content, title ).ShowAsync();
        }
    }
}