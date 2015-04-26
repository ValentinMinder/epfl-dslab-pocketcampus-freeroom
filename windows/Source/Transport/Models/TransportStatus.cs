// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftEnum]
    public enum TransportStatus
    {
        Success = 200,
        NetworkError = 404
    }
}