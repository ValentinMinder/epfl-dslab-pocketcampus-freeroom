// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    [ThriftEnum]
    public enum SearchStatus
    {
        Success = 200,
        InternalError = 500
    }
}