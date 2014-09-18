using System;
using System.Threading.Tasks;
using PocketCampus.Events.Controls;
using Windows.ApplicationModel.Resources;
using Windows.Devices.Enumeration;
using Windows.UI.Popups;
using Windows.UI.Xaml.Controls.Primitives;

namespace PocketCampus.Events.Services
{
    public sealed class CodeScanner : ICodeScanner
    {
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Events.WindowsRuntime/CodeScanner" );

        public async void ScanCode()
        {
            var videoDevices = await DeviceInformation.FindAllAsync( DeviceClass.VideoCapture );
            if ( videoDevices.Count == 0 )
            {
                await new MessageDialog( _resources.GetString( "NoCaptureDevices" ) ).ShowAsync();
                return;
            }

            var taskSource = new TaskCompletionSource<bool>();

            var scanner = new CodeScannerControl();
            scanner.Closed += ( _, __ ) => taskSource.SetResult( true );

            var popup = new Popup
            {
                Child = scanner,
                IsOpen = true
            };

            await taskSource.Task;
            popup.IsOpen = false;
        }
    }
}