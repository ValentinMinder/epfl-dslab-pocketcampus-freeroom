// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    /// <summary>
    /// Request to display an event item.
    /// </summary>
    public sealed class ViewEventItemRequest
    {
        /// <summary>
        /// Gets the item's ID.
        /// </summary>
        public long ItemId { get; private set; }

        /// <summary>
        /// Gets an option for the item's "favorite" status.
        /// </summary>
        public EventItemFavoriteOption FavoriteOption { get; private set; }

        /// <summary>
        /// Gets the user's ticket, if there is one.
        /// </summary>
        public string UserTicket { get; private set; }


        /// <summary>
        /// Creates a new ViewEventItemRequest.
        /// </summary>
        public ViewEventItemRequest( long itemId, EventItemFavoriteOption favoriteOption, string userTicket = null )
        {
            ItemId = itemId;
            FavoriteOption = favoriteOption;
            UserTicket = userTicket;
        }
    }
}