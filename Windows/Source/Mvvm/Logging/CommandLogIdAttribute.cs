// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// Marks Commands to indicate their log ID.
    /// </summary>
    [AttributeUsage( AttributeTargets.Property )]
    public sealed class CommandLogIdAttribute : Attribute
    {
        /// <summary>
        /// Gets the ID of the Command this attribute is applied to.
        /// </summary>
        public string Id { get; private set; }


        /// <summary>
        /// Creates a new CommandLogIdAttribute.
        /// </summary>
        public CommandLogIdAttribute( string id )
        {
            Id = id;
        }
    }
}