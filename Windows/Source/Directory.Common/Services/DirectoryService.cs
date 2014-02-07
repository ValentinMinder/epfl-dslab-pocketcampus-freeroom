// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Directory.Models;
using ThriftSharp;

// Plumbing for IDirectoryService.

namespace PocketCampus.Directory.Services
{
    public sealed class DirectoryService : ThriftServiceImplementation<IDirectoryService>, IDirectoryService
    {
        public DirectoryService( IServerConfiguration config )
            : base( config.CreateCommunication( "directory" ) )
        {
        }

        public Task<Person[]> SearchPeopleAsync( string query )
        {
            return CallAsync<string, Person[]>( x => x.SearchPeopleAsync, query );
        }

        public Task<string[]> SearchPartialMatchesAsync( string query )
        {
            return CallAsync<string, string[]>( x => x.SearchPartialMatchesAsync, query );
        }
    }
}