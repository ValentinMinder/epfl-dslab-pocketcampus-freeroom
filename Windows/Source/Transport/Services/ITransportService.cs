// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Transport.Models;
using ThriftSharp;

namespace PocketCampus.Transport.Services
{
    /// <summary>
    /// The transport server service.
    /// </summary>
    [ThriftService( "TransportService" )]
    public interface ITransportService
    {
        /// <summary>
        /// Asynchronously gets station suggestions for the specified query.
        /// </summary>
        [ThriftMethod( "autocomplete" )]
        Task<Station[]> GetSuggestionsAsync( [ThriftParameter( 1, "constraint" )] string query, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously gets stations from the specified names.
        /// </summary>
        [ThriftMethod( "getLocationsFromNames" )]
        Task<Station[]> GetStationsAsync( [ThriftParameter( 1, "names" )] string[] names, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously gets the next trips from and to the specified stations.
        /// </summary>
        [ThriftMethod( "getTrips" )]
        Task<TripsResult> GetTripsAsync( [ThriftParameter( 1, "from" )] string fromName, [ThriftParameter( 2, "to" )] string toName, CancellationToken cancellationToken );
    }
}