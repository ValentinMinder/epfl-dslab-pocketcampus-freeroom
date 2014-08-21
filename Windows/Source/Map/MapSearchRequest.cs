// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;

namespace PocketCampus.Map
{
    /// <summary>
    /// Search request for either a query or a map item (or nothing).
    /// </summary>
    public sealed class MapSearchRequest
    {
        /// <summary>
        /// Gets the query, if any.
        /// </summary>
        public string Query { get; private set; }

        /// <summary>
        /// Gets the map item, if any.
        /// </summary>
        public MapItem Item { get; private set; }


        /// <summary>
        /// Creates an empty MapSearchRequest.
        /// </summary>
        public MapSearchRequest() { }

        /// <summary>
        /// Creates a MapSearchRequest with the specified query.
        /// </summary>
        public MapSearchRequest( string query )
        {
            Query = query;
        }

        /// <summary>
        /// Creates a MapSearchRequest with the specified map item.
        /// </summary>
        public MapSearchRequest( MapItem item )
        {
            Item = item;
        }
    }
}