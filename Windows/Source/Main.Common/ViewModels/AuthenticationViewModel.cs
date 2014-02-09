// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
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
        private readonly INavigationService _navigationService;
        private readonly ITequilaAuthenticator _authenticator;
        private readonly IMainSettings _settings;

        private string _userName;
        private string _password;
        private bool _saveCredentials;
        private bool _isAuthenticating;
        private AuthenticationStatus _status;

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
        /// Gets the authentication status.
        /// </summary>
        public AuthenticationStatus Status
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
        public AuthenticationViewModel( INavigationService navigationService, ITequilaAuthenticator authenticator, IMainSettings settings,
                                        AuthenticationMode authMode )
        {
            _navigationService = navigationService;
            _authenticator = authenticator;
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
                if ( await _authenticator.AuthenticateAsync( UserName, Password ) )
                {
                    _settings.IsAuthenticated = SaveCredentials;
                    _settings.UserName = UserName;
                    _settings.Password = Password;
                    authOk = true;
                }
                else
                {
                    Status = AuthenticationStatus.WrongCredentials;
                }
            }
            catch
            {
                Status = AuthenticationStatus.Error;
            }

            if ( authOk )
            {
                _navigationService.NavigateBack();
            }

            IsAuthenticating = false;
        }
    }
}