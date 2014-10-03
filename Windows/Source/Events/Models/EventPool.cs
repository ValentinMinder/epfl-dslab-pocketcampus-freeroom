// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Pool of event items.
    /// </summary>
    [ThriftStruct( "EventPool" )]
    public sealed class EventPool
    {
        /// <summary>
        /// The ID of the root pool.
        /// </summary>
        public const long RootId = -1;

        /// <summary>
        /// The pool's ID.
        /// </summary>
        [ThriftField( 1, true, "poolId" )]
        public long Id { get; set; }

        /// <summary>
        /// URL to the pool's picture, if any.
        /// </summary>
        [ThriftField( 5, false, "poolPicture" )]
        public string PictureUrl { get; set; }

        /// <summary>
        /// The pool's name, if any.
        /// </summary>
        [ThriftField( 6, false, "poolTitle" )]
        public string Name { get; set; }

        /// <summary>
        /// The pool's location, if any.
        /// </summary>
        [ThriftField( 7, false, "poolPlace" )]
        public string Location { get; set; }

        /// <summary>
        /// A value indicating whether to disable the favorite button in the child events.
        /// </summary>
        [ThriftField( 10, false, "disableStar" )]
        public bool? DisableFavorites { get; set; }

        /// <summary>
        /// A value indicating whether to disable the category filter for the pool.
        /// </summary>
        [ThriftField( 11, false, "disableFilterByCateg" )]
        public bool? DisableCategoryFiltering { get; set; }

        /// <summary>
        /// A value indicating whether to disable the tag filter for the pool.
        /// </summary>
        [ThriftField( 12, false, "disableFilterByTags" )]
        public bool? DisableTagFiltering { get; set; }

        /// <summary>
        /// A value indicating whether to enable code scanning for the pool.
        /// </summary>
        [ThriftField( 13, false, "enableScan" )]
        public bool? EnableCodeScanning { get; set; }

        /// <summary>
        /// The fallback text if there are no events.
        /// </summary>
        [ThriftField( 14, false, "noResultText" )]
        public string NoEventsFallbackText { get; set; }

        /// <summary>
        /// A value indicating whether the pool should always refresh when coming back to it.
        /// </summary>
        [ThriftField( 16, false, "refreshOnBack" )]
        public bool? AlwaysRefresh { get; set; }

        /// <summary>
        /// A value indicating whether an e-mail with favorites can be sent from the pool.
        /// </summary>
        [ThriftField( 19, false, "sendStarredItems" )]
        public bool? EnableFavoriteEmailRequest { get; set; }

        /// <summary>
        /// URL that overrides the pool's detailed view if set.
        /// </summary>
        [ThriftField( 21, false, "overrideLink" )]
        public string OverrideTargetUrl { get; set; }


        /// <summary>
        /// The pool's items.
        /// </summary>
        /// <remarks>
        /// Not part of the Thrift interface.
        /// </remarks>
        public EventItem[] Items { get; set; }

        /// <summary>
        /// The pool's log ID.
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