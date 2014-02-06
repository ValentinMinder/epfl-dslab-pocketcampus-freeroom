// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Directory.Models;
using ThriftSharp;

namespace PocketCampus.Directory.Services
{
    /// <summary>
    /// The directory server service.
    /// </summary>
    [ThriftService( "DirectoryService" )]
    public interface IDirectoryService
    {
        /// <summary>
        /// Asynchronously searches for all people whose name or SCIPER number matches the specified query.
        /// </summary>
        [ThriftMethod( "searchPersons" )]
        [ThriftThrows( 1, "le", typeof( DirectoryException ) )]
        Task<Person[]> SearchPeopleAsync( [ThriftParameter( 1, "nameOrSciper" )] string query );

        /// <summary>
        /// Asynchronously searches for all names of people matching the specified query.
        /// </summary>
        [ThriftMethod( "autocomplete" )]
        Task<string[]> SearchPartialMatchesAsync( [ThriftParameter( 1, "constraint" )] string query );
    }
}