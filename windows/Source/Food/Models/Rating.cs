// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "EpflRating" )]
    public sealed class Rating : ObservableObject
    {
        private double _value;
        private int _voteCount;

        [ThriftField( 1, true, "ratingValue" )]
        public double Value
        {
            get { return _value; }
            set { SetProperty( ref _value, value ); }
        }

        [ThriftField( 2, true, "voteCount" )]
        public int VoteCount
        {
            get { return _voteCount; }
            set { SetProperty( ref _voteCount, value ); }
        }


        public void AddVote( double vote )
        {
            Value = ( Value * VoteCount + vote ) / ( VoteCount + 1 );
            VoteCount++;
        }
    }
}