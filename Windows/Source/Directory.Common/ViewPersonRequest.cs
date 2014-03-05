// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Directory.Models;

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
        /// Gets the person, if any.
        /// </summary>
        public Person Person { get; private set; }


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

        /// <summary>
        /// Creates a ViewPersonRequest with the specified person.
        /// </summary>
        public ViewPersonRequest( Person person )
        {
            Person = person;
        }
    }
}