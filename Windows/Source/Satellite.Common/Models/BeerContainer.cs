// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// The beer containers used by Satellite.
    /// </summary>
    [ThriftEnum( "SatelliteBeerContainer" )]
    public enum BeerContainer
    {
        /// <summary>
        /// Draft beer, in a glass.
        /// </summary>
        [ThriftEnumMember( "DRAFT", 1 )]
        Draft,

        /// <summary>
        /// Bottled beer.
        /// </summary>
        [ThriftEnumMember( "BOTTLE", 2 )]
        Bottle,

        /// <summary>
        /// Big bottle of beer, meant to be shared.
        /// </summary>
        [ThriftEnumMember( "LARGE_BOTTLE", 3 )]
        BigBottle
    }
}