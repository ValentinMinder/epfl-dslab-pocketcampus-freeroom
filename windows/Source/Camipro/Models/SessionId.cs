// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "SessionId" )]
    public sealed class SessionId
    {
        // Required, but unused
        [ThriftField( 1, true, "tos" )]
        public int Intention
        {
            get { return 0; }
            set { }
        }

        [ThriftField( 4, false, "camiproCookie" )]
        public string CamiproCookie { get; set; }
    }
}