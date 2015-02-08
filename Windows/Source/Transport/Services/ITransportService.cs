// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Transport.Models;
using ThriftSharp;

namespace PocketCampus.Transport.Services
{
    [ThriftService( "TransportService" )]
    public interface ITransportService
    {
        [ThriftMethod( "searchForStations" )]
        Task<StationSearchResponse> SearchStationsAsync( [ThriftParameter( 1, "request" )] StationSearchRequest request, CancellationToken cancellationToken );

        [ThriftMethod( "getDefaultStations" )]
        Task<DefaultStationsResponse> GetDefaultStationsAsync( CancellationToken cancellationToken );

        [ThriftMethod( "searchForTrips" )]
        Task<TripSearchResponse> SearchTripsAsync( [ThriftParameter( 1, "request" )] TripSearchRequest request, CancellationToken cancellationToken );
    }
}