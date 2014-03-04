// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Map.Models;
using PocketCampus.Map.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    /// <summary>
    /// Provides search functionality for the EPFL map.
    /// </summary>
    public sealed class SearchProvider : DataViewModel<NoParameter>
    {
        private readonly IMapService _mapService;

        private MapItem[] _searchResults;
        private bool _anySearchResults;

        /// <summary>
        /// Gets the search results.
        /// </summary>
        public MapItem[] SearchResults
        {
            get { return _searchResults; }
            private set { SetProperty( ref _searchResults, value ); }
        }

        /// <summary>
        /// Gets a value indicating whether there are any search results.
        /// </summary>
        public bool AnySearchResults
        {
            get { return _anySearchResults; }
            private set { SetProperty( ref _anySearchResults, value ); }
        }

        /// <summary>
        /// Gets the provider for the search autocomplete.
        /// </summary>
        public Func<string, Task<IEnumerable<object>>> AutoCompleteProvider
        {
            get { return ProvideSearchSuggestionsAsync; }
        }

        /// <summary>
        /// Gets the command executed to search.
        /// </summary>
        [LogId( "Search" )]
        public AsyncCommand<string> SearchCommand
        {
            get { return GetAsyncCommand<string>( Search ); }
        }


        /// <summary>
        /// Creates a new SearchProvider.
        /// </summary>
        public SearchProvider( IMapService mapService )
        {
            _mapService = mapService;

            AnySearchResults = true;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }


        /// <summary>
        /// Executes the specified search request.
        /// </summary>
        public async void ExecuteRequest( MapSearchRequest request )
        {
            if ( request.Query != null )
            {
                await SearchCommand.ExecuteAsync( request.Query );
            }
            else if ( request.Item != null )
            {
                SearchResults = new[] { request.Item };
                AnySearchResults = true;
            }
        }

        /// <summary>
        /// Provides search suggestions.
        /// </summary>
        private Task<IEnumerable<object>> ProvideSearchSuggestionsAsync( string query )
        {
            return _mapService.SearchAsync( query ).ContinueWith( t => (IEnumerable<object>) t.Result.Select( i => i.Name ) );
        }

        /// <summary>
        /// Searches for rooms, buildings and stuff.
        /// </summary>
        private Task Search( string query )
        {
            return TryExecuteAsync( async token =>
            {
                // Pretend there are results for now, don't show any kind of "no results" message
                // while the search is taking place
                AnySearchResults = true;

                var results = await _mapService.SearchAsync( query );
                var uniqueResult = results.FirstOrDefault( i => i.Name.Equals( query, StringComparison.CurrentCultureIgnoreCase ) );

                if ( !token.IsCancellationRequested )
                {
                    SearchResults = uniqueResult == null ? results : new[] { uniqueResult };
                    AnySearchResults = SearchResults.Length > 0;
                }
            } );
        }
    }
}