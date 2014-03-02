using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
    public sealed class EventItemViewModel : DataViewModel<long>
    {
        private readonly INavigationService _navigationService;
        private readonly IBrowserService _browserService;
        private readonly IEventsService _eventsService;
        private readonly IPluginSettings _settings;
        private readonly long _itemId;

        private EventItem _item;
        private EventPool[] _pools;

        public EventItem Item
        {
            get { return _item; }
            private set { SetProperty( ref _item, value ); }
        }

        public EventPool[] Pools
        {
            get { return _pools; }
            private set { SetProperty( ref _pools, value ); }
        }

        public bool IsFavorite
        {
            get { return _settings.FavoritesByPool[Item.ParentPoolId ?? -1].Contains( Item.Id ); }
            set
            {
                if ( value )
                {
                    _settings.FavoritesByPool[Item.ParentPoolId ?? -1].Add( Item.Id );
                }
                else
                {
                    _settings.FavoritesByPool[Item.ParentPoolId ?? -1].Remove( Item.Id );
                }
            }
        }

        public Command<EventPool> ViewPoolCommand
        {
            get
            {
                return GetCommand<EventPool>( pool =>
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

        public Command<string> OpenLinkCommand
        {
            get { return GetCommand<string>( _browserService.NavigateTo ); }
        }

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
            }
        }
    }
}