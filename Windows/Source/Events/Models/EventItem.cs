// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    // This can be anything: an event, a person, a laboratory, a poster, ...
    [ThriftStruct( "EventItem" )]
    public sealed class EventItem
    {
        public static readonly int? FavoriteCategoryId = -2;


        [ThriftField( 1, true, "eventId" )]
        public long Id { get; set; }

        [ThriftField( 2, false, "startDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? StartDate { get; set; }

        [ThriftField( 3, false, "endDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? EndDate { get; set; }

        [ThriftField( 4, false, "fullDay" )]
        public bool? IsFullDay { get; set; }

        [ThriftField( 5, false, "eventPicture" )]
        public string PictureUrl { get; set; }

        [ThriftField( 6, false, "eventTitle" )]
        public string Name { get; set; }

        // May be an EPFL room, or something else, in which case LocationUrl is set
        [ThriftField( 7, false, "eventPlace" )]
        public string Location { get; set; }

        // Sometimes in HTML
        [ThriftField( 8, false, "eventSpeaker" )]
        public string SpeakerName { get; set; }

        // In HTML
        [ThriftField( 9, false, "eventDetails" )]
        public string Details { get; set; }

        [ThriftField( 10, false, "eventThumbnail" )]
        public string ThumbnailUrl { get; set; }

        [ThriftField( 16, false, "locationHref" )]
        public string LocationUrl { get; set; }

        [ThriftField( 17, false, "detailsLink" )]
        public string DetailsUrl { get; set; }

        [ThriftField( 11, false, "secondLine" )]
        public string ShortDetails { get; set; }

        // If set, overrides the time display
        [ThriftField( 18, false, "timeSnippet" )]
        public string TimeOverride { get; set; }

        // If true, the name should be hidden from the detailed item view
        [ThriftField( 21, false, "hideTitle" )]
        public bool? HideName { get; set; }

        // If true, the thumbnail should be hidden from the detailed item view
        [ThriftField( 22, false, "hideThumbnail" )]
        public bool? HideThumbnail { get; set; }

        // If set, date/location/speaker information should be hidden from the detailed item view
        [ThriftField( 23, false, "hideEventInfo" )]
        public bool? HideInformation { get; set; }

        [ThriftField( 14, false, "eventCateg" )]
        public int? CategoryId { get; set; }

        [ThriftField( 15, false, "eventTags" )]
        public string[] TagIds { get; set; }

        [ThriftField( 31, false, "parentPool" )]
        public long? ParentPoolId { get; set; }


        // For logging purposes only
        public string LogId
        {
            get { return Id + "-" + Name; }
        }
    }
}