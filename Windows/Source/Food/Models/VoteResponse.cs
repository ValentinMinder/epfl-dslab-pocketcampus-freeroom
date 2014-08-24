// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Response for vote requests.
    /// </summary>
    [ThriftStruct( "VoteResponse" )]
    public sealed class VoteResponse
    {
        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 1, true, "submitStatus" )]
        public VoteStatus Status { get; set; }
    }
}