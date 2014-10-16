using System;
using System.IO;
using System.Linq;
using PocketCampus.Common;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.Devices.Enumeration;
using Windows.Media.Capture;
using Windows.Media.MediaProperties;
using Windows.Phone.UI.Input;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Media.Imaging;
using ZXing;

namespace PocketCampus.Events.Controls
{
    public sealed partial class CodeScannerControl
    {
        private static readonly TimeSpan ScanInterval = TimeSpan.FromMilliseconds( 200 );
        private const string CustomUrlPrefix = "pocketcampus://";
        private const string CustomUrlLogPrefix = "pocketcampus://events.plugin.pocketcampus.org/";

        private readonly DispatcherTimer _timer;
        private MediaCapture _camera;


        public CodeScannerControl()
        {
            InitializeComponent();

            _timer = new DispatcherTimer { Interval = ScanInterval };
            _timer.Tick += ( _, __ ) => ScanPreviewBuffer();

            Loaded += This_Loaded;
            HardwareButtons.BackPressed += HardwareButtons_BackPressed;
        }

        internal event EventHandler<EventArgs> Closed;
        private void OnClosed()
        {
            var evt = Closed;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }

        private async void This_Loaded( object sender, RoutedEventArgs e )
        {
            try
            {
                var captureDevices = await DeviceInformation.FindAllAsync( DeviceClass.VideoCapture );
                _camera = new MediaCapture();
                await _camera.InitializeAsync( new MediaCaptureInitializationSettings
                {
                    VideoDeviceId = captureDevices.First( c => c.IsEnabled ).Id,
                    PhotoCaptureSource = PhotoCaptureSource.VideoPreview
                } );
                CameraPreview.Source = _camera;
                await _camera.StartPreviewAsync();
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

        private void Close()
        {
            _timer.Stop();
            _camera.Dispose();

            HardwareButtons.BackPressed -= HardwareButtons_BackPressed;

            OnClosed();
        }

        private async void ScanPreviewBuffer()
        {
            // from http://stackoverflow.com/a/24776375
            using ( var stream = new MemoryStream() )
            {
                await _camera.CapturePhotoToStreamAsync( ImageEncodingProperties.CreateBmp(), stream.AsRandomAccessStream() );
                var image = new BitmapImage();
                await image.SetSourceAsync( stream.AsRandomAccessStream() );
                var reader = new BarcodeReader();
                var wrb = new WriteableBitmap( image.PixelWidth, image.PixelHeight );
                wrb.SetSource( stream.AsRandomAccessStream() );
                var result = reader.Decode( wrb );
                ProcessResult( result );
            }
        }

        private void ProcessResult( Result result )
        {
            string url = result.Text;
            if ( url.StartsWith( CustomUrlPrefix ) )
            {
                Messenger.Send( new EventLogRequest( "QRCodeScanned", url.Replace( CustomUrlLogPrefix, "" ) ) );
                LauncherEx.Launch( new Uri( url, UriKind.Absolute ) );
                Close();
            }
            else
            {
                WrongCodeMessage.Visibility = Visibility.Visible;
            }
        }
    }
}