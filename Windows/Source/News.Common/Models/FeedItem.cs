// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// EPFL news feed item.
    /// </summary>
    /// <remarks>
    /// The content is not a member because it's often very large; downloading everything
    /// is too long and consumes too muchbandwidth.
    /// </remarks>
    [ThriftStruct( "NewsItem" )]
    public sealed class FeedItem
    {
        /// <summary>
        /// The item's ID.
        /// </summary>
        [ThriftField( 1, true, "newsItemId" )]
        public long Id { get; set; }

        /// <summary>
        /// The item's title.
        /// </summary>
        [ThriftField( 2, true, "title" )]
        public string Title { get; set; }

        /// <summary>
        /// The item's URL.
        /// </summary>
        [ThriftField( 3, true, "link" )]
        public string Url { get; set; }

        /// <summary>
        /// The item's publication date.
        /// </summary>
        [ThriftField( 5, true, "pubDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Date { get; set; }

        /// <summary>
        /// The item's image, if any.
        /// </summary>
        [ThriftField( 6, false, "imageUrl" )]
        [ThriftConverter( typeof( StringToOnlineImageConverter ) )]
        public OnlineImage Image { get; set; }
    }
}