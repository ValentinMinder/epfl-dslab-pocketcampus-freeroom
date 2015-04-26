// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "VoteResponse" )]
    public sealed class VoteResponse
    {
        [ThriftField( 1, true, "submitStatus" )]
        public VoteStatus Status { get; set; }
    }
}