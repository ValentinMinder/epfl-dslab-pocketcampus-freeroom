// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum DoubleSidedConfig
    {
        SingleSide = 0, // not supported by the server; for UI purposes only
        LongEdge = 1,
        ShortEdge = 2
    }
}