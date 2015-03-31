// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
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
                var session = await _authenticator.AuthenticateAsync( UserName, Password, SaveCredentials );
                if ( session == null )
                {
                    AuthenticationStatus = AuthenticationStatus.WrongCredentials;
                }
                else
                {
                    _settings.Session = session;
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