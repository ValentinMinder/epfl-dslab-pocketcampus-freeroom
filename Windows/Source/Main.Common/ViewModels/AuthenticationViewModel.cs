// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Main.Models;
using PocketCampus.Main.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// The ViewModel that authenticates the user.
    /// </summary>
    [LogId( "/dashboard/authenticate" )]
    public sealed class AuthenticationViewModel : ViewModel<AuthenticationMode>
    {
        private readonly IAuthenticationService _authenticationService;
        private readonly ITequilaAuthenticator _authenticator;
        private readonly IServerAccess _serverAccess;
        private readonly INavigationService _navigationService;
        private readonly IMainSettings _settings;

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
        /// Gets a value indicating whether the user should be allowed to opt-out of saving credentials.
        /// </summary>
        public bool CanSaveCredentials { get; private set; }

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
        [LogId( "Authenticate" )]
        [LogParameter( "SaveCredentials" )]
        [LogValueConverter( true, "SavePasswordYes" )]
        [LogValueConverter( false, "SavePasswordNo" )]
        public AsyncCommand AuthenticateCommand
        {
            get { return GetAsyncCommand( AuthenticateAsync, () => !IsAuthenticating ); }
        }


        /// <summary>
        /// Creates a new AuthenticationViewModel.
        /// </summary>
        public AuthenticationViewModel( IAuthenticationService authenticationService, ITequilaAuthenticator authenticator,
                                        IServerAccess serverAccess, INavigationService navigationService,
                                        IMainSettings settings,
                                        AuthenticationMode authMode )
        {
            _authenticationService = authenticationService;
            _authenticator = authenticator;
            _serverAccess = serverAccess;
            _navigationService = navigationService;
            _settings = settings;

            SaveCredentials = true;
            CanSaveCredentials = authMode == AuthenticationMode.Dialog;
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
                    var sessionResponse = await _authenticationService.GetSessionAsync( tokenResponse.Token );
                    if ( sessionResponse.Status != AuthenticationStatus.Success )
                    {
                        throw new Exception( "An error occurred while getting a session." );
                    }

                    _settings.ServerSession = _serverAccess.ServerSession = sessionResponse.Session;

                    _settings.IsAuthenticated = SaveCredentials;
                    _settings.UserName = UserName;
                    _settings.Password = Password;
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
                _navigationService.NavigateBack();
            }

            IsAuthenticating = false;
        }
    }
}