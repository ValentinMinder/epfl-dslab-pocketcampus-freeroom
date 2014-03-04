// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Events.ViewModels;
using PocketCampus.Mvvm;

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
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<EventPoolViewModel, ViewPoolRequest>( new ViewPoolRequest( EventPool.RootId ) );
        }

        /// <summary>
        /// Navigates to the plugin from an external source, with a destination and parameters.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            if ( destination == ViewPoolQuery )
            {
                long id = long.Parse( parameters[PoolIdParameter] );

                string ticket = null;
                parameters.TryGetValue( UserTicketParameter, out ticket );

                long? favoriteId = null;
                string favoriteIdString = null;
                if ( parameters.TryGetValue( MarkAsFavoriteParameter, out favoriteIdString ) )
                {
                    favoriteId = long.Parse( favoriteIdString );
                }

                navigationService.NavigateTo<EventPoolViewModel, ViewPoolRequest>( new ViewPoolRequest( id, ticket, favoriteId ) );
            }
        }
    }
}