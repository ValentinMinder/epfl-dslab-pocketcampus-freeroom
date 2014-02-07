// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// A request to enable logging for an object's commands.
    /// </summary>
    public sealed class CommandLoggingRequest
    {
        /// <summary>
        /// Gets the object whose commands should be logged.
        /// </summary>
        public object Object { get; private set; }

        /// <summary>
        /// Creates a new LoggingRequest for the specified object.
        /// </summary>
        public CommandLoggingRequest( object obj )
        {
            Object = obj;
        }
    }
}