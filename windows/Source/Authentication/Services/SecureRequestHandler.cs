// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using PocketCampus.Authentication.Models;
using PocketCampus.Authentication.ViewModels;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;

namespace PocketCampus.Authentication.Services
{
    public sealed class SecureRequestHandler : ISecureRequestHandler
    {
        private static readonly string[] OAuth2Scopes = 
        {
            "Tequila.profile", "Moodle.read", "ISA.read", "Camipro.read", "Camipro.write"
        };
        private static readonly string TokenRequestUrl =
            "https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth?response_type=code&redirect_uri=https%3A%2F%2Fpocketcampus.epfl.ch%2F&client_id=1b74e3837e50e21afaf2005f%40epfl.ch&scope=" + string.Join( ",", OAuth2Scopes );
        private static readonly string CodeRequestUrl =
            TokenRequestUrl + "&doauth=Approve"; // Tequila stuff...
        private const string TequilaRequestKeyParameter = "requestkey";
        private const string TequilaCodeParameter = "code";


        private readonly IServerSettings _serverSettings;
        private readonly ICredentialsStorage _credentials;
        private readonly INavigationService _navigationService;
        private readonly IAuthenticator _authenticator;
        private readonly IAuthenticationService _authenticationService;


        public SecureRequestHandler( IServerSettings serverSettings, ICredentialsStorage credentials, INavigationService navigationService,
                                     IAuthenticator authenticator, IAuthenticationService authenticationService )
        {
            _serverSettings = serverSettings;
            _credentials = credentials;
            _navigationService = navigationService;
            _authenticator = authenticator;
            _authenticationService = authenticationService;
        }

        // New OAuth2 auth
        public async Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
        {
            if ( _serverSettings.Session == null )
            {
                var client = new HttpClient();
                var keyResponse = await client.GetAsync( TokenRequestUrl );
                keyResponse.EnsureSuccessStatusCode();
                string requestKey = GetUrlParameterValue( keyResponse.RequestMessage.RequestUri, TequilaRequestKeyParameter );

                if ( await _authenticator.AuthenticateAsync( _credentials.UserName, _credentials.Password, requestKey ) )
                {
                    var codeResponse = await client.GetAsync( CodeRequestUrl );
                    codeResponse.EnsureSuccessStatusCode();
                    string code = GetUrlParameterValue( codeResponse.RequestMessage.RequestUri, TequilaCodeParameter );

                    var sessionRequest = new SessionRequest
                    {
                        TequilaToken = code,
                        RememberMe = _serverSettings.SessionStatus == SessionStatus.LoggedIn
                    };
                    var sessionResponse = await _authenticationService.GetSessionAsync( sessionRequest );

                    if ( sessionResponse.Status == ResponseStatus.Success )
                    {
                        _serverSettings.Session = sessionResponse.Session;
                    }
                }
                else
                {
                    _serverSettings.SessionStatus = SessionStatus.NotLoggedIn;
                    return default( T );
                }
            }

            return await attempt();
        }

        public void Authenticate<TViewModel>()
            where TViewModel : ViewModel<NoParameter>
        {
            // Destroy the session, since this is called when credentials have been declared invalid
            _serverSettings.Session = null;

            var authRequest = new AuthenticationRequest( () => _navigationService.NavigateTo<TViewModel>() );
            _navigationService.RemoveCurrentFromBackStack();
            _navigationService.NavigateTo<MainViewModel, AuthenticationRequest>( authRequest );
        }

        private static string GetUrlParameterValue( Uri url, string parameterKey )
        {
            return WebUtility.UrlDecode( url.Query.Split( '&' ).Select( s => s.Split( '=' ) ).Single( s => s[0] == parameterKey )[1] );
        }
    }
}