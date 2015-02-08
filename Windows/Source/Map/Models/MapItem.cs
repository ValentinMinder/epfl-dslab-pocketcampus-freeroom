// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using PocketCampus.Common;
using ThriftSharp;

namespace PocketCampus.Map.Models
{
    [ThriftStruct( "MapItem" )]
    public sealed class MapItem
    {
        [ThriftField( 1, true, "title" )]
        public string Name { get; set; }

        [ThriftField( 2, false, "description" )]
        public string Description { get; set; }

        [ThriftField( 3, true, "latitude" )]
        public double Latitude { get; set; }

        [ThriftField( 4, true, "longitude" )]
        public double Longitude { get; set; }

        [ThriftField( 5, true, "layerId" )]
        public long LayerId { get; set; }

        [ThriftField( 6, true, "itemId" )]
        public long Id { get; set; }

        [ThriftField( 7, false, "floor" )]
        public int? Floor { get; set; }

        [ThriftField( 8, false, "category" )]
        public string Category { get; set; }


        [IgnoreDataMember]
        public GeoPosition Position
        {
            get { return new GeoPosition( Latitude, Longitude ); }
        }
    }
}