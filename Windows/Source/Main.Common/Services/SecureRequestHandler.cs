// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Serialization;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Handles requests that require two-step authentication.
    /// </summary>
    public sealed class SecureRequestHandler : ISecureRequestHandler
    {
        private readonly IMainSettings _mainSettings;
        private readonly INavigationService _navigationService;
        private readonly ITequilaAuthenticator _authenticator;

        private bool _isRetrying;


        /// <summary>
        /// Creates a new SecureRequestHandler.
        /// </summary>
        public SecureRequestHandler( IMainSettings mainSettings, INavigationService navigationService, ITequilaAuthenticator authenticator )
        {
            _mainSettings = mainSettings;
            _navigationService = navigationService;
            _authenticator = authenticator;
        }


        /// <summary>
        /// Asynchronously executes the specified request, with the specified authenticator, for the specified ViewModel type.
        /// The request asynchronously returns a boolean indicating whether the authentication succeeded.
        /// </summary>
        public async Task ExecuteAsync<TViewModel, TToken, TSession>( ITwoStepAuthenticator<TToken, TSession> authenticator, Func<TSession, Task<bool>> attempt )
            where TViewModel : IViewModel<NoParameter>
            where TToken : IAuthenticationToken
            where TSession : class
        {
            var session = LoadSession<TSession>( typeof( TViewModel ) );

            if ( session == null )
            {
                var token = await authenticator.GetTokenAsync();
                if ( await _authenticator.AuthenticateAsync( _mainSettings.UserName, _mainSettings.Password, token.AuthenticationKey ) )
                {
                    session = await authenticator.GetSessionAsync( token );
                    SaveSession( typeof( TViewModel ), session );
                }
                else
                {
                    // Authenticate, and then go to this plugin if it succeeds
                    // but go back to whatever was the previous plugin rather than to this one if it doesn't
                    _navigationService.PopBackStack();
                    _navigationService.NavigateToDialog<AuthenticationViewModel>();
                    _navigationService.NavigateTo<TViewModel>();
                    return;
                }
            }

            if ( !( await attempt( session ) ) )
            {
                if ( _isRetrying )
                {
                    throw new Exception( "The refresh failed twice in a row." );
                }

                _isRetrying = true;
                SaveSession( typeof( TViewModel ), (TSession) null );

                try
                {
                    await ExecuteAsync<TViewModel, TToken, TSession>( authenticator, attempt );
                }
                finally
                {
                    _isRetrying = false;
                }
            }
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
            return (T) new XmlSerializer( typeof( T ) ).Deserialize( new StringReader( serialized ) );
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

            var writer = new StringWriter();
            // Ensure that the BOM isn't saved - weird, but it works
            var emptyNs = new XmlSerializerNamespaces( new[]
            {
                new XmlQualifiedName( "", "" ),
            } );
            new XmlSerializer( session.GetType() ).Serialize( writer, session, emptyNs );
            return writer.ToString();
        }
    }
}