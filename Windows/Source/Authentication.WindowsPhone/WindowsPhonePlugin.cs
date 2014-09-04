using PocketCampus.Authentication.ViewModels;
using PocketCampus.Common;

namespace PocketCampus.Authentication
{
    public sealed class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
    {
        /// <summary>
        /// This plugin does not have a name.
        /// </summary>
        public string Name
        {
            get { return null; }
        }

        /// <summary>
        /// This plugin does not have an icon.
        /// </summary>
        public string IconKey
        {
            get { return null; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( ThinMvvm.WindowsPhone.IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<AuthenticationViewModel>( "/PocketCampus.Authentication.WindowsPhone;component/Views/AuthenticationView.xaml" );
        }
    }
}