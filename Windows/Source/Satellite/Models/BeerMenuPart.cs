// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// Part of a beer menu.
    /// </summary>
    [ThriftStruct( "SatelliteMenuPart" )]
    public sealed class BeerMenuPart
    {
        /// <summary>
        /// The beers of the month (if there are any).
        /// </summary>
        [ThriftField( 1, true, "beersOfTheMonth" )]
        public Beer[] BeersOfTheMonth { get; set; }

        /// <summary>
        /// The beers, grouped by their type.
        /// </summary>
        [ThriftField( 2, true, "beers" )]
        public Dictionary<string, Beer[]> Beers { get; set; }
    }
}