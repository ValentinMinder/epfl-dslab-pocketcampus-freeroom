// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.Devices.Enumeration;
using Windows.Media.Capture;
using Windows.Media.MediaProperties;
using Windows.Phone.UI.Input;
using Windows.Storage.Streams;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Media.Imaging;
using ZXing;

// HACK: Instead of using the preview stream (which can't be written to a RenderTargetBitmap),
//       this class takes a picture every 200 ms and scans it.

namespace PocketCampus.Events.ExtraViews
{
    public sealed partial class CodeScanView
    {
        private static readonly TimeSpan InitialScanDelay = TimeSpan.FromSeconds( 2 );
        private static readonly TimeSpan ScanInterval = TimeSpan.FromMilliseconds( 200 );
        private const string CustomUrlPrefix = "pocketcampus://";
        private const string CustomUrlLogPrefix = "pocketcampus://events.plugin.pocketcampus.org/";

        private MediaCapture _camera;
        private bool _enableScan;


        public CodeScanView()
        {
            InitializeComponent();
            Loaded += This_Loaded;
            HardwareButtons.BackPressed += HardwareButtons_BackPressed;
        }

        public static async Task<DeviceInformation> GetBackCameraAsync()
        {
            var allDevices = await DeviceInformation.FindAllAsync( DeviceClass.VideoCapture );
            return allDevices.FirstOrDefault( d => d.EnclosureLocation.Panel == Windows.Devices.Enumeration.Panel.Back );
        }

        private async void This_Loaded( object sender, RoutedEventArgs e )
        {
            try
            {
                _camera = new MediaCapture();
                await _camera.InitializeAsync( new MediaCaptureInitializationSettings
                {
                    VideoDeviceId = ( await GetBackCameraAsync() ).Id,
                    StreamingCaptureMode = StreamingCaptureMode.Video,
                    PhotoCaptureSource = PhotoCaptureSource.VideoPreview
                } );
                _camera.VideoDeviceController.FlashControl.Enabled = false;
                _camera.SetPreviewRotation( VideoRotation.Clockwise90Degrees );
                _camera.SetRecordRotation( VideoRotation.Clockwise90Degrees );

                CameraPreview.Source = _camera;
                await _camera.StartPreviewAsync();

                StartScanning();
            }
            catch
            {
                ErrorMessage.Visibility = Visibility.Visible;
            }
        }

        private void HardwareButtons_BackPressed( object sender, BackPressedEventArgs e )
        {
            e.Handled = true;
            Close();
        }

        private async void StartScanning()
        {
            _enableScan = true;
            await Task.Delay( InitialScanDelay );
            while ( _enableScan )
            {
                await Task.Delay( ScanInterval );
                await ScanPreviewBuffer();
            }
        }

        private void Close()
        {
            _enableScan = false;
            _camera.Dispose();
            HardwareButtons.BackPressed -= HardwareButtons_BackPressed;

            Frame.GoBack();
        }

        private async Task ScanPreviewBuffer()
        {
            try
            {
                using ( var stream = new InMemoryRandomAccessStream() )
                {
                    // Fairly small values to avoid scanning very large images
                    var props = new ImageEncodingProperties
                    {
                        Subtype = "BMP",
                        Width = 600,
                        Height = 800
                    };
                    await _camera.CapturePhotoToStreamAsync( props, stream );

                    var bitmap = new WriteableBitmap( (int) props.Width, (int) props.Height );
                    stream.Seek( 0 );
                    await bitmap.SetSourceAsync( stream );

                    var reader = new BarcodeReader();
                    var result = reader.Decode( bitmap );

                    if ( result != null )
                    {
                        ProcessResultText( result.Text );
                    }
                }
            }
            catch
            {
                // The camera is prone to odd things. Don't crash.
            }
        }

        private void ProcessResultText( string url )
        {
            if ( url != null && url.StartsWith( CustomUrlPrefix ) )
            {
                Messenger.Send( new EventLogRequest( "QRCodeScanned", url.Replace( CustomUrlLogPrefix, "" ) ) );
                CodeScanner.NavigationService.RemoveCurrentFromBackStack();
                Close();
                LauncherEx.Launch( new Uri( url, UriKind.Absolute ) );
            }
            else
            {
                WrongCodeMessage.Visibility = Visibility.Visible;
            }
        }
    }
}
