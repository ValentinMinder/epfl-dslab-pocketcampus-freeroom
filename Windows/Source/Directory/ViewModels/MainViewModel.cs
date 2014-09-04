// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Directory.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/directory" )]
    public sealed class MainViewModel : DataViewModel<ViewPersonRequest>
    {
        private const int MinimumQueryLengthForRefresh = 3;

        private readonly IDirectoryService _directoryService;
        private readonly INavigationService _navigationService;
        private readonly ViewPersonRequest _request;

        private string _query;
        private bool _isLoadingMoreResults;
        private ObservableCollection<Person> _searchResults;
        private bool _anySearchResults;

        private sbyte[] _currentPaginationToken;


        /// <summary>
        /// Gets or sets the current query.
        /// </summary>
        public string Query
        {
            get { return _query; }
            set { SetProperty( ref _query, value ); }
        }

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
        /// Gets the command executed to search for people.
        /// </summary>
        [LogId( "Search" )]
        public AsyncCommand SearchCommand
        {
            get { return this.GetAsyncCommand( () => SearchAsync( Query, true ) ); }
        }

        [LogId( "SearchForMore" )]
        public AsyncCommand SearchForMoreCommand
        {
            get { return this.GetAsyncCommand( SearchForMoreAsync, () => _currentPaginationToken != null ); }
        }

        /// <summary>
        /// Gets the command executed to view a person's details.
        /// </summary>
        [LogId( "ViewPersion" )]
        [LogParameter( "$Param.FullName" )]
        public Command<Person> ViewPersonCommand
        {
            get { return this.GetCommand<Person>( _navigationService.NavigateTo<PersonViewModel, Person> ); }
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
            _request = request;

            this.ListenToProperty( x => x.Query, OnQueryChanged );
        }


        public override async Task OnNavigatedToAsync()
        {
            if ( _request.Name != null )
            {
                await SearchAsync( _request.Name, true );
                if ( _searchResults.Count == 1 )
                {
                    _navigationService.RemoveCurrentFromBackStack();
                }
            }

            await base.OnNavigatedToAsync();
        }

        /// <summary>
        /// Asynchronously searches for people with the specified query.
        /// </summary>
        private Task SearchAsync( string query, bool navigateToSingleResult )
        {
            return TryExecuteAsync( async token =>
            {
                var request = new SearchRequest
                {
                    Query = query,
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _directoryService.SearchAsync( request, token );

                if ( response.Status != SearchStatus.Success )
                {
                    throw new Exception( "An error occurred while searching." );
                }

                if ( !token.IsCancellationRequested )
                {
                    _currentPaginationToken = response.PaginationToken;
                    SearchResults = new ObservableCollection<Person>( response.Results );
                    AnySearchResults = SearchResults.Count > 0;

                    if ( navigateToSingleResult && SearchResults.Count == 1 )
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
            if ( _currentPaginationToken == null )
            {
                return;
            }

            var token = CurrentCancellationToken;
            IsLoadingMoreResults = true;

            try
            {
                var request = new SearchRequest
                {
                    Query = this.Query,
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    PaginationToken = _currentPaginationToken
                };
                var response = await _directoryService.SearchAsync( request, CurrentCancellationToken );

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
                _currentPaginationToken = null;
            }

            IsLoadingMoreResults = false;
        }

        private async void OnQueryChanged()
        {
            if ( string.IsNullOrWhiteSpace( Query ) )
            {
                SearchResults.Clear();
                AnySearchResults = true;
                return;
            }

            await TryExecuteAsync( async _ =>
            {
                var request = new SearchRequest
                {
                    Query = Query,
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _directoryService.SearchAsync( request, CancellationToken.None );

                if ( response.Status != SearchStatus.Success )
                {
                    throw new Exception( "An error occurred while searching." );
                }

                _currentPaginationToken = null;
                SearchResults = new ObservableCollection<Person>( response.Results );
                AnySearchResults = SearchResults.Count > 0;
            } );
        }
    }
}