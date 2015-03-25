// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    [ThriftStruct( "SatelliteMenuPart" )]
    public sealed class BeerMenuPart
    {
        [ThriftField( 1, true, "beersOfTheMonth" )]
        public Beer[] BeersOfTheMonth { get; set; }

        [ThriftField( 2, true, "beers" )]
        public Dictionary<string, Beer[]> Beers { get; set; }
    }
}