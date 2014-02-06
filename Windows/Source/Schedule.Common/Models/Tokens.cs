// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThriftSharp;

// Plumbing for authentication

namespace PocketCampus.Schedule.Models
{
    [ThriftStruct( "ScheduleToken" )]
    public sealed class ScheduleToken : IAuthenticationToken
    {
        [ThriftField( 1, true, "tequilaToken" )]
        public string AuthenticationKey { get; set; }

        [ThriftField( 2, true, "sessionId" )]
        public string SessionId { get; set; }
    }
}