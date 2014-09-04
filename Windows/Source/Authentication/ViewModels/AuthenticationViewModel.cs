// Copyright (c) PocketCampus.Org 2014
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
    /// <summary>
    /// The ViewModel that authenticates the user.
    /// </summary>
    [LogId( "/authentication" )]
    public sealed class AuthenticationViewModel : ViewModel<AuthenticationRequest>
    {
        private readonly IAuthenticationService _authenticationService;
        private readonly IAuthenticator _authenticator;
        private readonly IServerAccess _serverAccess;
        private readonly INavigationService _navigationService;
        private readonly IServerSettings _settings;
        private readonly ICredentialsStorage _credentials;
        private readonly AuthenticationRequest _request;

        private string _userName;
        private string _password;
        private bool _saveCredentials;
        private bool _isAuthenticating;
        private AuthenticationAttemptStatus _status;

        /// <summary>
        /// Gets or sets the user name (GASPAR identifier or SCIPER number).
        /// </summary>
        public string UserName
        {
            get { return _userName; }
            set { SetProperty( ref _userName, value ); }
        }

        /// <summary>
        /// Gets or sets the password.
        /// </summary>
        public string Password
        {
            get { return _password; }
            set { SetProperty( ref _password, value ); }
        }

        /// <summary>
        /// Gets or sets a value indicating whether credentials should be saved.
        /// </summary>
        public bool SaveCredentials
        {
            get { return _saveCredentials; }
            set { SetProperty( ref _saveCredentials, value ); }
        }

        /// <summary>
        /// Gets a value indicating whether authentication is in progress.
        /// </summary>
        public bool IsAuthenticating
        {
            get { return _isAuthenticating; }
            private set { SetProperty( ref _isAuthenticating, value ); }
        }

        /// <summary>
        /// Gets the authentication attempt status.
        /// </summary>
        public AuthenticationAttemptStatus Status
        {
            get { return _status; }
            private set { SetProperty( ref _status, value ); }
        }

        /// <summary>
        /// Gets the command executed to attempt to authenticate the user.
        /// </summary>
        [LogId( "LogIn" )]
        [LogParameter( "SaveCredentials" )]
        [LogValueConverter( typeof( SavePasswordLogValueConverter ) )]
        public AsyncCommand AuthenticateCommand
        {
            get { return this.GetAsyncCommand( AuthenticateAsync, () => !IsAuthenticating ); }
        }


        /// <summary>
        /// Creates a new AuthenticationViewModel.
        /// </summary>
        public AuthenticationViewModel( IAuthenticationService authenticationService, IAuthenticator authenticator,
                                        IServerAccess serverAccess, INavigationService navigationService,
                                        IServerSettings settings, ICredentialsStorage credentials,
                                        AuthenticationRequest request )
        {
            _authenticationService = authenticationService;
            _authenticator = authenticator;
            _serverAccess = serverAccess;
            _navigationService = navigationService;
            _settings = settings;
            _credentials = credentials;
            _request = request;

            SaveCredentials = true;
        }


        /// <summary>
        /// Asynchronously attempts to authenticate the user.
        /// </summary>
        private async Task AuthenticateAsync()
        {
            bool authOk = false;
            IsAuthenticating = true;

            try
            {
                var tokenResponse = await _authenticationService.GetTokenAsync();
                if ( tokenResponse.Status != AuthenticationStatus.Success )
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
                    if ( sessionResponse.Status != AuthenticationStatus.Success )
                    {
                        throw new Exception( "An error occurred while getting a session." );
                    }

                    _settings.Session = sessionResponse.Session;

                    _settings.SessionStatus = SaveCredentials ? SessionStatus.LoggedIn : SessionStatus.LoggedInTemporarily;
                    _credentials.UserName = UserName;
                    _credentials.Password = Password;
                    authOk = true;
                }
                else
                {
                    Status = AuthenticationAttemptStatus.WrongCredentials;
                }
            }
            catch
            {
                Status = AuthenticationAttemptStatus.Error;
            }

            if ( authOk )
            {
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

            IsAuthenticating = false;
        }


        /// <summary>
        /// Utility class for analytics.
        /// </summary>
        private sealed class SavePasswordLogValueConverter : ILogValueConverter
        {
            public string Convert( object value )
            {
                return (bool) value ? "SavePasswordYes" : "SavePasswordNo";
            }
        }
    }
}