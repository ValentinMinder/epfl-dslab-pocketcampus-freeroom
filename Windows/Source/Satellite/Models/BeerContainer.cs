// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// The beer containers used by Satellite.
    /// </summary>
    [ThriftEnum]
    public enum BeerContainer
    {
        /// <summary>
        /// Draft beer, in a glass.
        /// </summary>
        Draft = 1,

        /// <summary>
        /// Bottled beer.
        /// </summary>
        Bottle = 2,

        /// <summary>
        /// Big bottle of beer, meant to be shared.
        /// </summary>
        BigBottle = 3
    }
}