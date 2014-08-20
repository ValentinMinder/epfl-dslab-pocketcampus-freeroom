// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using PocketCampus.Common;
using ThriftSharp;

namespace PocketCampus.Map.Models
{
    /// <summary>
    /// Map item on the EPFL campus.
    /// </summary>
    [ThriftStruct( "MapItem" )]
    public sealed class MapItem
    {
        /// <summary>
        /// The item's name.
        /// </summary>
        [ThriftField( 1, true, "title" )]
        public string Name { get; set; }

        /// <summary>
        /// The item's description.
        /// </summary>
        [ThriftField( 2, false, "description" )]
        public string Description { get; set; }

        /// <summary>
        /// The item's latitude.
        /// </summary>
        [ThriftField( 3, true, "latitude" )]
        public double Latitude { get; set; }

        /// <summary>
        /// The item's longitude.
        /// </summary>
        [ThriftField( 4, true, "longitude" )]
        public double Longitude { get; set; }

        /// <summary>
        /// The ID of the layer the item is on.
        /// </summary>
        [ThriftField( 5, true, "layerId" )]
        public long LayerId { get; set; }

        /// <summary>
        /// The item's ID.
        /// </summary>
        [ThriftField( 6, true, "itemId" )]
        public long Id { get; set; }

        /// <summary>
        /// The floor the item is on.
        /// </summary>
        [ThriftField( 7, false, "floor" )]
        public int? Floor { get; set; }

        /// <summary>
        /// The category of the item.
        /// </summary>
        [ThriftField( 8, false, "category" )]
        public string Category { get; set; }


        /// <summary>
        /// The item's geographical position.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface; computed from the latitude and longitude.
        /// </remarks>
        [IgnoreDataMember]
        public GeoPosition Position
        {
            get { return new GeoPosition( Latitude, Longitude ); }
        }
    }
}