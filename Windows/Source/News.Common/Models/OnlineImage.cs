// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Text.RegularExpressions;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Variable size image hosted on an online server.
    /// </summary>
    public sealed class OnlineImage
    {
        // Tokens to identify the 'size' part of the URL and make it editable.
        private static readonly Regex SizeRegex = new Regex( @"\d+x\d+" );
        private const string SizeFormat = "{0}x{1}";

        private readonly string _urlFormat;


        /// <summary>
        /// Creates a new image from an URL in a feed item.
        /// </summary>
        public OnlineImage( string url )
        {
            _urlFormat = SizeRegex.Replace( url, SizeFormat );
        }


        /// <summary>
        /// Gets the URL of the image for the specified width and height.
        /// </summary>
        public string GetUrl( int width, int height )
        {
            return string.Format( _urlFormat, width, height );
        }
    }
}