// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Map.Models;
using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    /// <summary>
    /// Provides search functionality for the EPFL map.
    /// </summary>
    public sealed class SearchProvider : DataViewModel<NoParameter>
    {
        private const string IgnoredNamePrefix = "Auditoire ";

        private readonly IMapService _mapService;

        private MapItem[] _searchResults;

        /// <summary>
        /// Gets the search results.
        /// </summary>
        public MapItem[] SearchResults
        {
            get { return _searchResults; }
            private set { SetProperty( ref _searchResults, value ); }
        }

        /// <summary>
        /// Gets the command executed to search.
        /// </summary>
        [LogId( "Search" )]
        [LogParameter( "$Param" )]
        public AsyncCommand<string> SearchCommand
        {
            get { return this.GetAsyncCommand<string>( Search ); }
        }


        /// <summary>
        /// Creates a new SearchProvider.
        /// </summary>
        public SearchProvider( IMapService mapService )
        {
            _mapService = mapService;
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
            }
        }

        /// <summary>
        /// Searches for rooms, buildings and stuff.
        /// </summary>
        private Task Search( string query )
        {
            return TryExecuteAsync( async token =>
            {
                var results = await _mapService.SearchAsync( query, token );
                var uniqueResult = results.FirstOrDefault( r => AreNamesEqual( r.Name, query ) );

                if ( !token.IsCancellationRequested )
                {
                    SearchResults = uniqueResult == null ? results : new[] { uniqueResult };
                }
            } );
        }

        /// <summary>
        /// Indicates whether the two specified names are considered to be equal.
        /// </summary>
        private static bool AreNamesEqual( string name1, string name2 )
        {
            return Enumerable.SequenceEqual( NormalizeName( name1 ), NormalizeName( name2 ) );
        }

        /// <summary>
        /// Normalizes the specified name.
        /// </summary>
        private static IEnumerable<char> NormalizeName( string name )
        {
            return name.Replace( IgnoredNamePrefix, "" )
                       .ToUpperInvariant()
                       .ToCharArray()
                       .Where( c => !char.IsWhiteSpace( c ) );
        }
    }
}