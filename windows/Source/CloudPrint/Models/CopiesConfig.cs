// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;
using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "CloudPrintMultipleCopies" )]
    public sealed class CopiesConfig : ObservableObject
    {
        private int _count;
        private bool _collate;

        [ThriftField( 1, true, "numberOfCopies" )]
        public int Count
        {
            get { return _count; }
            set { SetProperty( ref _count, value ); }
        }

        [ThriftField( 2, true, "collate" )]
        public bool Collate
        {
            get { return _collate; }
            set { SetProperty( ref _collate, value ); }
        }
    }
}