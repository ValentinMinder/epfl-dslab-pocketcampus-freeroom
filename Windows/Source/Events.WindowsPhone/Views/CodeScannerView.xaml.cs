// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

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

// Freely inspired from http://geekswithblogs.net/tmurphy/archive/2012/07/23/reading-qr-codes-in-your-windows-phone-app.aspx

namespace PocketCampus.Events.Views
{
    public partial class CodeScannerView : BasePage
    {
        private static readonly TimeSpan ScanInterval = TimeSpan.FromMilliseconds( 200 );
        private const string CustomUrlPrefix = "pocketcampus://";

        private readonly DispatcherTimer _timer;
        private PhotoCamera _camera;


        /// <summary>
        /// Creates a new CodeScannerView.
        /// </summary>
        public CodeScannerView()
        {
            InitializeComponent();

            _timer = new DispatcherTimer { Interval = ScanInterval };
            _timer.Tick += ( _, __ ) => ScanPreviewBuffer();
        }


        /// <summary>
        /// Called when the user navigates to the page.
        /// </summary>
        protected override void OnNavigatedTo( NavigationEventArgs e )
        {
            base.OnNavigatedTo( e );

            // this must be done here
            _camera = new PhotoCamera( CameraType.Primary );
            _camera.Initialized += Camera_Initialized;
            PreviewVideo.SetSource( _camera );
        }

        /// <summary>
        /// Called when the user navigates away from the page.
        /// </summary>
        protected override void OnNavigatedFrom( NavigationEventArgs e )
        {
            base.OnNavigatedFrom( e );

            _timer.Stop();
            _camera.Initialized -= Camera_Initialized;
            _camera.Dispose();
        }

        /// <summary>
        /// Called when the camera is initialized.
        /// </summary>
        private void Camera_Initialized( object sender, CameraOperationCompletedEventArgs e )
        {
            Dispatcher.BeginInvoke( () =>
            {
                PreviewTransform.Rotation = _camera.Orientation;
                _timer.Start();
            } );
        }

        /// <summary>
        /// Scans the camera's preview buffer for a QR code.
        /// </summary>
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

        /// <summary>
        /// Processes a positive scan result.
        /// </summary>
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