// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
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
        public TransportService( IServerAccess access )
            : base( access.CreateCommunication( "transport" ) )
        {

        }

        public Task<Station[]> GetSuggestionsAsync( string query, CancellationToken cancellationToken )
        {
            return CallAsync<string, CancellationToken, Station[]>( x => x.GetSuggestionsAsync, query, cancellationToken );
        }

        public Task<Station[]> GetStationsAsync( string[] names, CancellationToken cancellationToken )
        {
            return CallAsync<string[], CancellationToken, Station[]>( x => x.GetStationsAsync, names, cancellationToken );
        }

        public async Task<TripsResult> GetTripsAsync( string fromName, string toName, CancellationToken cancellationToken )
        {
            var result = await CallAsync<string, string, CancellationToken, TripsResult>( x => x.GetTripsAsync, fromName, toName, cancellationToken );
            foreach ( var trip in result.Trips )
            {
                // remove weird connections without a line
                // they often happen when going to a station
                // e.g. Lausanne-Flon -> Lausanne Gare -> Lausanne (no line, no arrival/departure times) -> Zürich HB
                trip.Connections = trip.Connections.Where( c => c.Line != null ).ToArray();
            }
            result.Trips = result.Trips.OrderBy( t => t.DepartureTime ).ToArray();
            return result;
        }
    }
}