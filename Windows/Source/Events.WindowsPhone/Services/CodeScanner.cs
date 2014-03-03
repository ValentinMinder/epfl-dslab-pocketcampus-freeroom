// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using Microsoft.Devices;
using Microsoft.Phone.Controls;
using PocketCampus.Common;
using PocketCampus.Events.Resources;

namespace PocketCampus.Events.Services
{
    public sealed class CodeScanner : ICodeScanner
    {
        public void ScanCode()
        {
            if ( PhotoCamera.IsCameraTypeSupported( CameraType.Primary ) )
            {
                var frame = (PhoneApplicationFrame) Application.Current.RootVisual;
                var target = new Uri( "/PocketCampus.Events.WindowsPhone;component/Views/CodeScannerView.xaml", UriKind.Relative );
                frame.Navigate( target );
            }
            else
            {
                MessageBoxEx.ShowDialog( PluginResources.NoCameraCaption, PluginResources.NoCameraMessage );
            }
        }
    }
}