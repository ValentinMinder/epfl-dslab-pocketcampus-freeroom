using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
    public sealed class EventPoolViewModel : DataViewModel<long>
    {
        private readonly INavigationService _navigationService;
        private readonly IEventsService _eventsService;
        private readonly IPluginSettings _settings;
        private readonly long _poolId;

        private EventPool _pool;

        public EventPool Pool
        {
            get { return _pool; }
            private set { SetProperty( ref _pool, value ); }
        }

        public Command<EventItem> ViewEventItemCommand
        {
            get { return GetCommand<EventItem>( item => _navigationService.NavigateTo<EventItemViewModel, long>( item.Id ) ); }
        }

        public Command FilterByCategoryCommand
        {
            get { return GetCommand( () => _navigationService.NavigateTo<CategoryFilterViewModel, EventPool>( Pool ), () => Pool.DisableCategoryFiltering != true ); }
        }

        public Command FilterByTagCommand
        {
            get { return GetCommand( () => _navigationService.NavigateTo<TagFilterViewModel, EventPool>( Pool ), () => Pool.DisableTagFiltering != true ); }
        }

        public AsyncCommand RequestFavoriteEmailCommand
        {
            get { return GetAsyncCommand( RequestFavoriteEmailAsync, () => Pool.EnableFavoriteEmailRequest == true ); }
        }

        public Command ScanCodeCommand
        {
            get { return GetCommand( () => { }, () => Pool.EnableCodeScanning == true ); }
        }

        public EventPoolViewModel( INavigationService navigationService, IEventsService eventsService,
                                   IPluginSettings settings,
                                   long poolId )
        {
            _navigationService = navigationService;
            _eventsService = eventsService;
            _settings = settings;
            _poolId = poolId;
        }

        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force || ( _pool != null && _pool.AlwaysRefresh == true ) )
            {
                var request = new EventPoolRequest
                {
                    PoolId = _poolId,
                    DayCount = _settings.SearchDayCount,
                    IsInPast = _settings.SearchInPast,
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };
                var response = await _eventsService.GetEventPoolAsync( request );

                if ( response.Status != EventsStatusCode.OK )
                {
                    throw new Exception( "An error occurred while fetching the event pool." );
                }

                _settings.EventTags = response.EventTags;
                _settings.EventCategories = response.EventCategories;

                response.Pool.Items = response.ChildrenItems.Values.ToArray();
                Pool = response.Pool;
            }
        }

        private Task RequestFavoriteEmailAsync()
        {
            return TryExecuteAsync( async _ =>
            {
                // TODO email and favorites
                // also, some status thing

                var request = new FavoriteEmailRequest
                {
                    PoolId = Pool.Id,
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _eventsService.SendFavoriteItemsByEmailAsync( request );

                if ( response.Status != EventsStatusCode.OK )
                {
                    throw new Exception( "An error occurred while requesting an e-mail with the favorites." );
                }
            } );
        }
    }
}