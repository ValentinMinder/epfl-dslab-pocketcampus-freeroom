using System.Collections.Generic;
using PocketCampus.Events.Models;

namespace PocketCampus.Events
{
    public sealed class EventItemGroup : List<EventItem>
    {
        public string CategoryName { get; private set; }

        public EventItemGroup( string categoryName, IEnumerable<EventItem> items )
            : base( items )
        {
            CategoryName = categoryName;
        }
    }
}