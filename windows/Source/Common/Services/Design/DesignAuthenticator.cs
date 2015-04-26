// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ITequilaAuthenticator

#if DEBUG
using System.Threading.Tasks;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignAuthenticator : IAuthenticator
    {
        public Task<bool> AuthenticateAsync( string userName, string password )
        {
            return Task.FromResult( true );
        }

        public Task<bool> AuthenticateAsync( string userName, string password, string serviceKey )
        {
            return Task.FromResult( true );
        }

        public Task LogOffAsync()
        {
            return Task.FromResult( 0 );
        }
    }
}
#endif