// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    [ThriftStruct( "BeersResponse" )]
    public sealed class BeersResponse
    {
        [ThriftField( 1, false, "beerList" )]
        public Dictionary<BeerContainer, BeerMenuPart> BeerMenu { get; set; }

        [ThriftField( 2, true, "statusCode" )]
        public BeerMenuStatus Status { get; set; }
    }
}