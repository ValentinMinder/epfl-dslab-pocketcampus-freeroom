// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;

namespace PocketCampus.Authentication.Services
{
    public sealed class SecureRequestHandler : ISecureRequestHandler
    {
        private readonly IServerSettings _serverSettings;
        private readonly ICredentialsStorage _credentials;
        private readonly IAuthenticator _authenticator;


        public SecureRequestHandler( IServerSettings serverSettings, ICredentialsStorage credentials, IAuthenticator authenticator )
        {
            _serverSettings = serverSettings;
            _credentials = credentials;
            _authenticator = authenticator;
        }


        public async Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
        {
            if ( _serverSettings.Session == null && _credentials.UserName != null )
            {
                _serverSettings.Session = await _authenticator.AuthenticateAsync( _credentials.UserName, _credentials.Password, _serverSettings.SessionStatus == SessionStatus.LoggedIn );
            }

            // Even if the session is null, attempt anyway; that way consumers only have to deal with the "auth error from server" case
            return await attempt();
        }
    }
}