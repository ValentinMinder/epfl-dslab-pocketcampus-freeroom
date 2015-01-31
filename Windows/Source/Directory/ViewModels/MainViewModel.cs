// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Threading.Tasks;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Directory.ViewModels
{
    [LogId( "/directory" )]
    public sealed class MainViewModel : DataViewModel<ViewPersonRequest>
    {
        private readonly IDirectoryService _directoryService;
        private readonly INavigationService _navigationService;
        private readonly ViewPersonRequest _request;

        private string _query;
        private bool _isLoadingMoreResults;
        private ObservableCollection<Person> _searchResults;

        private sbyte[] _currentPaginationToken;
        private string _lastQuery;


        public string Query
        {
            get { return _query; }
            set { SetProperty( ref _query, value ); }
        }

        public bool IsLoadingMoreResults
        {
            get { return _isLoadingMoreResults; }
            private set { SetProperty( ref _isLoadingMoreResults, value ); }
        }

        public ObservableCollection<Person> SearchResults
        {
            get { return _searchResults; }
            private set { SetProperty( ref _searchResults, value ); }
        }


        [LogId( "Search" )]
        public AsyncCommand SearchCommand
        {
            get
            {
                return this.GetAsyncCommand( async () =>
                {
                    await SearchAsync( Query );
                    if ( SearchResults != null && SearchResults.Count == 1 )
                    {
                        _navigationService.NavigateTo<PersonViewModel, Person>( SearchResults[0] );
                    }
                } );
            }
        }

        [LogId( "SearchForMore" )]
        public AsyncCommand SearchForMoreCommand
        {
            get { return this.GetAsyncCommand( SearchForMoreAsync, () => !IsLoadingMoreResults && _currentPaginationToken != null ); }
        }

        [LogId( "ViewPersion" )]
        [LogParameter( "$Param.FullName" )]
        public Command<Person> ViewPersonCommand
        {
            get { return this.GetCommand<Person>( _navigationService.NavigateTo<PersonViewModel, Person> ); }
        }


        public MainViewModel( IDirectoryService directoryService, INavigationService navigationService,
                              ViewPersonRequest request )
        {
            _directoryService = directoryService;
            _navigationService = navigationService;
            _request = request;

            this.ListenToProperty( x => x.Query, async () => await SearchAsync( Query ) );
        }


        public override async Task OnNavigatedToAsync()
        {
            if ( _request.Query != null )
            {
                await SearchAsync( _request.Query );
                if ( SearchResults != null && SearchResults.Count == 1 )
                {
                    _navigationService.RemoveCurrentFromBackStack();
                    _navigationService.NavigateTo<PersonViewModel, Person>( SearchResults[0] );
                }
            }

            await base.OnNavigatedToAsync();
        }

        private async Task SearchAsync( string query )
        {
            if ( string.IsNullOrWhiteSpace( query ) )
            {
                SearchResults = null;
                return;
            }

            if ( query == _lastQuery )
            {
                return;
            }

            _lastQuery = query;

            await TryExecuteAsync( async token =>
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
                }
            } );
        }

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
                    Query = Query,
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
    }
}