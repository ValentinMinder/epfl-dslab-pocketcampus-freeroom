// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "CloudPrintPageRange" )]
    public sealed class PageRange
    {
        [ThriftField( 1, true, "pageFrom" )]
        public int From { get; set; }

        [ThriftField( 2, true, "pageTo" )]
        public int To { get; set; }
    }
}