// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    /// <summary>
    /// Request to search the EPFL directory.
    /// </summary>
    [ThriftStruct( "DirectoryRequest" )]
    public sealed class SearchRequest
    {
        /// <summary>
        /// The query.
        /// </summary>
        [ThriftField( 1, true, "query" )]
        public string Query { get; set; }

        /// <summary>
        /// The pagination token, if any.
        /// </summary>
        [ThriftField( 3, false, "resultSetCookie" )]
        public sbyte[] PaginationToken { get; set; }

        /// <summary>
        /// The language of the query.
        /// </summary>
        [ThriftField( 4, false, "language" )]
        public string Language { get; set; }
    }
}