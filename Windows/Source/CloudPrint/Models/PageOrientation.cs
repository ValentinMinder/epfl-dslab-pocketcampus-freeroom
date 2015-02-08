// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum PageOrientation
    {
        Portrait = 3,
        Landscape = 4,
        ReverseLandscape = 5,
        ReversePortrait = 6
    }
}