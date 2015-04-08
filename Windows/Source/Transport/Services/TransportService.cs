// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Transport.Models;
using ThriftSharp;

// Plumbing for ITransportService

namespace PocketCampus.Transport.Services
{
    public sealed class TransportService : ThriftServiceImplementation<ITransportService>, ITransportService
    {
        public TransportService( IServerAccess access ) : base( access.CreateCommunication( "transport" ) ) { }

        public Task<StationSearchResponse> SearchStationsAsync( StationSearchRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<StationSearchRequest, CancellationToken, StationSearchResponse>( x => x.SearchStationsAsync, request, cancellationToken );
        }

        public Task<DefaultStationsResponse> GetDefaultStationsAsync( CancellationToken cancellationToken )
        {
            return CallAsync<CancellationToken, DefaultStationsResponse>( x => x.GetDefaultStationsAsync, cancellationToken );
        }

        public Task<TripSearchResponse> SearchTripsAsync( TripSearchRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<TripSearchRequest, CancellationToken, TripSearchResponse>( x => x.SearchTripsAsync, request, cancellationToken );
        }
    }
}