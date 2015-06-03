// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    [ThriftEnum]
    public enum ResponseStatus
    {
        Success = 200,
        NetworkError = 404,
        AuthenticationError = 407
    }
}