// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftEnum]
    public enum VoteStatus
    {
        AlreadyVoted = 1,
        Success = 2,
        TooEarly = 3,
        MealInDistantPast = 5
    }
}