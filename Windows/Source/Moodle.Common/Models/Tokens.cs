// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThriftSharp;

// Plumbing for two-step authentication

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "TequilaToken" )]
    public sealed class TequilaToken : IAuthenticationToken
    {
        [ThriftField( 1, true, "iTequilaKey" )]
        public string AuthenticationKey { get; set; }

        [ThriftField( 2, false, "loginCookie" )]
        public string Cookie { get; set; }
    }

    [ThriftStruct( "SessionId" )]
    public sealed class SessionId
    {
        [ThriftField( 1, true, "tos" )]
        public int Intent
        {
            get { return 0; }
            set { }
        }

        [ThriftField( 3, false, "moodleCookie" )]
        public string Cookie { get; set; }
    }

    [ThriftStruct( "MoodleRequest" )]
    public sealed class MoodleRequest
    {
        [ThriftField( 1, true, "iSessionId" )]
        public SessionId SessionId { get; set; }

        [ThriftField( 2, true, "iLanguage" )]
        public string Language { get; set; }

        [ThriftField( 3, false, "iCourseId" )]
        public int CourseId { get; set; }
    }

    [ThriftStruct( "MoodleSession" )]
    public sealed class MoodleSession
    {
        [ThriftField( 1, true, "moodleCookie" )]
        public string Cookie { get; set; }
    }
}