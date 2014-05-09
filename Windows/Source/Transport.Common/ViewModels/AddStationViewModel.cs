// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Transport.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Transport.ViewModels
{
    /// <summary>
    /// ViewModel that allows the user to add a station.
    /// </summary>
    [LogId( "/transport/addStation" )]
    public sealed class AddStationViewModel : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _pluginSettings;

        /// <summary>
        /// Gets the auto-complete provider for station names.
        /// </summary>
        public Func<string, Task<IEnumerable<string>>> AutoCompleteProvider
        {
            get { return ProvideAutoComplete; }
        }

        /// <summary>
        /// Gets the command executed to add a station.
        /// </summary>
        [LogId( "Add" )]
        public AsyncCommand<string> AddCommand
        {
            get { return GetAsyncCommand<string>( AddAsync ); }
        }


        /// <summary>
        /// Creates a new AddStationViewModel.
        /// </summary>
        public AddStationViewModel( ITransportService transportService, INavigationService navigationService, IPluginSettings pluginSettings )
        {
            _transportService = transportService;
            _navigationService = navigationService;
            _pluginSettings = pluginSettings;
        }


        /// <summary>
        /// Provides auto-complete for station names.
        /// </summary>
        private async Task<IEnumerable<string>> ProvideAutoComplete( string query )
        {
            var suggestions = await _transportService.GetSuggestionsAsync( query, CurrentCancellationToken );
            return suggestions.Select( s => s.Name );
        }

        /// <summary>
        /// Adds the specified station to the settings, and navigates back.
        /// </summary>
        private async Task AddAsync( string stationName )
        {
            if ( _pluginSettings.Stations.Any( s => s.Name == stationName ) )
            {
                _navigationService.NavigateBack();
            }

            await TryExecuteAsync( async token =>
            {
                var station = ( await _transportService.GetStationsAsync( new[] { stationName }, token ) )[0];
                _pluginSettings.Stations.Add( station );
                _navigationService.NavigateBack();
            } );
        }
    }
}