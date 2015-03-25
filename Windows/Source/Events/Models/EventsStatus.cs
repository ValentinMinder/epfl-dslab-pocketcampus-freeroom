// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftEnum]
    public enum EventsStatus
    {
        Success = 200,
        NetworkError = 400,
        AuthenticationError = 500
    }
}