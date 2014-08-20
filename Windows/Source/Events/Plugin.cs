// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
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
            get { return "events"; }
        }

        /// <summary>
        /// This plugin is visible in the application's main menu.
        /// </summary>
        public bool IsVisible
        {
            get { return true; }
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
            _settings = Container.Bind<IPluginSettings, PluginSettings>();
            Container.Bind<IEventsService, EventsService>();
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
                    if ( parameters.TryGetValue( UserTicketParameter, out ticket ) && !_settings.UserTickets.Contains( ticket ) )
                    {
                        // TODO once this is an observablecollection, make this simpler
                        string[] newTickets = new string[_settings.UserTickets.Length + 1];
                        Array.Copy( _settings.UserTickets, newTickets, _settings.UserTickets.Length );
                        newTickets[_settings.UserTickets.Length] = ticket;
                        _settings.UserTickets = newTickets;
                    }

                    string favoriteIdString = null;
                    if ( parameters.TryGetValue( MarkAsFavoriteParameter, out favoriteIdString ) )
                    {
                        long favoriteId = long.Parse( favoriteIdString );
                        _settings.FavoriteItemIds.Add( favoriteId );

                        var request = new ViewEventItemRequest( favoriteId, true );
                        // Ignore the pool, go to the item
                        navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( request );
                    }
                    else
                    {
                        long poolId = long.Parse( parameters[PoolIdParameter] );
                        navigationService.NavigateTo<EventPoolViewModel, long>( poolId );
                    }
                    break;

                case ViewItemQuery:
                    {
                        long itemId = long.Parse( parameters[EventIdParameter] );
                        var request = new ViewEventItemRequest( itemId, false );
                        navigationService.NavigateTo<EventItemViewModel, ViewEventItemRequest>( request );
                        break;
                    }
            }
        }
    }
}