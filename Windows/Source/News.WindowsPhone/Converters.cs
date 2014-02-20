// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using PocketCampus.Common;
using PocketCampus.News.Models;

namespace PocketCampus.News
{
    /// <summary>
    /// Converts feed item images to ImageSources.
    /// </summary>
    public sealed class OnlineImageDisplayConverter : ValueConverter<OnlineImage, ImageSource>
    {
        /// <summary>
        /// The width used for images.
        /// </summary>
        public int Width { get; set; }

        /// <summary>
        /// The height used for images.
        /// </summary>
        public int Height { get; set; }

        public override ImageSource Convert( OnlineImage value )
        {
            if ( value == null )
            {
                return null;
            }
            return new BitmapImage( new Uri( value.GetUrl( Width, Height ), UriKind.Absolute ) );
        }
    }
}