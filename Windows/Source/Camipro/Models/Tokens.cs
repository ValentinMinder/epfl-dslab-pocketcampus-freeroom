// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using PocketCampus.Common;
using ThriftSharp;

// The tokens used for authentication. Magic.

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "TequilaToken" )]
    public sealed class TequilaToken : IAuthenticationToken
    {
        [ThriftField( 1, true, "iTequilaKey" )]
        public string AuthenticationKey { get; set; }

        [ThriftField( 2, false, "loginCookie" )]
        public string LoginCookie { get; set; }
    }

    [ThriftStruct( "SessionId" )]
    public sealed class SessionId
    {
        [ThriftField( 1, true, "tos" )]
        public int Intention
        {
            get { return 0; }
            set { }
        }

        [ThriftField( 4, false, "camiproCookie" )]
        public string CamiproCookie { get; set; }
    }

    [ThriftStruct( "CamiproRequest" )]
    public sealed class CamiproRequest
    {
        [ThriftField( 1, true, "iSessionId" )]
        public SessionId Session { get; set; }

        [ThriftField( 2, true, "iLanguage" )]
        public string Language { get; set; }
    }

    [ThriftStruct( "CamiproSession" )]
    [DataContract]
    public sealed class CamiproSession
    {
        [ThriftField( 1, true, "camiproCookie" )]
        [DataMember]
        public string Cookie { get; set; }
    }
}