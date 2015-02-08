// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Authentication.Models;
using PocketCampus.Authentication.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Authentication.ViewModels
{
    [LogId( "/authentication" )]
    public sealed class MainViewModel : ViewModel<AuthenticationRequest>
    {
        private readonly IAuthenticationService _authenticationService;
        private readonly IAuthenticator _authenticator;
        private readonly INavigationService _navigationService;
        private readonly IServerSettings _settings;
        private readonly ICredentialsStorage _credentials;
        private readonly AuthenticationRequest _request;


        private string _userName;
        private string _password;
        private bool _saveCredentials;
        private AuthenticationStatus _authenticationStatus;


        public string UserName
        {
            get { return _userName; }
            set { SetProperty( ref _userName, value ); }
        }

        public string Password
        {
            get { return _password; }
            set { SetProperty( ref _password, value ); }
        }

        public bool SaveCredentials
        {
            get { return _saveCredentials; }
            set { SetProperty( ref _saveCredentials, value ); }
        }

        public AuthenticationStatus AuthenticationStatus
        {
            get { return _authenticationStatus; }
            private set { SetProperty( ref _authenticationStatus, value ); }
        }


        [LogId( "LogIn" )]
        [LogParameter( "SaveCredentials" )]
        [LogValueConverter( typeof( SavePasswordLogValueConverter ) )]
        public AsyncCommand AuthenticateCommand
        {
            get { return this.GetAsyncCommand( AuthenticateAsync, () => AuthenticationStatus != AuthenticationStatus.Authenticating ); }
        }


        public MainViewModel( IAuthenticationService authenticationService, IAuthenticator authenticator,
                              INavigationService navigationService, IServerSettings settings, ICredentialsStorage credentials,
                              AuthenticationRequest request )
        {
            _authenticationService = authenticationService;
            _authenticator = authenticator;
            _navigationService = navigationService;
            _settings = settings;
            _credentials = credentials;
            _request = request;

            SaveCredentials = true;
        }


        private async Task AuthenticateAsync()
        {
            AuthenticationStatus = AuthenticationStatus.Authenticating;

            try
            {
                var tokenResponse = await _authenticationService.GetTokenAsync();

                if ( tokenResponse.Status != ResponseStatus.Success )
                {
                    throw new Exception( "An error occurred while getting a token." );
                }

                if ( await _authenticator.AuthenticateAsync( UserName, Password, tokenResponse.Token ) )
                {
                    var sessionRequest = new SessionRequest
                    {
                        TequilaToken = tokenResponse.Token,
                        RememberMe = SaveCredentials
                    };

                    var sessionResponse = await _authenticationService.GetSessionAsync( sessionRequest );

                    if ( sessionResponse.Status != ResponseStatus.Success )
                    {
                        throw new Exception( "An error occurred while getting a session." );
                    }

                    _settings.Session = sessionResponse.Session;
                    _settings.SessionStatus = SaveCredentials ? SessionStatus.LoggedIn : SessionStatus.LoggedInTemporarily;
                    _credentials.SetCredentials( UserName, Password );
                    AuthenticationStatus = AuthenticationStatus.Success;

                    if ( _request.SuccessAction == null )
                    {
                        _navigationService.NavigateBack();
                    }
                    else
                    {
                        _navigationService.RemoveCurrentFromBackStack();
                        _request.SuccessAction();
                    }
                }
                else
                {
                    AuthenticationStatus = AuthenticationStatus.WrongCredentials;
                }
            }
            catch
            {
                AuthenticationStatus = AuthenticationStatus.Error;
            }
        }


        // Convert log parameters, for analytics
        private sealed class SavePasswordLogValueConverter : ILogValueConverter
        {
            public string Convert( object value )
            {
                return (bool) value ? "SavePasswordYes" : "SavePasswordNo";
            }
        }
    }
}