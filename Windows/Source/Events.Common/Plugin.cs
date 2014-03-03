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
    public class Plugin : IPlugin
    {
        private const string ViewPoolQuery = "showEventPool";
        private const string PoolIdParameter = "eventPoolId";
        private const string UserTicketParameter = "userTicket";

        public string Id
        {
            get { return "Events"; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IEventsService, EventsService>();
        }

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
                navigationService.NavigateTo<EventPoolViewModel, ViewPoolRequest>( new ViewPoolRequest( id, ticket ) );
            }
        }
    }
}