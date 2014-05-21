// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Devices;
using PocketCampus.Common;
using PocketCampus.Events.Controls;
using PocketCampus.Events.Resources;

namespace PocketCampus.Events.Services
{
    /// <summary>
    /// Lets the user scan QR codes for event pool and items.
    /// </summary>
    public sealed class CodeScanner : ICodeScanner
    {
        /// <summary>
        /// Requests a QR code scan to the user.
        /// </summary>
        public void ScanCode()
        {
            if ( PhotoCamera.IsCameraTypeSupported( CameraType.Primary ) )
            {
                CodeScannerPopup.Show();
            }
            else
            {
                MessageBoxEx.ShowDialog( PluginResources.NoCameraCaption, PluginResources.NoCameraMessage );
            }
        }
    }
}