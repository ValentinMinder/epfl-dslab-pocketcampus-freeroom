// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
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
        /// Asynchronously searches for all people whose name, SCIPER number, phone number or other attribute matches the specified request.
        /// </summary>
        [ThriftMethod( "searchDirectory" )]
        Task<SearchResponse> SearchAsync( [ThriftParameter( 1, "req" )] SearchRequest request, CancellationToken cancellationToken );
    }
}