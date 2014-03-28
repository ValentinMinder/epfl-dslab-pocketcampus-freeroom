// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Threading;
using Microsoft.Devices;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using PocketCampus.Common;
using ThinMvvm;
using ThinMvvm.Logging;
using ZXing;
using ZXing.Common;
using ZXing.QrCode;

// Freely inspired from http://geekswithblogs.net/tmurphy/archive/2012/07/23/reading-qr-codes-in-your-windows-phone-app.aspx

namespace PocketCampus.Events.Controls
{
    public partial class CodeScannerPopup : ContentControl
    {
        private static readonly TimeSpan ScanInterval = TimeSpan.FromMilliseconds( 200 );
        private const string CustomUrlPrefix = "pocketcampus://";
        private const string CustomUrlLogPrefix = "pocketcampus://events.plugin.pocketcampus.org/";

        private PhotoCamera _camera;
        private DispatcherTimer _timer;
        private PhoneApplicationFrame _frame;
        private PhoneApplicationPage _underPage;
        private bool _underPageTrayVisible;
        private bool _underPageAppBarVisible;
        private bool _isDone;


        /// <summary>
        /// Creates a new CodeScannerPopup.
        /// </summary>
        public CodeScannerPopup()
        {
            InitializeComponent();

            _timer = new DispatcherTimer { Interval = ScanInterval };
            _timer.Tick += ( _, __ ) => ScanPreviewBuffer();

            Loaded += This_Loaded;
        }

        public static void Show()
        {
            var scanner = new CodeScannerPopup();
            var popup = new Popup { Child = scanner };
            popup.IsOpen = true;

            scanner.CloseRequested += ( _, __ ) => popup.IsOpen = false;
        }

        private event EventHandler<EventArgs> CloseRequested;

        /// <summary>
        /// Called when the control is loaded.
        /// </summary>
        private void This_Loaded( object sender, EventArgs e )
        {
            try
            {
                // camera init must be done here
                _camera = new PhotoCamera( CameraType.Primary );
                _camera.Initialized += Camera_Initialized;
                PreviewVideo.SetSource( _camera );

                _frame = (PhoneApplicationFrame) Application.Current.RootVisual;
                _frame.BackKeyPress += Frame_BackKeyPress;

                _underPage = (PhoneApplicationPage) _frame.Content;

                _underPageTrayVisible = SystemTray.GetIsVisible( _underPage );
                _underPageAppBarVisible = _underPage.ApplicationBar.IsVisible;

                SystemTray.SetIsVisible( _underPage, false );
                _underPage.ApplicationBar.IsVisible = false;
            }
            catch
            {
                ErrorMessage.Visibility = Visibility.Visible;
            }
        }

        /// <summary>
        /// Handles back key presses.
        /// </summary>
        private void Frame_BackKeyPress( object sender, CancelEventArgs e )
        {
            e.Cancel = true;
            Close();
        }

        /// <summary>
        /// Closes the control and reverts any changes made to the underlying page.
        /// </summary>
        private void Close()
        {
            _timer.Stop();
            _camera.Initialized -= Camera_Initialized;
            _camera.Dispose();
            _isDone = true;

            _frame.BackKeyPress -= Frame_BackKeyPress;

            SystemTray.SetIsVisible( _underPage, _underPageTrayVisible );
            _underPage.ApplicationBar.IsVisible = _underPageAppBarVisible;

            // No null check, we know it's always non-null
            CloseRequested( this, EventArgs.Empty );
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
        private void ProcessResult( Result result )
        {
            if ( _isDone )
            {
                // this is sometimes called once too many??
                return;
            }

            string url = result.Text;
            if ( url.StartsWith( CustomUrlPrefix ) )
            {
                Messenger.Send( new EventLogRequest( "QRCodeScanned", url.Replace( CustomUrlLogPrefix, "" ) ) );
                LauncherEx.Launch( url );
                Close();
            }
            else
            {
                WrongCodeMessage.Visibility = Visibility.Visible;
            }
        }
    }
}