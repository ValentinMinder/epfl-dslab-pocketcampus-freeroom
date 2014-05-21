// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using Microsoft.Phone.Info;

namespace PocketCampus.Common
{
    /// <summary>
    /// Helper class for Windows Phone functionality
    /// </summary>
    public static class PhoneHelper
    {
        private static double _screenScaleFactor = 0.0;

        /// <summary>
        /// Gets the screen scaling factor.
        /// </summary>
        public static double GetScreenScaleFactor()
        {
            if ( _screenScaleFactor == 0.0 )
            {
                object temp;
                if ( DeviceExtendedProperties.TryGetValue( "PhysicalScreenResolution", out temp ) )
                {
                    // GDR3
                    var screenSize = (Size) temp;
                    _screenScaleFactor = screenSize.Width / Application.Current.Host.Content.ActualWidth;
                }
                else
                {
                    // pre-GDR3
                    _screenScaleFactor = (double) Application.Current.Host.Content.ScaleFactor / 100.0;
                }
            }

            return _screenScaleFactor;
        }
    }
}