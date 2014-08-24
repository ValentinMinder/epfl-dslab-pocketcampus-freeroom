﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using PocketCampus.Common.Services;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    /// <summary>
    /// ViewModel for item details.
    /// </summary>
    [LogId( "/events/item" )]
    public sealed class EventItemViewModel : CachedDataViewModel<ViewEventItemRequest, EventItemResponse>
    {
        private static readonly TimeSpan CacheDuration = TimeSpan.FromDays( 7 );

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
        /// Gets a value indicating whether the item can be set as a favorite.
        /// </summary>
        public bool CanBeFavorite { get; private set; }

        /// <summary>
        /// Gets or sets a value indicating whether the item is a favorite of the user.
        /// </summary>
        public bool IsFavorite
        {
            get { return _isFavorite; }
            set
            {
                SetProperty( ref _isFavorite, value );

                string eventName = value ? "MarkFavorite" : "UnmarkFavorite";
                Messenger.Send( new EventLogRequest( eventName, Item.LogId ) );
            }
        }

        /// <summary>
        /// Gets the command executed to view a child pool.
        /// </summary>
        [LogId( "ShowPool" )]
        [LogParameter( "$Param.LogId" )]
        public Command<EventPool> ViewPoolCommand
        {
            get
            {
                return this.GetCommand<EventPool>( pool =>
                {
                    if ( pool.OverrideTargetUrl == null )
                    {
                        _navigationService.NavigateTo<EventPoolViewModel, long>( pool.Id );
                    }
                    else
                    {
                        _browserService.NavigateTo( pool.OverrideTargetUrl );
                    }
                } );
            }
        }

        /// <summary>
        /// Gets the command executed to open the "more details" link.
        /// </summary>
        [LogId( "ViewMoreDetails" )]
        [LogParameter( "Item.LogId" )]
        public Command ViewMoreDetailsCommand
        {
            get { return this.GetCommand( () => _browserService.NavigateTo( Item.DetailsUrl ) ); }
        }

        /// <summary>
        /// Gets the command executed to open the location on the map.
        /// </summary>
        [LogId( "ViewOnMap" )]
        [LogParameter( "Item.Location" )]
        public Command ViewOnMapCommand
        {
            get { return this.GetCommand( () => _browserService.NavigateTo( Item.LocationUrl ) ); }
        }


        /// <summary>
        /// Creates a new EventItemViewModel.
        /// </summary>
        public EventItemViewModel( IDataCache cache, INavigationService navigationService, IBrowserService browserService,
                                   IEventsService eventsService, IPluginSettings settings,
                                   ViewEventItemRequest request )
            : base( cache )
        {
            _navigationService = navigationService;
            _browserService = browserService;
            _eventsService = eventsService;
            _settings = settings;
            _itemId = request.ItemId;

            CanBeFavorite = request.CanBeFavorite;
        }


        protected override CachedTask<EventItemResponse> GetData( bool force, CancellationToken token )
        {
            if ( force )
            {
                var request = new EventItemRequest
                {
                    ItemId = _itemId,
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                return CachedTask.Create( () => _eventsService.GetEventItemAsync( request, token ), _itemId, DateTime.Now.Add( CacheDuration ) );
            }

            return CachedTask.NoNewData<EventItemResponse>();
        }

        protected override bool HandleData( EventItemResponse data, CancellationToken token )
        {
            if ( data.Status != EventsStatus.Success )
            {
                throw new Exception( "An error occured while fetching an event item." );
            }

            _settings.EventCategories = data.EventCategories;
            _settings.EventTags = data.EventTags;

            var pools = data.ChildrenPools == null ? Enumerable.Empty<EventPool>() : data.ChildrenPools.Values;
            Pools = pools.OrderBy( p => p.Id ).ToArray();
            Item = data.Item;
            IsFavorite = _settings.FavoriteItemIds.Contains( Item.Id );

            return true;
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