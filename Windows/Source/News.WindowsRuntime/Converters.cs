// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using Windows.Graphics.Display;

namespace PocketCampus.News
{
    /// <summary>
    /// Converts feed item images to URLs.
    /// </summary>
    public sealed class ImageDisplayConverter : ValueConverter<string, string>
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

        public override string Convert( string value )
        {
            if ( value == null )
            {
                return null;
            }

            int scaledWidth = (int) ( ScreenScaleFactor * Width );
            int scaledHeight = (int) ( ScreenScaleFactor * Height );

            return value.Replace( WidthPlaceholder, scaledWidth.ToString() ).Replace( HeightPlaceholder, scaledHeight.ToString() );
        }
    }
}