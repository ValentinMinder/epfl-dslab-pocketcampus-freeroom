// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The possible response statuses of vote requests.
    /// </summary>
    [ThriftEnum( "SubmitStatus" )]
    public enum VoteStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        [ThriftEnumMember( "VALID", 2 )]
        Success,

        /// <summary>
        /// The user has already voted.
        /// </summary>
        [ThriftEnumMember( "ALREADY_VOTED", 1 )]
        AlreadyVoted,

        /// <summary>
        /// It is too early to vote.
        /// </summary>
        [ThriftEnumMember( "TOO_EARLY", 3 )]
        TooEarly
    }
}