// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml.Resources;

namespace PocketCampus.Main
{
    // Brilliant and awful hack to improve localization support; no more X.Content or Y.Text and x:Uid not working
    public sealed class LocalizingResourceLoader : CustomXamlResourceLoader
    {
        protected override object GetResource( string resourceId, string objectType, string propertyName, string propertyType )
        {
            // TODO: Find a way to get designer support.

            var parts = resourceId.Split( '.' );

            if ( parts.Length == 1 )
            {
                return GetResourceFromImplicitPath( parts );
            }
            if ( parts.Length == 2 )
            {
                return GetResourceFromRelativePath( parts );
            }
            if ( parts.Length == 3 )
            {
                return GetResourceFromFullPath( parts );
            }

            throw new InvalidOperationException( "Wrong path parts length." );
        }

        private static string GetResourceFromImplicitPath( string[] parts )
        {
            return LocalizationHelper.GetLoaderForCurrentAssembly( "Resources" ).GetString( parts[0] );
        }

        private static string GetResourceFromRelativePath( string[] parts )
        {
            return LocalizationHelper.GetLoaderForCurrentAssembly( parts[0] ).GetString( parts[1] );
        }

        private static string GetResourceFromFullPath( string[] parts )
        {
            string assemblyName = "PocketCampus." + parts[0] + ".WindowsRuntime";
            return ResourceLoader.GetForViewIndependentUse( assemblyName + "/" + parts[1] ).GetString( parts[2] );
        }
    }
}