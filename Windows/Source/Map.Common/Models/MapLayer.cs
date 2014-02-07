// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Map.Models
{
    /// <summary>
    /// Layer on a map.
    /// </summary>
    [ThriftStruct( "MapLayer" )]
    public sealed class MapLayer
    {
        /// <summary>
        /// The layer's name.
        /// </summary>
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        /// <summary>
        /// The URL of the layer's image.
        /// </summary>
        [ThriftField( 2, false, "drawableUrl" )]
        public string ImageUrl { get; set; }

        /// <summary>
        /// The layer's ID.
        /// </summary>
        [ThriftField( 3, true, "layerId" )]
        public long Id { get; set; }

        /// <summary>
        /// The duration the layer should be cached.
        /// </summary>
        [ThriftField( 5, true, "cacheInSeconds" )]
        public int CacheDuration { get; set; }

        /// <summary>
        /// Whether the layer should be displayed.
        /// </summary>
        [ThriftField( 6, true, "displayable" )]
        public bool CanDisplay { get; set; }


        /// <summary>
        /// The layer's items.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        public MapItem[] Items { get; set; }
    }
}