// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Threading;
using PocketCampus.Satellite.Models;
using PocketCampus.Satellite.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Satellite.ViewModels
{
    [LogId( "/satellite" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, BeersResponse>
    {
        private readonly ISatelliteService _beerService;

        private Dictionary<BeerContainer, BeerMenuPart> _beerMenu;


        public Dictionary<BeerContainer, BeerMenuPart> BeerMenu
        {
            get { return _beerMenu; }
            private set { SetProperty( ref _beerMenu, value ); }
        }


        public MainViewModel( IDataCache cache, ISatelliteService beerService )
            : base( cache )
        {
            _beerService = beerService;
        }


        protected override CachedTask<BeersResponse> GetData( bool force, CancellationToken token )
        {
            if ( force )
            {
                return CachedTask.Create( () => _beerService.GetBeersAsync( token ) );
            }
            return CachedTask.NoNewData<BeersResponse>();
        }

        protected override bool HandleData( BeersResponse data, CancellationToken token )
        {
            if ( data.Status != BeerMenuStatus.Success )
            {
                throw new Exception( "An error occurred on the Satellite server-side." );
            }

            if ( !token.IsCancellationRequested )
            {
                BeerMenu = data.BeerMenu;
            }

            return true;
        }
    }
}