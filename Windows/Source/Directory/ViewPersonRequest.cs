// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Directory
{
    public sealed class ViewPersonRequest
    {
        public string Query { get; private set; }


        public ViewPersonRequest() { }

        public ViewPersonRequest( string query )
        {
            Query = query;
        }
    }
}