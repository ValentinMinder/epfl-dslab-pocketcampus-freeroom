// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventPool" )]
    public sealed class EventPool
    {
        public const long RootId = -1;

        [ThriftField( 1, true, "poolId" )]
        public long Id { get; set; }

        [ThriftField( 5, false, "poolPicture" )]
        public string PictureUrl { get; set; }

        [ThriftField( 6, false, "poolTitle" )]
        public string Name { get; set; }

        [ThriftField( 7, false, "poolPlace" )]
        public string Location { get; set; }

        [ThriftField( 10, false, "disableStar" )]
        public bool? DisableFavorites { get; set; }

        [ThriftField( 11, false, "disableFilterByCateg" )]
        public bool? DisableCategoryFiltering { get; set; }

        [ThriftField( 12, false, "disableFilterByTags" )]
        public bool? DisableTagFiltering { get; set; }

        [ThriftField( 13, false, "enableScan" )]
        public bool? EnableCodeScanning { get; set; }

        [ThriftField( 14, false, "noResultText" )]
        public string NoResultsFallbackText { get; set; }

        [ThriftField( 16, false, "refreshOnBack" )]
        public bool? AlwaysRefresh { get; set; }

        [ThriftField( 19, false, "sendStarredItems" )]
        public bool? EnableFavoriteEmailRequest { get; set; }

        [ThriftField( 21, false, "overrideLink" )]
        public string OverrideTargetUrl { get; set; }

        public EventItem[] Items { get; set; }
    }
}