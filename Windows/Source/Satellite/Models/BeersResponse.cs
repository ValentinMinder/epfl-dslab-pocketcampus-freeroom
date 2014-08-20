// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// Response to a beer menu request.
    /// </summary>
    [ThriftStruct( "BeersResponse" )]
    public sealed class BeersResponse
    {
        /// <summary>
        /// The beer menu.
        /// </summary>
        [ThriftField( 1, false, "beerList" )]
        public Dictionary<BeerContainer, BeerMenuPart> BeerMenu { get; set; }

        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public BeerMenuStatus Status { get; set; }
    }
}