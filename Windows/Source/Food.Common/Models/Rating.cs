// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Ratings for meals or restaurants.
    /// </summary>
    [ThriftStruct( "EpflRating" )]
    public sealed class Rating : ObservableObject
    {
        private double _value;
        private int _voteCount;

        /// <summary>
        /// The rating value, from 0 to 1.
        /// </summary>
        [ThriftField( 1, true, "ratingValue" )]
        public double Value
        {
            get { return _value; }
            set { SetProperty( ref _value, value ); }
        }

        /// <summary>
        /// The number of votes.
        /// </summary>
        [ThriftField( 2, true, "voteCount" )]
        public int VoteCount
        {
            get { return _voteCount; }
            set { SetProperty( ref _voteCount, value ); }
        }
    }
}