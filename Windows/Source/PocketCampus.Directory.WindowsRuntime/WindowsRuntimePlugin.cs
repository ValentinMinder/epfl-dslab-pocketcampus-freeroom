using System;
using PocketCampus.Common;
using PocketCampus.Directory.Services;
using PocketCampus.Directory.ViewModels;
using PocketCampus.Directory.Views;
using ThinMvvm;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Directory
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Directory.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public object Icon
        {
            get
            {
                return new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Directory.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["DirectoryIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            Container.Bind<IContactsService, ContactsService>();

            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<PersonViewModel, PersonView>();
        }
    }
}