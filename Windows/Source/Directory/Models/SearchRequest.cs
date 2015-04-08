// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    [ThriftStruct( "DirectoryRequest" )]
    public sealed class SearchRequest
    {
        [ThriftField( 1, true, "query" )]
        public string Query { get; set; }

        [ThriftField( 3, false, "resultSetCookie" )]
        public sbyte[] PaginationToken { get; set; }

        [ThriftField( 4, false, "language" )]
        public string Language { get; set; }
    }
}