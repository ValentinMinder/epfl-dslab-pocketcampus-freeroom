// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Events.Models;
using ThriftSharp;

namespace PocketCampus.Events.Services
{
    /// <summary>
    /// The Events service.
    /// </summary>
    [ThriftService( "EventsService" )]
    public interface IEventsService
    {
        /// <summary>
        /// Asynchronously gets an event item.
        /// </summary>
        [ThriftMethod( "getEventItem" )]
        Task<EventItemResponse> GetEventItemAsync( [ThriftParameter( 1, "iRequest" )] EventItemRequest request, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously gets an event pool.
        /// </summary>
        [ThriftMethod( "getEventPool" )]
        Task<EventPoolResponse> GetEventPoolAsync( [ThriftParameter( 1, "iRequest" )]EventPoolRequest request, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously requests an e-mail with the items marked as favorite by the user.
        /// </summary>
        [ThriftMethod( "sendStarredItemsByEmail" )]
        Task<FavoriteEmailResponse> SendFavoriteItemsByEmailAsync( [ThriftParameter( 1, "iRequest" )] FavoriteEmailRequest request );
    }
}