// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Events.Models;
using ThriftSharp;

// Plumbing for IEventsService

namespace PocketCampus.Events.Services
{
    public sealed class EventsService : ThriftServiceImplementation<IEventsService>, IEventsService
    {
        public EventsService( IServerAccess access ) : base( access.CreateCommunication( "events" ) ) { }

        public Task<EventItemResponse> GetEventItemAsync( EventItemRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<EventItemRequest, CancellationToken, EventItemResponse>( x => x.GetEventItemAsync, request, cancellationToken );
        }

        public Task<EventPoolResponse> GetEventPoolAsync( EventPoolRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<EventPoolRequest, CancellationToken, EventPoolResponse>( x => x.GetEventPoolAsync, request, cancellationToken );
        }

        public Task<FavoriteEmailResponse> SendFavoriteItemsByEmailAsync( FavoriteEmailRequest request )
        {
            return CallAsync<FavoriteEmailRequest, FavoriteEmailResponse>( x => x.SendFavoriteItemsByEmailAsync, request );
        }
    }
}