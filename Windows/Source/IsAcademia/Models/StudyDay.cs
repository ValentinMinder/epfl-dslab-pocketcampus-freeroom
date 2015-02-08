// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    [ThriftStruct( "StudyDay" )]
    public sealed class StudyDay
    {
        [ThriftField( 1, true, "day" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Day { get; set; }

        [ThriftField( 2, true, "periods" )]
        public Period[] Periods { get; set; }
    }
}