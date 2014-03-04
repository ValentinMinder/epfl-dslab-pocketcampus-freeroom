// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    /// <summary>
    /// ViewModel for item details.
    /// </summary>
    [LogId( "/events/event" )]
    public sealed class EventItemViewModel : DataViewModel<long>
    {
        private readonly INavigationService _navigationService;
        private readonly IBrowserService _browserService;
        private readonly IEventsService _eventsService;
        private readonly IPluginSettings _settings;
        private readonly long _itemId;

        private EventItem _item;
        private EventPool[] _pools;
        private bool _isFavorite;


        /// <summary>
        /// Gets the item.
        /// </summary>
        public EventItem Item
        {
            get { return _item; }
            private set { SetProperty( ref _item, value ); }
        }

        /// <summary>
        /// Gets the item's child pools, if any.
        /// </summary>
        public EventPool[] Pools
        {
            get { return _pools; }
            private set { SetProperty( ref _pools, value ); }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the item is a favorite of the user.
        /// </summary>
        public bool IsFavorite
        {
            get { return _isFavorite; }
            set { SetProperty( ref _isFavorite, value ); }
        }


        /// <summary>
        /// Gets the command executed to view a child pool.
        /// </summary>
        [LogId( "ViewPool" )]
        public Command<EventPool> ViewPoolCommand
        {
            get
            {
                return GetCommand<EventPool>( pool =>
                {
                    if ( pool.OverrideTargetUrl == null )
                    {
                        _navigationService.NavigateTo<EventPoolViewModel, ViewPoolRequest>( new ViewPoolRequest( pool.Id ) );
                    }
                    else
                    {
                        _browserService.NavigateTo( pool.OverrideTargetUrl );
                    }
                } );
            }
        }

        /// <summary>
        /// Gets the command executed to open a link.
        /// </summary>
        [LogId( "OpenLink" )]
        public Command<string> OpenLinkCommand
        {
            get { return GetCommand<string>( _browserService.NavigateTo ); }
        }


        /// <summary>
        /// Creates a new EventItemViewModel.
        /// </summary>
        public EventItemViewModel( INavigationService navigationService, IBrowserService browserService,
                                   IEventsService eventsService, IPluginSettings settings,
                                   long itemId )
        {
            _navigationService = navigationService;
            _browserService = browserService;
            _eventsService = eventsService;
            _settings = settings;
            _itemId = itemId;
        }


        /// <summary>
        /// Refreshes the data.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force )
            {
                var request = new EventItemRequest
                {
                    ItemId = _itemId,
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _eventsService.GetEventItemAsync( request );

                if ( response.Status != EventsStatusCode.OK )
                {
                    throw new Exception( "An error occured while fetching an event item." );
                }

                _settings.EventCategories = response.EventCategories;
                _settings.EventTags = response.EventTags;

                Pools = response.ChildrenPools == null ? new EventPool[0] : response.ChildrenPools.Values.ToArray();
                Item = response.Item;
                IsFavorite = _settings.FavoriteItemIds.Contains( Item.Id );
            }
        }


        /// <summary>
        /// Called when the user navigates away from the ViewModel.
        /// </summary>
        public override void OnNavigatedFrom()
        {
            if ( Item == null )
            {
                return;
            }

            if ( IsFavorite && !_settings.FavoriteItemIds.Contains( Item.Id ) )
            {
                _settings.FavoriteItemIds.Add( Item.Id );
            }
            else if ( !IsFavorite && _settings.FavoriteItemIds.Contains( Item.Id ) )
            {
                _settings.FavoriteItemIds.Remove( Item.Id );
            }
        }
    }
}