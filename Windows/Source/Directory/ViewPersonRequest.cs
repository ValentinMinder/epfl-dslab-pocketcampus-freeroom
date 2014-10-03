// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Directory
{
    /// <summary>
    /// Search request for either a query or a person (or nothing).
    /// </summary>
    public sealed class ViewPersonRequest
    {
        /// <summary>
        /// Gets the query, if any.
        /// </summary>
        public string Name { get; private set; }

        /// <summary>
        /// Creates an empty ViewPersonRequest.
        /// </summary>
        public ViewPersonRequest() { }

        /// <summary>
        /// Creates a ViewPersonRequest with the specified query.
        /// </summary>
        public ViewPersonRequest( string name )
        {
            Name = name;
        }
    }
}