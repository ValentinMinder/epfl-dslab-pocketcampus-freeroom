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
    /// <summary>
    /// The Events plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        private const string ViewPoolQuery = "showEventPool";
        private const string PoolIdParameter = "eventPoolId";
        private const string UserTicketParameter = "userTicket";
        private const string MarkAsFavoriteParameter = "markFavorite";
        private const string ViewItemQuery = "showEventItem";
        private const string EventIdParameter = "eventItemId";

        private IPluginSettings _settings;

        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "Events"; }
        }

        /// <summary>
        /// This plugin does not require authentication. (it uses special QR codes for that since it's also aimed at non-EPFL people)
        /// </summary>
        public bool RequiresAuthentication
        {
            get { return false; }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IEventsService, EventsService>();
            _settings = Container.Bind<IPluginSettings, PluginSettings>();
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<EventPoolViewModel, long>( EventPool.RootId );
        }

        /// <summary>
        /// Navigates to the plugin from an external source, with a destination and parameters.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            switch ( destination )
            {
                case ViewPoolQuery:
                    string ticket = null;
                    parameters.TryGetValue( UserTicketParameter, out ticket );
                    if ( ticket != null )
                    {
                        _settings.UserTickets.Add( ticket );
                    }

                    string favoriteIdString = null;
                    if ( parameters.TryGetValue( MarkAsFavoriteParameter, out favoriteIdString ) )
                    {
                        long favoriteId = long.Parse( favoriteIdString );
                        _settings.FavoriteItemIds.Add( favoriteId );
                        // Ignore the pool, go to the item
                        navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( new ViewEventItemRequest( favoriteId, true ) );
                    }
                    else
                    {
                        long poolId = long.Parse( parameters[PoolIdParameter] );
                        navigationService.NavigateTo<EventPoolViewModel, long>( poolId );
                    }
                    break;

                case ViewItemQuery:
                    long itemId = long.Parse( parameters[EventIdParameter] );
                    navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( new ViewEventItemRequest( itemId, false ) );
                    break;
            }
        }
    }
}