// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using Windows.Graphics.Display;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.News
{
    /// <summary>
    /// Converts feed item images to ImageSources.
    /// </summary>
    public sealed class ImageDisplayConverter : ValueConverter<string, ImageSource>
    {
        private static readonly double ScreenScaleFactor = DisplayInformation.GetForCurrentView().RawPixelsPerViewPixel;

        private const string WidthPlaceholder = "{x}";
        private const string HeightPlaceholder = "{y}";


        /// <summary>
        /// The width used for images.
        /// </summary>
        public int Width { get; set; }

        /// <summary>
        /// The height used for images.
        /// </summary>
        public int Height { get; set; }

        public override ImageSource Convert( string value )
        {
            if ( value == null )
            {
                return null;
            }

            int scaledWidth = (int) ( ScreenScaleFactor * Width );
            int scaledHeight = (int) ( ScreenScaleFactor * Height );

            string url = value.Replace( WidthPlaceholder, scaledWidth.ToString() ).Replace( HeightPlaceholder, scaledHeight.ToString() );
            return new BitmapImage( new Uri( url, UriKind.Absolute ) );
        }
    }
}