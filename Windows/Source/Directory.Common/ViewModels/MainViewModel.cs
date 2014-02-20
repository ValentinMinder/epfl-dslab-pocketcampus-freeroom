// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
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
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly IDirectoryService _directoryService;
        private readonly INavigationService _navigationService;

        private Person[] _searchResults;
        private bool _anySearchResults;


        /// <summary>
        /// Gets the search results.
        /// </summary>
        public Person[] SearchResults
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
        public Func<string, Task<IEnumerable<object>>> SearchAutoCompleteProvider
        {
            get { return ProvideSearchSuggestionsAsync; }
        }

        /// <summary>
        /// Gets the command executed to search for people.
        /// </summary>
        [LogId( "Search" )]
        public AsyncCommand<string> SearchCommand
        {
            get { return GetAsyncCommand<string>( ExecuteSearchCommand ); }
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
        public MainViewModel( IDirectoryService directoryService, INavigationService navigationService )
        {
            _directoryService = directoryService;
            _navigationService = navigationService;
            _anySearchResults = true;
        }


        /// <summary>
        /// Asynchronously provides search suggestions for the specified query.
        /// </summary>
        private async Task<IEnumerable<object>> ProvideSearchSuggestionsAsync( string query )
        {
            // return await to benefit from implicit conversions
            return await _directoryService.SearchPartialMatchesAsync( query );
        }

        /// <summary>
        /// Asynchronously searches for people with the specified query.
        /// </summary>
        private Task ExecuteSearchCommand( string query )
        {
            return TryExecuteAsync( async token =>
            {
                var results = await _directoryService.SearchPeopleAsync( query );

                if ( !token.IsCancellationRequested )
                {
                    SearchResults = results;
                    AnySearchResults = SearchResults.Length > 0;
                }
            } );
        }
    }
}