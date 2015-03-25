// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    [ThriftEnum]
    public enum BeerMenuStatus
    {
        Success = 200,
        NetworkError = 407
    }
}