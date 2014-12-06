﻿using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum PagesPerSheet
    {
        One = 1, // not supported by the server; for UI purposes only
        Two = 2,
        Four = 4,
        Six = 6,
        Nine = 9,
        Sixteen = 16
    }
}