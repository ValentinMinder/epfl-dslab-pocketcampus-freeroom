// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using PocketCampus.Common;

namespace PocketCampus.News
{
    /// <summary>
    /// Converts feed item images to ImageSources.
    /// </summary>
    public sealed class ImageDisplayConverter : ValueConverter<string, ImageSource>
    {
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

            string url = value.Replace( WidthPlaceholder, Width.ToString() ).Replace( HeightPlaceholder, Height.ToString() );
            return new BitmapImage( new Uri( url, UriKind.Absolute ) );
        }
    }
}