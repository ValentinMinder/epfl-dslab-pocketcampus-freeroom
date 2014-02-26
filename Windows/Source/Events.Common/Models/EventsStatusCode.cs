// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// The possible response statuses of server requests.
    /// </summary>
    /// <remarks>
    /// Not in the Thrift interface (it uses undocumented constants).
    /// </remarks>
    [ThriftEnum( "_" )]
    public enum EventsStatusCode
    {
        OK = 200,
        Forbidden = 400,
        ServerError = 500
    }
}