// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.News.Models
{
    [ThriftStruct( "NewsFeedItem" )]
    public sealed class FeedItem
    {
        [ThriftField( 1, true, "itemId" )]
        public int Id { get; set; }

        [ThriftField( 2, true, "title" )]
        public string Title { get; set; }

        [ThriftField( 3, true, "date" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Date { get; set; }

        // Contains {x} and {y} tokens to set its size
        [ThriftField( 4, false, "imageUrl" )]
        public string ImageUrl { get; set; }


        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + " " + Title; }
        }
    }
}