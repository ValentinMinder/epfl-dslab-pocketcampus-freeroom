// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Serialization;
using PocketCampus.Authentication;
using PocketCampus.Authentication.Models;
using PocketCampus.Authentication.Services;
using PocketCampus.Authentication.ViewModels;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Handles requests that require two-step authentication.
    /// </summary>
    public sealed class SecureRequestHandler : ISecureRequestHandler
    {
        private readonly IMainSettings _mainSettings;
        private readonly ICredentialsStorage _credentials;
        private readonly INavigationService _navigationService;
        private readonly IAuthenticator _authenticator;
        private readonly IAuthenticationService _authenticationService;


        /// <summary>
        /// Creates a new SecureRequestHandler.
        /// </summary>
        public SecureRequestHandler( IMainSettings mainSettings, ICredentialsStorage credentials, INavigationService navigationService,
                                     IAuthenticator authenticator, IAuthenticationService authenticationService )
        {
            _mainSettings = mainSettings;
            _credentials = credentials;
            _navigationService = navigationService;
            _authenticator = authenticator;
            _authenticationService = authenticationService;
        }


        /// <summary>
        /// Asynchronously executes the specified request.
        /// </summary>
        public async Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
            where T : class
        {
            if ( _mainSettings.Session == null )
            {
                var tokenResponse = await _authenticationService.GetTokenAsync();

                if ( tokenResponse.Status == AuthenticationStatus.Success
                  && await _authenticator.AuthenticateAsync( _credentials.UserName, _credentials.Password, tokenResponse.Token ) )
                {
                    var sessionRequest = new SessionRequest
                    {
                        TequilaToken = tokenResponse.Token,
                        RememberMe = _mainSettings.SessionStatus == SessionStatus.LoggedIn
                    };
                    var sessionResponse = await _authenticationService.GetSessionAsync( sessionRequest );

                    if ( sessionResponse.Status == AuthenticationStatus.Success )
                    {
                        _mainSettings.Session = sessionResponse.Session;
                    }
                }
                else
                {
                    _mainSettings.SessionStatus = SessionStatus.NotLoggedIn;
                    return null;
                }
            }

            return await attempt();
        }

        /// <summary>
        /// Asynchronously executes the specified request, with the specified authenticator, for the specified ViewModel type.
        /// </summary>
        public async Task ExecuteAsync<TViewModel, TToken, TSession>( ITwoStepAuthenticator<TToken, TSession> authenticator, Func<TSession, Task> attempt )
            where TViewModel : ViewModel<NoParameter>
            where TToken : IAuthenticationToken
            where TSession : class
        {
            var session = LoadSession<TSession>( typeof( TViewModel ) );

            if ( session == null )
            {
                var token = await authenticator.GetTokenAsync();
                if ( await _authenticator.AuthenticateAsync( _credentials.UserName, _credentials.Password, token.AuthenticationKey ) )
                {
                    session = await authenticator.GetSessionAsync( token );
                    SaveSession( typeof( TViewModel ), session );
                }
                else
                {
                    _mainSettings.SessionStatus = SessionStatus.NotLoggedIn;
                    return;
                }
            }

            await attempt( session );
        }

        /// <summary>
        /// Requests new credentials from the user.
        /// If authentication is successful, comes back to a new instance of the ViewModel.
        /// </summary>
        public void Authenticate<TViewModel>()
            where TViewModel : ViewModel<NoParameter>
        {
            var authRequest = new AuthenticationRequest( () => _navigationService.NavigateTo<TViewModel>() );
            _navigationService.RemoveCurrentFromBackStack();
            _navigationService.NavigateTo<AuthenticationViewModel, AuthenticationRequest>( authRequest );
        }


        /// <summary>
        /// Loads a session of the specified type for the specified ViewModel type.
        /// </summary>
        private TSession LoadSession<TSession>( Type typeKey )
            where TSession : class
        {
            string key = typeKey.FullName;
            var sessions = _mainSettings.Sessions;
            if ( sessions.ContainsKey( key ) )
            {
                return DeserializeSession<TSession>( sessions[key] );
            }
            return null;
        }

        /// <summary>
        /// Saves the specified session for the specified ViewModel type.
        /// </summary>
        private void SaveSession( Type typeKey, object session )
        {
            string key = typeKey.FullName;
            string value = SerializeSession( session );
            var sessions = _mainSettings.Sessions;
            if ( sessions.ContainsKey( key ) )
            {
                sessions[key] = value;
            }
            else
            {
                sessions.Add( key, value );
            }
            _mainSettings.Sessions = sessions; // force a save, since dictionaries aren't observable
        }


        // HACK: Since objects can't be (de)serialized into "object"s (because of the known types magic),
        //       convert them to strings and then save them.

        /// <summary>
        /// Deserializes an object of the specified type from the specified string.
        /// </summary>
        private static T DeserializeSession<T>( string serialized )
        {
            if ( serialized == "" )
            {
                return default( T );
            }
            using ( var reader = new StringReader( serialized ) )
            {
                return (T) new XmlSerializer( typeof( T ) ).Deserialize( reader );
            }
        }

        /// <summary>
        /// Serializes the specified object into a string.
        /// </summary>
        private static string SerializeSession( object session )
        {
            if ( session == null )
            {
                return "";
            }

            using ( var writer = new StringWriter() )
            {
                // Ensure that the BOM isn't saved - weird, but it works
                var emptyNs = new XmlSerializerNamespaces( new[] { new XmlQualifiedName( "", "" ) } );

                new XmlSerializer( session.GetType() ).Serialize( writer, session, emptyNs );
                return writer.ToString();
            }
        }
    }
}