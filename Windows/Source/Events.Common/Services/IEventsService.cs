// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Events.Models;
using ThriftSharp;

namespace PocketCampus.Events.Services
{
    [ThriftService( "EventsService" )]
    public interface IEventsService
    {
        [ThriftMethod( "getEventItem" )]
        Task<EventItemResponse> GetEventItemAsync( [ThriftParameter( 1, "iRequest" )] EventItemRequest request );

        [ThriftMethod( "getEventPool" )]
        Task<EventPoolResponse> GetEventPoolAsync( [ThriftParameter( 1, "iRequest" )]EventPoolRequest request );

        [ThriftMethod( "sendStarredItemsByEmail" )]
        Task<FavoriteEmailResponse> SendFavoriteItemsByEmailAsync( [ThriftParameter( 1, "iRequest" )] FavoriteEmailRequest request );
    }
}