// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;
using PocketCampus.Transport.Services;

namespace PocketCampus.Transport.ViewModels
{
    /// <summary>
    /// ViewModel that allows the user to add a station.
    /// </summary>
    [LogId( "/transport/addstation" )]
    public sealed class AddStationViewModel : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _pluginSettings;

        /// <summary>
        /// Gets the auto-complete provider for station names.
        /// </summary>
        public Func<string, Task<IEnumerable<object>>> AutoCompleteProvider
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
        private async Task<IEnumerable<object>> ProvideAutoComplete( string query )
        {
            var suggestions = await _transportService.GetSuggestionsAsync( query );
            return suggestions.Select( s => s.Name );
        }

        /// <summary>
        /// Adds the specified station to the settings, and navigates back.
        /// </summary>
        private Task AddAsync( string stationName )
        {
            return TryExecuteAsync( async _ =>
            {
                var station = ( await _transportService.GetStationsAsync( new[] { stationName } ) )[0];
                _pluginSettings.Stations.Add( station );
                _navigationService.NavigateBack();
            } );
        }
    }
}