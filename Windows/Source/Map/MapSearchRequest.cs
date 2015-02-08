// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;

namespace PocketCampus.Map
{
    // Union type: either a query, or an item, or nothing
    public sealed class MapSearchRequest
    {
        public string Query { get; private set; }

        public MapItem Item { get; private set; }


        public MapSearchRequest() { }


        public MapSearchRequest( string query )
        {
            Query = query;
        }

        public MapSearchRequest( MapItem item )
        {
            Item = item;
        }
    }
}