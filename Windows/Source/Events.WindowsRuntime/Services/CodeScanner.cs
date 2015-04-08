// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Events.ExtraViews;
using ThinMvvm;
using Windows.ApplicationModel.Resources;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Events.Services
{
    public sealed class CodeScanner : ICodeScanner
    {
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Events.WindowsRuntime/CodeScanner" );

        // HACK: We need a navigation service to remove the last view after a successful scan,
        //       but it needs to be accessed from the scanning view, so...
        public static INavigationService NavigationService { get; private set; }

        public CodeScanner( INavigationService navigationService )
        {
            NavigationService = navigationService;
        }

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