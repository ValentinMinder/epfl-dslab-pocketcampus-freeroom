// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Runtime.Serialization;
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
    [ThriftStruct( "NewsFeedItem" )]
    public sealed class FeedItem
    {
        /// <summary>
        /// The item's ID.
        /// </summary>
        [ThriftField( 1, true, "itemId" )]
        public int Id { get; set; }

        /// <summary>
        /// The item's title.
        /// </summary>
        [ThriftField( 2, true, "title" )]
        public string Title { get; set; }

        /// <summary>
        /// The item's publication date.
        /// </summary>
        [ThriftField( 3, true, "date" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Date { get; set; }

        /// <summary>
        /// The item's image, if any.
        /// </summary>
        /// <remarks>
        /// Contains {x} and {y} tokens to change its size
        /// </remarks>
        [ThriftField( 4, false, "imageUrl" )]
        public string ImageUrl { get; set; }


        /// <summary>
        /// The log ID for the item.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + " " + Title; }
        }
    }
}