// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Reflection;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;

namespace PocketCampus.Common
{
    public static class LocalizationHelper
    {
        private static string _currentAssemblyNamePrefix;


        public static void Initialize()
        {
            ( (Frame) Window.Current.Content ).Navigating += Frame_Navigating;
        }


        public static ResourceLoader GetLoaderForCurrentAssembly( string part )
        {
            return ResourceLoader.GetForViewIndependentUse( _currentAssemblyNamePrefix + part );
        }


        private static void Frame_Navigating( object sender, NavigatingCancelEventArgs e )
        {
            _currentAssemblyNamePrefix = e.SourcePageType.GetTypeInfo().Assembly.GetName().Name + "/";

            // I'm not sure why, but this is required; GetForViewIndepenentUse("PocketCampus.Main.WindowsRuntime/...") throws.
            if ( _currentAssemblyNamePrefix == "PocketCampus.Main.WindowsRuntime/" )
            {
                _currentAssemblyNamePrefix = "";
            }
        }
    }
}