// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Transport.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Transport.ViewModels
{
    /// <summary>
    /// The settings ViewModel.
    /// </summary>
    [LogId( "/transport/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        private readonly IPluginSettings _settings;

        private bool _sortByPosition;

        /// <summary>
        /// Whether the stop groups should be sorted by position.
        /// This will enable GPS tracking.
        /// </summary>
        public bool SortByPosition
        {
            get { return _sortByPosition; }
            set { SetProperty( ref _sortByPosition, value ); }
        }


        /// <summary>
        /// Creates a new SettingsViewModel.
        /// </summary>
        public SettingsViewModel( IPluginSettings settings )
        {
            _settings = settings;
            SortByPosition = _settings.SortByPosition;
        }


        /// <summary>
        /// Executed when the user leaves this page, via the "back" button.
        /// </summary>
        public override void OnNavigatedFrom()
        {
            _settings.SortByPosition = SortByPosition;
        }
    }
}