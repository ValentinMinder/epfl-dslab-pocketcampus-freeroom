// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    public sealed class ViewEventItemRequest
    {
        public long ItemId { get; private set; }

        public bool CanBeFavorite { get; private set; }


        public ViewEventItemRequest( long itemId, bool canBeFavorite )
        {
            ItemId = itemId;
            CanBeFavorite = canBeFavorite;
        }
    }
}