// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    [ThriftStruct( "DirectoryResponse" )]
    public sealed class SearchResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 1, true, "status" )]
        public SearchStatus Status { get; set; }

        /// <summary>
        /// The results.
        /// </summary>
        [ThriftField( 2, false, "results" )]
        public Person[] Results { get; set; }

        /// <summary>
        /// The pagination token, if any.
        /// </summary>
        [ThriftField( 3, false, "resultSetCookie" )]
        public sbyte[] PaginationToken { get; set; }
    }
}
