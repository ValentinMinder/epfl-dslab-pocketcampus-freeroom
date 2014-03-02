using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
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
        private readonly IEmailPrompt _emailPrompt;
        private readonly long _poolId;

        private EventPool _pool;
        private EventItemGroup[] _itemGroups;
        private EmailSendingStatus _favoriteEmailStatus;

        public EventPool Pool
        {
            get { return _pool; }
            private set { SetProperty( ref _pool, value ); }
        }

        public EventItemGroup[] ItemGroups
        {
            get { return _itemGroups; }
            private set { SetProperty( ref _itemGroups, value ); }
        }

        public EmailSendingStatus FavoriteEmailStatus
        {
            get { return _favoriteEmailStatus; }
            private set { SetProperty( ref _favoriteEmailStatus, value ); }
        }

        public Command<EventItem> ViewItemCommand
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

        public Command ViewSettingsCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        public AsyncCommand RequestFavoriteEmailCommand
        {
            get { return GetAsyncCommand( RequestFavoriteEmailAsync, () => Pool.EnableFavoriteEmailRequest == true ); }
        }

        public Command ScanCodeCommand
        {
            // TODO
            // URI form: pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=-1&userTicket=6298eb264f3cb42f6faa7b6a7f5c5482
            // use UriMapper on the root app frame, add something in the plugins to handle that, maybe?
            get { return GetCommand( () => { }, () => false ); }
            //get { return GetCommand( () => { }, () => Pool.EnableCodeScanning == true ); }
        }

        public EventPoolViewModel( INavigationService navigationService, IEventsService eventsService,
                                   IPluginSettings settings, IEmailPrompt userPrompt,
                                   long poolId )
        {
            _navigationService = navigationService;
            _eventsService = eventsService;
            _settings = settings;
            _emailPrompt = userPrompt;
            _poolId = poolId;
        }

        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force || ( _pool != null && _pool.AlwaysRefresh == true ) )
            {
                if ( !_settings.FavoritesByPool.ContainsKey( _poolId ) )
                {
                    _settings.FavoritesByPool.Add( _poolId, new List<long>() );
                }
                if ( !_settings.ExcludedCategoriesByPool.ContainsKey( _poolId ) )
                {
                    _settings.ExcludedCategoriesByPool.Add( _poolId, new List<int>() );
                }
                if ( !_settings.ExcludedTagsByPool.ContainsKey( _poolId ) )
                {
                    _settings.ExcludedTagsByPool.Add( _poolId, new List<string>() );
                }


                var request = new EventPoolRequest
                {
                    PoolId = _poolId,
                    DayCount = (int) _settings.SearchPeriod,
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

                Pool = response.Pool;
                Pool.Items = response.ChildrenItems == null ? new EventItem[0] : response.ChildrenItems.Values.ToArray();
            }

            var groups = from item in Pool.Items
                         where item.CategoryId == null
                            || !_settings.ExcludedCategoriesByPool[_poolId].Contains( item.CategoryId.Value )
                         where item.TagIds == null
                            || !item.TagIds.Any( _settings.ExcludedTagsByPool[_poolId].Contains )
                         orderby item.TimeOverride ascending,
                                 item.StartDate descending,
                                 item.EndDate ascending,
                                 item.Name ascending
                         group item by item.CategoryId into itemGroup
                         let categName = itemGroup.Key.HasValue ? _settings.EventCategories[itemGroup.Key.Value] : null
                         select new EventItemGroup( categName, itemGroup );

            ItemGroups = groups.ToArray();

            foreach ( var group in ItemGroups )
            {
                System.Diagnostics.Debug.WriteLine(
    string.Join( Environment.NewLine, string.Format( @"new EventItemGroup( ""{0}"", new[]
{{
{1}
}} ),;
",
    group.CategoryName,
    string.Join( Environment.NewLine, group.Select( i =>
        string.Format(
    @"new EventItem
{{
    Name = ""{0}"",
    SpeakerName = ""{1}"",
    Location = ""{2}"",
    StartDate = {3},
    EndDate {4},
    IsFullDay = {5},
    TimeOverride = ""{6}"",
    ShortDetails = @""{7}"",
    PictureUrl = @""{8}"",
    PictureThumbnailUrl = @""{9}"",
    HidePictureThumbnail = {10},
    HideName = {11},
    HideInformation = {12},
    Details = @""{13}"",
    DetailsUrl = @""{14}""
}},",
    i.Name ?? "",
    i.SpeakerName ?? "",
    i.Location ?? "",
    ( i.StartDate.HasValue ? string.Format( "new DateTime( {0}, {1}, {2}, {3}, {4}, {5} )", i.StartDate.Value.Year, i.StartDate.Value.Month, i.StartDate.Value.Day, i.StartDate.Value.Hour, i.StartDate.Value.Minute, i.StartDate.Value.Second ) : "null" ),
    ( i.EndDate.HasValue ? string.Format( "new DateTime( {0}, {1}, {2}, {3}, {4}, {5} )", i.EndDate.Value.Year, i.EndDate.Value.Month, i.EndDate.Value.Day, i.EndDate.Value.Hour, i.EndDate.Value.Minute, i.EndDate.Value.Second ) : "null" ),
    i.IsFullDay == null ? "null" : i.IsFullDay.Value.ToString(),
    i.TimeOverride ?? "",
    i.ShortDetails ?? "",
    i.PictureUrl ?? "",
    i.PictureThumbnailUrl ?? "",
    i.HidePictureThumbnail == null ? "null" : i.HidePictureThumbnail.ToString(),
    i.HideName == null ? "null" : i.HideName.Value.ToString(),
    i.HideInformation == null ? "null" : i.HideInformation.Value.ToString(),
    i.Details ?? "",
    i.DetailsUrl ?? ""
    ) ) ) )
        .Replace( "True", "true" ).Replace( "False", "false" ).Split( new[] { Environment.NewLine }, StringSplitOptions.None ).Select( s => "                    " + s ) ) );
            }
        }

        private async Task RequestFavoriteEmailAsync()
        {
            FavoriteEmailStatus = EmailSendingStatus.NoneRequested;

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
                    FavoriteItems = _settings.FavoritesByPool[Pool.Id].ToArray(),
                    UserTickets = _settings.UserTickets.ToArray(),
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var response = await _eventsService.SendFavoriteItemsByEmailAsync( request );

                if ( response.Status != EventsStatusCode.OK )
                {
                    throw new Exception( "An error occurred while requesting an e-mail with the favorites." );
                }

                FavoriteEmailStatus = EmailSendingStatus.Success;
            }
            catch
            {
                FavoriteEmailStatus = EmailSendingStatus.Error;
            }
        }
    }
}