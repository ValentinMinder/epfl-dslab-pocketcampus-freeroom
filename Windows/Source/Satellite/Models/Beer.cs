// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// Beer in Satellite's menu.
    /// </summary>
    [ThriftStruct( "SatelliteBeer" )]
    public sealed class Beer
    {
        /// <summary>
        /// The beer's name.
        /// </summary>
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        /// <summary>
        /// The name of the brewery that brewed the beer.
        /// </summary>
        [ThriftField( 2, true, "breweryName" )]
        public string BreweryName { get; set; }

        /// <summary>
        /// The name of the country the beer comes from.
        /// </summary>
        [ThriftField( 3, true, "originCountry" )]
        public string CountryName { get; set; }

        /// <summary>
        /// The beer's alcohol rate.
        /// </summary>
        [ThriftField( 4, true, "alcoholRate" )]
        public double AlcoholRate { get; set; }

        /// <summary>
        /// The beer's price.
        /// </summary>
        [ThriftField( 5, true, "price" )]
        public double Price { get; set; }

        /// <summary>
        /// The beer's description.
        /// </summary>
        [ThriftField( 6, true, "description" )]
        public string Description { get; set; }
    }
}