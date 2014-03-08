// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// A request to log an event that occurred.
    /// </summary>
    public sealed class EventLogRequest
    {
        /// <summary>
        /// Gets the ID of the event that should be logged.
        /// </summary>
        public string EventId { get; private set; }

        /// <summary>
        /// Gets the label of the event that should be logged.
        /// </summary>
        public string Label { get; private set; }

        /// <summary>
        /// Gets the ID of the screen on which the event should be logged, or null for the current screen.
        /// </summary>
        public string ScreenId { get; private set; }


        /// <summary>
        /// Creates a new EventLogRequest.
        /// </summary>
        public EventLogRequest( string eventId, string label, string screenId = null )
        {
            EventId = eventId;
            Label = label;
            ScreenId = screenId;
        }
    }
}