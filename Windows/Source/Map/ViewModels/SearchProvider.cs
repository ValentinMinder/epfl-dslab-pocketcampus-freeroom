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
    public sealed class SearchProvider : DataViewModel<NoParameter>
    {
        private const string IgnoredNamePrefix = "Auditoire ";

        private readonly IMapService _mapService;

        private MapItem[] _searchResults;


        public MapItem[] SearchResults
        {
            get { return _searchResults; }
            private set { SetProperty( ref _searchResults, value ); }
        }


        [LogId( "Search" )]
        [LogParameter( "$Param" )]
        public AsyncCommand<string> SearchCommand
        {
            get { return this.GetAsyncCommand<string>( Search ); }
        }


        public SearchProvider( IMapService mapService )
        {
            _mapService = mapService;
            Messenger.Send( new CommandLoggingRequest( this ) );
        }


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

        private static bool AreNamesEqual( string name1, string name2 )
        {
            return NormalizeName( name1 ).SequenceEqual( NormalizeName( name2 ) );
        }

        private static IEnumerable<char> NormalizeName( string name )
        {
            return name.Replace( IgnoredNamePrefix, "" )
                       .ToUpperInvariant()
                       .ToCharArray()
                       .Where( c => !char.IsWhiteSpace( c ) );
        }
    }
}