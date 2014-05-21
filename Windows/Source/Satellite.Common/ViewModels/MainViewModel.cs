// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Satellite.Models;
using PocketCampus.Satellite.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Satellite.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [LogId( "/satellite" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly ISatelliteService _beerService;

        private Dictionary<BeerContainer, BeerMenuPart> _beerMenu;

        /// <summary>
        /// Gets the beer menu.
        /// </summary>
        public Dictionary<BeerContainer, BeerMenuPart> BeerMenu
        {
            get { return _beerMenu; }
            private set { SetProperty( ref _beerMenu, value ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( ISatelliteService beerService )
        {
            _beerService = beerService;
        }


        /// <summary>
        /// Updates the data.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force )
            {
                var beersResponse = await _beerService.GetBeersAsync( token );
                if ( beersResponse.Status != BeerMenuStatus.Success )
                {
                    throw new Exception( "An error occurred on the Satellite server-side." );
                }

                if ( !token.IsCancellationRequested )
                {
                    BeerMenu = beersResponse.BeerMenu;
                }
            }
        }
    }
}