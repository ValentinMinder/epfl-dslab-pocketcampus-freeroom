// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Directory.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/directory" )]
    public sealed class MainViewModel : DataViewModel<ViewPersonRequest>
    {
        private readonly IDirectoryService _directoryService;
        private readonly INavigationService _navigationService;

        private bool _isLoadingMoreResults;
        private ObservableCollection<Person> _searchResults;
        private bool _anySearchResults;

        private sbyte[] _currentPaginationToken;
        private string _currentQuery;


        /// <summary>
        /// Gets a value indicating whether more results are being loaded.
        /// </summary>
        public bool IsLoadingMoreResults
        {
            get { return _isLoadingMoreResults; }
            private set { SetProperty( ref _isLoadingMoreResults, value ); }
        }

        /// <summary>
        /// Gets the search results.
        /// </summary>
        public ObservableCollection<Person> SearchResults
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
        /// Gets the auto-complete provider for people names.
        /// </summary>
        public Func<string, Task<IEnumerable<string>>> SearchAutoCompleteProvider
        {
            get { return ProvideSearchSuggestionsAsync; }
        }

        /// <summary>
        /// Gets the command executed to search for people.
        /// </summary>
        [LogId( "Search" )]
        public AsyncCommand<string> SearchCommand
        {
            get { return GetAsyncCommand<string>( SearchAsync ); }
        }

        [LogId( "SearchForMore" )]
        public AsyncCommand SearchForMoreCommand
        {
            get { return GetAsyncCommand( SearchForMoreAsync, () => _currentPaginationToken != null ); }
        }

        /// <summary>
        /// Gets the command executed to view a person's details.
        /// </summary>
        [LogId( "ViewPersion" )]
        public Command<Person> ViewPersonCommand
        {
            get { return GetCommand<Person>( _navigationService.NavigateTo<PersonViewModel, Person> ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( IDirectoryService directoryService, INavigationService navigationService,
                              ViewPersonRequest request )
        {
            _directoryService = directoryService;
            _navigationService = navigationService;
            _anySearchResults = true;

            if ( request.Name != null )
            {
                SearchCommand.ExecuteAsync( request.Name );
            }
            if ( request.Person != null )
            {
                ViewPersonCommand.Execute( request.Person );
            }
        }


        /// <summary>
        /// Asynchronously provides search suggestions for the specified query.
        /// </summary>
        private async Task<IEnumerable<string>> ProvideSearchSuggestionsAsync( string query )
        {
            // no pagination token, it's just search suggestions
            var response = await _directoryService.SearchAsync( new SearchRequest { Query = query } );

            if ( response.Status == SearchStatus.Success )
            {
                return response.Results
                               .Select( p => p.FirstName )
                               .Concat( response.Results.Select( p => p.LastName ) )
                               .Distinct()
                               .OrderBy( s => s );
            }
            return new string[0];
        }

        /// <summary>
        /// Asynchronously searches for people with the specified query.
        /// </summary>
        private Task SearchAsync( string query )
        {
            return TryExecuteAsync( async token =>
            {
                var response = await _directoryService.SearchAsync( new SearchRequest { Query = query } );

                if ( response.Status != SearchStatus.Success )
                {
                    throw new Exception( "An error occurred while searching." );
                }

                if ( !token.IsCancellationRequested )
                {
                    _currentQuery = query;
                    _currentPaginationToken = response.PaginationToken;
                    SearchResults = new ObservableCollection<Person>( response.Results );
                    AnySearchResults = SearchResults.Count > 0;

                    if ( SearchResults.Count == 1 )
                    {
                        ViewPersonCommand.Execute( SearchResults[0] );
                    }
                }
            } );
        }

        /// <summary>
        /// Asynchronously adds more results.
        /// </summary>
        private async Task SearchForMoreAsync()
        {
            var token = CurrentCancellationToken;
            IsLoadingMoreResults = true;

            try
            {
                var request = new SearchRequest
                {
                    Query = _currentQuery,
                    PaginationToken = _currentPaginationToken
                };
                var response = await _directoryService.SearchAsync( request );

                if ( response.Status == SearchStatus.Success && !token.IsCancellationRequested )
                {
                    _currentPaginationToken = response.PaginationToken;

                    foreach ( var result in response.Results )
                    {
                        SearchResults.Add( result );
                    }
                }
            }
            catch
            {
                // Ignore all exceptions since we're paginating; there are results displayed.
            }

            IsLoadingMoreResults = false;
        }
    }
}