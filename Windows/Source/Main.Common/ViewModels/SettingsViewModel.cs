// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// The settings ViewModel.
    /// </summary>
    [PageLogId( "/dashboard/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        private readonly ITequilaAuthenticator _authenticator;
        private readonly INavigationService _navigationService;

        /// <summary>
        /// Gets the settings.
        /// </summary>
        public IMainSettings Settings { get; private set; }

        /// <summary>
        /// Gets the command executed to log on.
        /// </summary>
        [CommandLogId( "LogOn" )]
        public Command LogOnCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<AuthenticationViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to log off.
        /// </summary>
        [CommandLogId( "LogOff" )]
        public AsyncCommand LogOffCommand
        {
            get { return GetAsyncCommand( ExecuteLogOffCommand ); }
        }


        /// <summary>
        /// Creates a new SettingsViewModel.
        /// </summary>
        public SettingsViewModel( IMainSettings settings, ITequilaAuthenticator authenticator, INavigationService navigationService )
        {
            Settings = settings;
            _authenticator = authenticator;
            _navigationService = navigationService;
        }


        /// <summary>
        /// Logs off.
        /// </summary>
        private async Task ExecuteLogOffCommand()
        {
            Settings.IsAuthenticated = false;
            Settings.UserName = null;
            Settings.Password = null;
            Settings.Sessions = new Dictionary<string, string>();
            await _authenticator.LogOffAsync();
        }
    }
}