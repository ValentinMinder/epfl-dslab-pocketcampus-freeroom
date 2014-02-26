// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventItem" )]
    public sealed class EventItem
    {
        [ThriftField( 1, true, "eventId" )]
        public long Id { get; set; }

        [ThriftField( 2, false, "startDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime StartDate { get; set; }

        [ThriftField( 3, false, "endDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime EndDate { get; set; }

        [ThriftField( 4, false, "fullDay" )]
        public bool? IsFullDay { get; set; }

        [ThriftField( 5, false, "eventPicture" )]
        public string PictureUrl { get; set; }

        [ThriftField( 6, false, "eventTitle" )]
        public string Name { get; set; }

        [ThriftField( 7, false, "eventPlace" )]
        public string Location { get; set; }

        [ThriftField( 8, false, "eventSpeaker" )]
        public string SpeakerName { get; set; }

        [ThriftField( 9, false, "eventDetails" )]
        public string Details { get; set; }

        [ThriftField( 10, false, "eventThumbnail" )]
        public string PictureThumbnailUrl { get; set; }

        [ThriftField( 16, false, "locationHref" )]
        public string LocationUrl { get; set; }

        [ThriftField( 17, false, "detailsLink" )]
        public string DetailsUrl { get; set; }

        [ThriftField( 11, false, "secondLine" )]
        public string ShortDetails { get; set; }

        [ThriftField( 18, false, "timeSnippet" )]
        public string TimeOverride { get; set; }

        [ThriftField( 21, false, "hideTitle" )]
        public bool HideTitle { get; set; }

        [ThriftField( 22, false, "hideThumbnail" )]
        public bool HidePictureThumbnail { get; set; }

        [ThriftField( 23, false, "hideEventInfo" )]
        public bool HideInformation { get; set; }
    }
}