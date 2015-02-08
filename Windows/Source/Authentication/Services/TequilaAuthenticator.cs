// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Threading.Tasks;
using PocketCampus.Common.Services;

namespace PocketCampus.Authentication.Services
{
    /// <summary>
    /// Authenticates users to Tequila.
    /// </summary>
    public class TequilaAuthenticator : IAuthenticator
    {
        private const string TequilaLogInUrl = "https://tequila.epfl.ch/cgi-bin/tequila/login";
        private const string TequilaServiceLogInUrl = "https://tequila.epfl.ch/cgi-bin/tequila/requestauth";
        private const string TequilaLogOffUrl = "https://tequila.epfl.ch/cgi-bin/tequila/LogOff";
        private const string UserNameParameter = "username", PasswordParameter = "password", KeyParameter = "requestkey";
        // HACK: If this token is present in Tequila's response to a keyless query, the credentials are valid.
        private const string ValidCredentialsToken = "You are connected as user";

        private readonly IHttpClient _client;


        public TequilaAuthenticator( IHttpClient client )
        {
            _client = client;
        }


        /// <summary>
        /// Asynchronously attempts to authenticate using the specified user name and password.
        /// </summary>
        public async Task<bool> AuthenticateAsync( string userName, string password )
        {
            var authParams = new Dictionary<string, string>
            {
                { UserNameParameter, userName },
                { PasswordParameter, password }
            };

            var response = await _client.PostAsync( TequilaLogInUrl, authParams );

            // Since Tequila always returns HTTP 200 (OK) we need a marker whose presence can be checked
            return response.Content.Contains( ValidCredentialsToken );
        }

        /// <summary>
        /// Asynchronously attempts to authenticate using the specified user name and password, for the service specified by a key.
        /// </summary>
        public async Task<bool> AuthenticateAsync( string userName, string password, string serviceKey )
        {
            var authParams = new Dictionary<string, string>
            {
                { UserNameParameter, userName },
                { PasswordParameter, password },
                { KeyParameter, serviceKey }
            };

            var response = await _client.PostAsync( TequilaServiceLogInUrl, authParams );

            // If we're still on Tequila, the credentials are invalid
            return !response.RequestUrl.Contains( TequilaServiceLogInUrl );
        }

        /// <summary>
        /// Asynchronously logs off from Tequila.
        /// </summary>
        public Task LogOffAsync()
        {
            return _client.GetAsync( TequilaLogOffUrl );
        }
    }
}