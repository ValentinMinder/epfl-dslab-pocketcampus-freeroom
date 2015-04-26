// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "VoteRequest" )]
    public sealed class VoteRequest
    {
        [ThriftField( 1, true, "mealId" )]
        public long MealId { get; set; }

        [ThriftField( 2, true, "rating" )]
        public double RatingValue { get; set; }

        [ThriftField( 3, true, "deviceId" )]
        public string DeviceId { get; set; }
    }
}