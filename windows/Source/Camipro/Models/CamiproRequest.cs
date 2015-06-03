﻿// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "CamiproRequest" )]
    public sealed class CamiproRequest
    {
        // Required but unused
        [ThriftField( 1, true, "iSessionId" )]
        public SessionId Session
        {
            get { return new SessionId(); }
            set { }
        }

        [ThriftField( 2, true, "iLanguage" )]
        public string Language { get; set; }
    }
}