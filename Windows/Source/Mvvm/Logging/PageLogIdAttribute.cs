// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// Marks ViewModels to indicate their log ID.
    /// </summary>
    [AttributeUsage( AttributeTargets.Class )]
    public sealed class PageLogIdAttribute : Attribute
    {
        /// <summary>
        /// Gets the ID of the ViewModel this attribute is applied to.
        /// </summary>
        public string Id { get; private set; }


        /// <summary>
        /// Creates a new PageLogIdAttribute.
        /// </summary>
        public PageLogIdAttribute( string id )
        {
            Id = id;
        }
    }
}