// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Directory.Models;
using ThriftSharp;

// Plumbing for IDirectoryService.

namespace PocketCampus.Directory.Services
{
    public sealed class DirectoryService : ThriftServiceImplementation<IDirectoryService>, IDirectoryService
    {
        public DirectoryService( IServerAccess access ) : base( access.CreateCommunication( "directory" ) ) { }


        public Task<SearchResponse> SearchAsync( SearchRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<SearchRequest, CancellationToken, SearchResponse>( x => x.SearchAsync, request, cancellationToken );
        }
    }
}