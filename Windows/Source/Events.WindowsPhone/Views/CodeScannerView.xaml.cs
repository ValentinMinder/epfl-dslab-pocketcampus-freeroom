using System;
using System.Windows;
using System.Windows.Navigation;
using System.Windows.Threading;
using Microsoft.Devices;
using PocketCampus.Common.Controls;
using Windows.System;
using ZXing;
using ZXing.Common;
using ZXing.QrCode;

namespace PocketCampus.Events.Views
{
    public partial class CodeScannerView : BasePage
    {
        private static readonly TimeSpan ScanInterval = TimeSpan.FromMilliseconds( 200 );
        private const string CustomUrlPrefix = "pocketcampus://";

        private readonly DispatcherTimer _timer;
        private PhotoCamera _camera;

        public CodeScannerView()
        {
            InitializeComponent();

            _timer = new DispatcherTimer { Interval = ScanInterval };
            _timer.Tick += ( _, __ ) => ScanPreviewBuffer();
        }

        protected override void OnNavigatedTo( NavigationEventArgs e )
        {
            base.OnNavigatedTo( e );

            _camera = new PhotoCamera( CameraType.Primary );
            _camera.Initialized += Camera_Initialized;
            PreviewVideo.SetSource( _camera );
        }

        protected override void OnNavigatedFrom( NavigationEventArgs e )
        {
            base.OnNavigatedFrom( e );

            _timer.Stop();
            _camera.Initialized -= Camera_Initialized;
            _camera.Dispose();
        }

        private void Camera_Initialized( object sender, CameraOperationCompletedEventArgs e )
        {
            Dispatcher.BeginInvoke( () =>
            {
                PreviewTransform.Rotation = _camera.Orientation;
                _timer.Start();
            } );
        }

        private void ScanPreviewBuffer()
        {
            int width = (int) _camera.PreviewResolution.Width;
            int height = (int) _camera.PreviewResolution.Height;
            byte[] pixelData = new byte[width * height];

            _camera.GetPreviewBufferY( pixelData );

            var luminance = new RGBLuminanceSource( pixelData, width, height, RGBLuminanceSource.BitmapFormat.Gray8 );
            var binarizer = new HybridBinarizer( luminance );
            var bitmap = new BinaryBitmap( binarizer );
            var result = new QRCodeReader().decode( bitmap );

            if ( result != null )
            {
                Dispatcher.BeginInvoke( () => ProcessResult( result ) );
            }
        }

        private async void ProcessResult( Result result )
        {
            string url = result.Text;
            if ( url.StartsWith( CustomUrlPrefix ) )
            {
                await Launcher.LaunchUriAsync( new Uri( url, UriKind.Absolute ) );
            }
            else
            {
                WrongCodeMessage.Visibility = Visibility.Visible;
            }
        }
    }
}