// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    /// <summary>
    /// Options for event items' "favorite" status.
    /// </summary>
    public enum EventItemFavoriteOption
    {
        /// <summary>
        /// The item must be marked as a favorite.
        /// </summary>
        Requested,

        /// <summary>
        /// The user can choose to mark the item as a favorite.
        /// </summary>
        Optional,

        /// <summary>
        /// The user cannot mark the item as a favorite.
        /// </summary>
        Forbidden
    }
}