// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Events.Models;

namespace PocketCampus.Events
{
    /// <summary>
    /// A group of EventItems, with their category.
    /// </summary>
    public sealed class EventItemGroup : List<EventItem>
    {
        /// <summary>
        /// Gets the category name.
        /// </summary>
        public string CategoryName { get; private set; }


        /// <summary>
        /// Creates a new EventItemGroup.
        /// </summary>
        public EventItemGroup( string categoryName, IEnumerable<EventItem> items )
            : base( items )
        {
            CategoryName = categoryName;
        }
    }
}