// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// An item in the Events plugin.
    /// It can be anything: an event, a person, a laboratory, a poster, ...
    /// </summary>
    [ThriftStruct( "EventItem" )]
    public sealed class EventItem
    {
        /// <summary>
        /// The ID of the "Favorites" category.
        /// </summary>
        public static readonly int? FavoriteCategoryId = -2;


        /// <summary>
        /// The item's ID.
        /// </summary>
        [ThriftField( 1, true, "eventId" )]
        public long Id { get; set; }

        /// <summary>
        /// The item's start date, if any.
        /// </summary>
        [ThriftField( 2, false, "startDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? StartDate { get; set; }

        /// <summary>
        /// The item's end date, if any.
        /// </summary>
        /// <remarks>
        /// May be the start date at midnight, which means it wasn't set.
        /// </remarks>
        [ThriftField( 3, false, "endDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? EndDate { get; set; }

        /// <summary>
        /// A value indicating whether the event takes the entire day.
        /// </summary>
        [ThriftField( 4, false, "fullDay" )]
        public bool? IsFullDay { get; set; }

        /// <summary>
        /// URL to the item's picture, if any.
        /// </summary>
        [ThriftField( 5, false, "eventPicture" )]
        public string PictureUrl { get; set; }

        /// <summary>
        /// The item name.
        /// </summary>
        [ThriftField( 6, false, "eventTitle" )]
        public string Name { get; set; }

        /// <summary>
        /// The item location, if any.
        /// </summary>
        [ThriftField( 7, false, "eventPlace" )]
        public string Location { get; set; }

        /// <summary>
        /// The name of the event's speaker, if it's an event.
        /// </summary>
        [ThriftField( 8, false, "eventSpeaker" )]
        public string SpeakerName { get; set; }

        /// <summary>
        /// Details about the event, in HTML format.
        /// </summary>
        [ThriftField( 9, false, "eventDetails" )]
        public string Details { get; set; }

        /// <summary>
        /// URL to the item's thumbnail picture, if any.
        /// </summary>
        [ThriftField( 10, false, "eventThumbnail" )]
        public string PictureThumbnailUrl { get; set; }

        /// <summary>
        /// URL to the item's location, if any.
        /// </summary>
        [ThriftField( 16, false, "locationHref" )]
        public string LocationUrl { get; set; }

        /// <summary>
        /// URL to more details about the item.
        /// </summary>
        [ThriftField( 17, false, "detailsLink" )]
        public string DetailsUrl { get; set; }

        /// <summary>
        /// Short details about the item.
        /// </summary>
        [ThriftField( 11, false, "secondLine" )]
        public string ShortDetails { get; set; }

        /// <summary>
        /// Special field that overrides the time if it's set.
        /// </summary>
        [ThriftField( 18, false, "timeSnippet" )]
        public string TimeOverride { get; set; }

        /// <summary>
        /// A value indicating whether the item's name should be hidden in a detailed view.
        /// </summary>
        [ThriftField( 21, false, "hideTitle" )]
        public bool? HideName { get; set; }

        /// <summary>
        /// A value indicating whether the item's thumbnail picture should be hidden in a detailed view.
        /// </summary>
        [ThriftField( 22, false, "hideThumbnail" )]
        public bool? HidePictureThumbnail { get; set; }

        /// <summary>
        /// A value indicating whether the item's date/location/speaker information should be hidden in a detailed view.
        /// </summary>
        [ThriftField( 23, false, "hideEventInfo" )]
        public bool? HideInformation { get; set; }

        /// <summary>
        /// The ID of the item's category.
        /// </summary>
        [ThriftField( 14, false, "eventCateg" )]
        public int? CategoryId { get; set; }

        /// <summary>
        /// The IDs of the item's tags.
        /// </summary>
        [ThriftField( 15, false, "eventTags" )]
        public string[] TagIds { get; set; }

        /// <summary>
        /// The ID of the item's parent pool.
        /// </summary>
        [ThriftField( 31, false, "parentPool" )]
        public long? ParentPoolId { get; set; }

        /// <summary>
        /// The item's log ID.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        public string LogId
        {
            get { return Id + "-" + Name; }
        }
    }
}