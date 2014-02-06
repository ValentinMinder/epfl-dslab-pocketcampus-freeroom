// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    /// <summary>
    /// Occurs when an error occurs on the server while executing a request.
    /// </summary>
    [ThriftStruct( "LDAPException" )]
    public sealed class DirectoryException : Exception
    {
        /// <summary>
        /// The exception message.
        /// </summary>
        [ThriftField( 1, false, "message" )]
        public new string Message { get; set; }
    }
}