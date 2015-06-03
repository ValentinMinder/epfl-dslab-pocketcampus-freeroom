// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    [ThriftStruct( "DirectoryResponse" )]
    public sealed class SearchResponse
    {
        [ThriftField( 1, true, "status" )]
        public SearchStatus Status { get; set; }

        [ThriftField( 2, false, "results" )]
        public Person[] Results { get; set; }

        [ThriftField( 3, false, "resultSetCookie" )]
        public sbyte[] PaginationToken { get; set; }
    }
}
