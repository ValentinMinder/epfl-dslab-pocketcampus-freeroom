// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Events.ViewModels;
using ThinMvvm;

namespace PocketCampus.Events
{
    public class Plugin : IPlugin
    {
        private const string ViewPoolQuery = "showEventPool";
        private const string PoolIdParameter = "eventPoolId";
        private const string UserTicketParameter = "userTicket";
        private const string MarkAsFavoriteParameter = "markFavorite";
        private const string ViewItemQuery = "showEventItem";
        private const string EventIdParameter = "eventItemId";

        private IPluginSettings _settings;

        public string Id
        {
            get { return "events"; }
        }

        public bool IsVisible
        {
            get { return true; }
        }

        // This plugin uses special QR codes for that since it's also aimed at non-EPFL people
        public bool RequiresAuthentication
        {
            get { return false; }
        }


        public void Initialize( INavigationService navigationService )
        {
            _settings = Container.Bind<IPluginSettings, PluginSettings>();
            Container.Bind<IEventsService, EventsService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<EventPoolViewModel, long>( EventPool.RootId );
        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            switch ( destination )
            {
                case ViewPoolQuery:
                    string ticket;
                    if ( parameters.TryGetValue( UserTicketParameter, out ticket ) && !_settings.UserTickets.Contains( ticket ) )
                    {
                        _settings.UserTickets.Add( ticket );
                    }

                    string favoriteIdString;
                    if ( parameters.TryGetValue( MarkAsFavoriteParameter, out favoriteIdString ) )
                    {
                        long favoriteId = long.Parse( favoriteIdString );
                        _settings.FavoriteItemIds.Add( favoriteId );

                        var poolRequest = new ViewEventItemRequest( favoriteId, true );
                        // Ignore the pool, go to the item
                        navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( poolRequest );
                    }
                    else
                    {
                        long poolId = long.Parse( parameters[PoolIdParameter] );
                        navigationService.NavigateTo<EventPoolViewModel, long>( poolId );
                    }
                    break;

                case ViewItemQuery:
                    long itemId = long.Parse( parameters[EventIdParameter] );
                    var itemRequest = new ViewEventItemRequest( itemId, false );
                    navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( itemRequest );
                    break;

                default:
                    NavigateTo( navigationService );
                    break;
            }
        }
    }
}