// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    /// <summary>
    /// Station from and to which trips are made.
    /// </summary>
    [ThriftStruct( "TransportStation" )]
    public sealed class Station
    {
        /// <summary>
        /// The station's ID.
        /// </summary>
        [ThriftField( 2, true, "id" )]
        public int Id { get; set; }

        /// <summary>
        /// The station's latitude.
        /// </summary>
        [ThriftField( 3, false, "latitude" )]
        [ThriftConverter( typeof( HafasCoordinateConverter ) )]
        public double? Latitude { get; set; }

        /// <summary>
        /// The station's longitude.
        /// </summary>
        [ThriftField( 4, false, "longitude" )]
        [ThriftConverter( typeof( HafasCoordinateConverter ) )]
        public double? Longitude { get; set; }

        /// <summary>
        /// The station's name.
        /// </summary>
        [ThriftField( 6, false, "name" )]
        public string Name { get; set; }


        /// <summary>
        /// The station's position.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface; computed from the latitude and longitude.
        /// </remarks>
        public GeoPosition Position
        {
            get { return new GeoPosition( Latitude.Value, Longitude.Value ); }
        }
    }
}