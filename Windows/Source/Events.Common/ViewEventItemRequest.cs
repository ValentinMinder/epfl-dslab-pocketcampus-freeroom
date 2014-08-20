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
        /// Gets a value indicating whether the item can be marked as favorite.
        /// </summary>
        public bool CanBeFavorite { get; private set; }


        /// <summary>
        /// Creates a new ViewEventItemRequest.
        /// </summary>
        public ViewEventItemRequest( long itemId, bool canBeFavorite )
        {
            ItemId = itemId;
            CanBeFavorite = canBeFavorite;
        }
    }
}