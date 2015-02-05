using System;
using PocketCampus.Events.ExtraViews;
using Windows.ApplicationModel.Resources;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Events.Services
{
    public sealed class CodeScanner : ICodeScanner
    {
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Events.WindowsRuntime/CodeScanner" );

        public async void ScanCode()
        {
            if ( await CodeScanView.GetBackCameraAsync() == null )
            {
                await new MessageDialog( _resources.GetString( "NoCaptureDevices" ) ).ShowAsync();
                return;
            }

            ( (Frame) Window.Current.Content ).Navigate( typeof( CodeScanView ) );
        }
    }
}