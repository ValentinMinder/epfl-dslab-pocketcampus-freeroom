// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    [ThriftStruct( "SatelliteBeer" )]
    public sealed class Beer
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        [ThriftField( 2, true, "breweryName" )]
        public string BreweryName { get; set; }

        [ThriftField( 3, true, "originCountry" )]
        public string CountryName { get; set; }

        [ThriftField( 4, true, "alcoholRate" )]
        public double AlcoholRate { get; set; }

        [ThriftField( 5, true, "price" )]
        public double Price { get; set; }

        [ThriftField( 6, true, "description" )]
        public string Description { get; set; }
    }
}