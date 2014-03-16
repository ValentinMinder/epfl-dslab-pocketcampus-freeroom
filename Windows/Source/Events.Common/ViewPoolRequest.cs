// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    /// <summary>
    /// Request to view a pool in the pool ViewModel.
    /// </summary>
    public sealed class ViewPoolRequest
    {
        /// <summary>
        /// Gets the pool's ID.
        /// </summary>
        public long PoolId { get; private set; }

        /// <summary>
        /// Gets the user's ticket for the pool, if any.
        /// </summary>
        public string UserTicket { get; private set; }

        /// <summary>
        /// Gets the ID of the item that should be displayed, if any.
        /// </summary>
        public long? ItemId { get; private set; }

        /// <summary>
        /// Gets a value indicating whether the item should be marked as favorite.
        /// </summary>
        public bool MarkItemAsFavorite { get; private set; }


        /// <summary>
        /// Creates a new ViewPoolRequest.
        /// </summary>
        public ViewPoolRequest( long poolId, string userTicket = null, long? itemId = null, bool markItemAsFavorite = false )
        {
            PoolId = poolId;
            UserTicket = userTicket;
            ItemId = itemId;
            MarkItemAsFavorite = markItemAsFavorite;
        }
    }
}