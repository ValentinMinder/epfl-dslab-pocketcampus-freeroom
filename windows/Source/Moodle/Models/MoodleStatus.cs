// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftEnum]
    public enum MoodleStatus
    {
        Success = 200,
        AuthenticationError = 403,
        NetworkError = 404
    }
}