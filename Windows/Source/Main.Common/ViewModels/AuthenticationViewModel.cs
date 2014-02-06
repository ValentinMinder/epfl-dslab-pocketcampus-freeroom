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
    [PageLogId( "/dashboard/authenticate" )]
    public sealed class AuthenticationViewModel : ViewModel<NoParameter>
    {
        private readonly INavigationService _navigationService;
        private readonly ITequilaAuthenticator _authenticator;
        private readonly IMainSettings _settings;

        private string _userName;
        private string _password;
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
        [CommandLogId( "Authenticate" )]
        public AsyncCommand AuthenticateCommand
        {
            get { return GetAsyncCommand( AuthenticateAsync, () => !IsAuthenticating ); }
        }


        /// <summary>
        /// Creates a new AuthenticationViewModel.
        /// </summary>
        public AuthenticationViewModel( INavigationService navigationService, ITequilaAuthenticator authenticator, IMainSettings settings )
        {
            _navigationService = navigationService;
            _authenticator = authenticator;
            _settings = settings;
        }


        /// <summary>
        /// Asynchronously attempts to authenticate the user.
        /// </summary>
        private async Task AuthenticateAsync()
        {
            IsAuthenticating = true;

            try
            {
                if ( await _authenticator.AuthenticateAsync( UserName, Password ) )
                {
                    _settings.IsAuthenticated = true;
                    _settings.UserName = UserName;
                    _settings.Password = Password;
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

            if ( _settings.IsAuthenticated )
            {
                _navigationService.NavigateBack();
            }

            IsAuthenticating = false;
        }
    }
}