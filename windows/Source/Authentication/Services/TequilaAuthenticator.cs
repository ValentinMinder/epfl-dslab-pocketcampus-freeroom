// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using PocketCampus.Authentication.Models;
using PocketCampus.Common.Services;

namespace PocketCampus.Authentication.Services
{
    /// <summary>
    /// Authenticates users to Tequila.
    /// </summary>
    public sealed class TequilaAuthenticator : IAuthenticator
    {
        private const string LogInUrl = "https://tequila.epfl.ch/cgi-bin/tequila/requestauth";
        private const string UserNameParameter = "username", PasswordParameter = "password", KeyParameter = "requestkey";

        private static readonly string OAuth2Url = "https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth";
        private static readonly string[] OAuth2Scopes = 
        {
            "Tequila.profile", "Moodle.read", "ISA.read", "Camipro.read", "Camipro.write"
        };
        private static readonly Dictionary<string, string> TokenRequestParameters = new Dictionary<string, string>
        {
            { "response_type", "code" },
            { "redirect_uri", "https://pocketcampus.epfl.ch/" },
            { "client_id", "1b74e3837e50e21afaf2005f@epfl.ch" },
            { "scope",  string.Join( ",", OAuth2Scopes ) }
        };
        private static readonly Dictionary<string, string> CodeRequestParameters = new Dictionary<string, string>( TokenRequestParameters )
        {
            { "doauth", "Approve" }
        };

        private const string TequilaRequestKeyParameter = "requestkey";
        private const string TequilaCodeParameter = "code";

        private readonly IHttpClient _client;
        private readonly IAuthenticationService _authenticationService;

        public TequilaAuthenticator( IHttpClient client, IAuthenticationService authenticationService )
        {
            _client = client;
            _authenticationService = authenticationService;
        }

        /// <summary>
        /// Authenticates with the specified credentials, and returns a session or null if the authentication failed.
        /// </summary>
        public async Task<string> AuthenticateAsync( string userName, string password, bool rememberMe )
        {
            var keyResponse = await _client.GetAsync( OAuth2Url, TokenRequestParameters );
            string requestKey = GetUrlParameterValue( keyResponse.RequestUrl, TequilaRequestKeyParameter );

            if ( await AuthenticateAsync( userName, password, requestKey ) )
            {
                var codeResponse = await _client.GetAsync( OAuth2Url, CodeRequestParameters );
                string code = GetUrlParameterValue( codeResponse.RequestUrl, TequilaCodeParameter );

                var sessionRequest = new SessionRequest
                {
                    TequilaToken = code,
                    RememberMe = rememberMe
                };
                var sessionResponse = await _authenticationService.GetSessionAsync( sessionRequest );

                if ( sessionResponse.Status == ResponseStatus.Success )
                {
                    return sessionResponse.Session;
                }
            }
            return null;
        }

        private async Task<bool> AuthenticateAsync( string userName, string password, string key )
        {
            var authParams = new Dictionary<string, string>
            {
                { UserNameParameter, userName },
                { PasswordParameter, password },
                { KeyParameter, key }
            };

            var response = await _client.PostAsync( LogInUrl, authParams );

            // If we're still on Tequila, the credentials are invalid
            return !response.RequestUrl.Contains( LogInUrl );
        }

        private static string GetUrlParameterValue( string url, string parameterKey )
        {
            // HACK: This is awful
            return WebUtility.UrlDecode( url.Split( '?' )[1].Split( '&' ).Select( s => s.Split( '=' ) ).Single( s => s[0] == parameterKey )[1] );
        }
    }
}