// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    /// <summary>
    /// ViewModel for pool details.
    /// </summary>
    [LogId( "/events/pool" )]
    public sealed class EventPoolViewModel : CachedDataViewModel<long, EventPoolResponse>
    {
        private static readonly TimeSpan CacheDuration = TimeSpan.FromDays( 7 );

        private readonly INavigationService _navigationService;
        private readonly IEventsService _eventsService;
        private readonly IPluginSettings _settings;
        private readonly IEmailPrompt _emailPrompt;
        private readonly ICodeScanner _codeScanner;
        private readonly long _poolId;

        private EventPool _pool;
        private EventItemGroup[] _itemGroups;
        private EmailSendingStatus _emailStatus;

        private Tuple<SearchPeriod, bool> _previousSettings;


        /// <summary>
        /// Gets the pool.
        /// </summary>
        public EventPool Pool
        {
            get { return _pool; }
            private set { SetProperty( ref _pool, value ); }
        }

        /// <summary>
        /// Gets the pool's child items, grouped by category.
        /// </summary>
        public EventItemGroup[] ItemGroups
        {
            get { return _itemGroups; }
            private set { SetProperty( ref _itemGroups, value ); }
        }

        /// <summary>
        /// Gets the status of the e-mail with the user's favorites.
        /// </summary>
        public EmailSendingStatus EmailStatus
        {
            get { return _emailStatus; }
            private set { SetProperty( ref _emailStatus, value ); }
        }

        /// <summary>
        /// Gets the command executed to view a child item.
        /// </summary>
        [LogId( "ShowEventItem" )]
        [LogParameter( "$Param.LogId" )]
        public Command<EventItem> ViewItemCommand
        {
            get
            {
                return this.GetCommand<EventItem>( item =>
                {
                    var request = new ViewEventItemRequest( item.Id, Pool.DisableFavorites != true );
                    _navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( request );
                } );
            }
        }

        /// <summary>
        /// Gets the command executed to show current events only.
        /// </summary>
        [LogId( "RightNow" )]
        public Command ShowCurrentEventsCommand
        {
            get { return this.GetCommand( ShowCurrentEvents ); }
        }

        /// <summary>
        /// Gets the command executed to show the category filters.
        /// </summary>
        [LogId( "ShowCategories" )]
        public Command FilterByCategoryCommand
        {
            get { return this.GetCommand( () => _navigationService.NavigateTo<CategoryFilterViewModel, EventPool>( Pool ), () => Pool.DisableCategoryFiltering != true ); }
        }

        /// <summary>
        /// Gets the command executed to show the tag filters.
        /// </summary>
        [LogId( "ShowTags" )]
        public Command FilterByTagCommand
        {
            get { return this.GetCommand( () => _navigationService.NavigateTo<TagFilterViewModel, EventPool>( Pool ), () => Pool.DisableTagFiltering != true ); }
        }

        /// <summary>
        /// Gets the command executed to show the settings.
        /// </summary>
        [LogId( "ShowSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to request the e-mail with the user's favorite events.
        /// </summary>
        [LogId( "RequestEmail" )]
        public AsyncCommand RequestFavoriteEmailCommand
        {
            get { return this.GetAsyncCommand( RequestFavoriteEmailAsync, () => Pool.EnableFavoriteEmailRequest == true ); }
        }

        /// <summary>
        /// Gets the command executed to let the user scan QR codes.
        /// </summary>
        [LogId( "ShowCodeScanner" )]
        public Command ScanCodeCommand
        {
            get { return this.GetCommand( _codeScanner.ScanCode, () => Pool.EnableCodeScanning == true ); }
        }


        /// <summary>
        /// Creates a new EventPoolViewModel.
        /// </summary>
        public EventPoolViewModel( IDataCache cache, INavigationService navigationService, IEventsService eventsService,
                                   IPluginSettings settings, IEmailPrompt emailPrompt, ICodeScanner codeScanner,
                                   long poolId )
            : base( cache )
        {
            _navigationService = navigationService;
            _eventsService = eventsService;
            _settings = settings;
            _emailPrompt = emailPrompt;
            _codeScanner = codeScanner;
            _poolId = poolId;

            _previousSettings = Tuple.Create( (SearchPeriod) 0, false );
        }


        protected override CachedTask<EventPoolResponse> GetData( bool force, CancellationToken token )
        {
            if ( force
              || Pool == null
              || ( Pool != null && Pool.AlwaysRefresh == true )
              || ( _previousSettings.Item1 != _settings.SearchPeriod || _previousSettings.Item2 != _settings.SearchInPast ) )
            {
                if ( !_settings.ExcludedCategoriesByPool.ContainsKey( _poolId ) )
                {
                    _settings.ExcludedCategoriesByPool.Add( _poolId, new int[0] );
                }
                if ( !_settings.ExcludedTagsByPool.ContainsKey( _poolId ) )
                {
                    _settings.ExcludedTagsByPool.Add( _poolId, new string[0] );
                }

                var request = new EventPoolRequest
                {
                    PoolId = _poolId,
                    HoursCount = (int) _settings.SearchPeriod,
                    IsInPast = _settings.SearchInPast,
                    UserTickets = _settings.UserTickets.ToArray(),
                    FavoriteEventIds = _settings.FavoriteItemIds.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };
                return CachedTask.Create( () => _eventsService.GetEventPoolAsync( request, token ), _poolId, DateTime.Now.Add( CacheDuration ) );
            }
            return CachedTask.NoNewData<EventPoolResponse>();
        }

        protected override bool HandleData( EventPoolResponse data, CancellationToken token )
        {
            if ( data.Status != EventsStatus.Success )
            {
                throw new Exception( "An error occurred while fetching the event pool." );
            }

            _settings.EventTags = data.EventTags;
            _settings.EventCategories = data.EventCategories;

            _previousSettings = Tuple.Create( _settings.SearchPeriod, _settings.SearchInPast );

            Pool = data.Pool;
            Pool.Items = data.ChildrenItems == null ? new EventItem[0] : data.ChildrenItems.Values.ToArray();


            var groups = from item in Pool.Items
                         where item.CategoryId == null
                            || !_settings.ExcludedCategoriesByPool[_poolId].Contains( item.CategoryId.Value )
                         where item.TagIds == null
                            || !item.TagIds.Any( _settings.ExcludedTagsByPool[_poolId].Contains )
                         orderby item.TimeOverride ascending,
                                 item.StartDate ascending,
                                 item.EndDate ascending,
                                 item.Name ascending
                         // Beautiful hack to force favorites into a "Favorites" category
                         group item by ( _settings.FavoriteItemIds.Contains( item.Id ) ? EventItem.FavoriteCategoryId : item.CategoryId ) into itemGroup
                         orderby itemGroup.Key ascending
                         let categName = itemGroup.Key.HasValue
                                      && _settings.EventCategories.ContainsKey( itemGroup.Key.Value )
                                       ? _settings.EventCategories[itemGroup.Key.Value]
                                       : "???"
                         select new EventItemGroup( categName, itemGroup );

            ItemGroups = groups.ToArray();

            return true;
        }

        private void ShowCurrentEvents()
        {
            _settings.SearchPeriod = SearchPeriod.Now;
            _settings.SearchInPast = false;
            RefreshAsync( true, CurrentCancellationToken );
        }


        /// <summary>
        /// Asynchronously requests an e-mail with the user's favorite items.
        /// </summary>
        private async Task RequestFavoriteEmailAsync()
        {
            EmailStatus = EmailSendingStatus.NoneRequested;

            string emailAddress = _emailPrompt.GetEmail();

            if ( emailAddress == null )
            {
                return;
            }

            try
            {
                var request = new FavoriteEmailRequest
                {
                    EmailAddress = emailAddress,
                    PoolId = Pool.Id,
                    FavoriteItems = _settings.FavoriteItemIds.ToArray(),
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _eventsService.SendFavoriteItemsByEmailAsync( request );

                if ( response.Status != EventsStatus.Success )
                {
                    throw new Exception( "An error occurred while requesting an e-mail with the favorites." );
                }

                EmailStatus = EmailSendingStatus.Success;
            }
            catch
            {
                EmailStatus = EmailSendingStatus.Error;
            }
        }
    }
}