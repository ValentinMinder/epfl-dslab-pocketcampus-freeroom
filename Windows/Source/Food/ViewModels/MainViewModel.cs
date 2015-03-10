// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using PocketCampus.Map;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    [LogId( "/food" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, FoodResponse>
    {
        // On startup, lunch is selected if the current hour is less than or equal to this one
        private const int LunchLimit = 18;

        private readonly INavigationService _navigationService;
        private readonly IFoodService _menuService;
        private readonly ICredentialsStorage _credentials;

        // The unfiltered menu
        private Restaurant[] _fullMenu;

        private Restaurant[] _menu;
        private SearchStatus _menuStatus;
        private MealTime _mealTime;
        private DateTime _mealDate;


        public Restaurant[] Menu
        {
            get { return _menu; }
            private set { SetProperty( ref _menu, value ); }
        }

        public SearchStatus MenuStatus
        {
            get { return _menuStatus; }
            private set { SetProperty( ref _menuStatus, value ); }
        }

        public MealTime MealTime
        {
            get { return _mealTime; }
            set { SetProperty( ref _mealTime, value ); }
        }

        public DateTime MealDate
        {
            get { return _mealDate; }
            set { SetProperty( ref _mealDate, value ); }
        }

        public IPluginSettings Settings { get; private set; }


        [LogId( "ViewRestaurantOnMap" )]
        [LogParameter( "$Param.Name" )]
        public Command<Restaurant> ViewMapItemCommand
        {
            get { return this.GetCommand<Restaurant>( r => Messenger.Send( new MapSearchRequest( r.MapItem ) ) ); }
        }

        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        [LogId( "RateMeal" )]
        [LogParameter( "$Param.LogId" )]
        public Command<Meal> RateMealCommand
        {
            get { return this.GetCommand<Meal>( _navigationService.NavigateTo<RatingViewModel, Meal> ); }
        }


        public MainViewModel( IDataCache cache, INavigationService navigationService, IFoodService menuService,
                              ICredentialsStorage credentials, IPluginSettings settings )
            : base( cache )
        {
            _navigationService = navigationService;
            _menuService = menuService;
            _credentials = credentials;

            _mealDate = DateTime.Now;
            _mealTime = GetMealTime( _mealDate.Hour );

            Settings = settings;
            Settings.PropertyChanged += ( _, __ ) => UpdateMenu();

            this.ListenToProperty( x => x.MealDate, RefreshMenu );
            this.ListenToProperty( x => x.MealTime, RefreshMenu );
        }


        private async void RefreshMenu()
        {
            await TryRefreshAsync( true );
        }

        protected override CachedTask<FoodResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<FoodResponse>();
            }

            var request = new FoodRequest
            {
                Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                MealTime = MealTime,
                Date = MealDate,
                UserName = _credentials.UserName
            };

            Func<Task<FoodResponse>> getter = () => _menuService.GetMenusAsync( request, token );

            if ( MealDate.Date == DateTime.Now.Date )
            {
                return CachedTask.Create( getter, MealTime.GetHashCode(), DateTime.Now.Date.AddDays( 1 ) );
            }
            return CachedTask.DoNotCache( getter );
        }

        protected override bool HandleData( FoodResponse data, CancellationToken token )
        {
            if ( data.Status != FoodStatus.Success )
            {
                throw new Exception( "An error occurred while fetching the menu on the server." );
            }

            if ( !Settings.IsPriceTargetOverriden && data.UserPriceTarget.HasValue )
            {
                Settings.PriceTarget = data.UserPriceTarget.Value;
            }

            foreach ( var restaurant in data.Menu )
            {
                foreach ( var meal in restaurant.Meals )
                {
                    meal.Restaurant = restaurant;
                }
            }

            if ( !token.IsCancellationRequested )
            {
                _fullMenu = data.Menu;
                UpdateMenu();
            }

            return true;
        }


        private void UpdateMenu()
        {
            if ( _fullMenu == null )
            {
                return;
            }

            // Using Any() on Settings.DisplayedMealTypes displays meals with more than one type
            // even if the user doesn't want the second type to appear
            var forbiddenTypes = EnumEx.GetValues<MealType>().Where( type => !Settings.DisplayedMealTypes.Contains( type ) ).ToArray();

            foreach ( var meal in _fullMenu.SelectMany( r => r.Meals ) )
            {
                meal.SetCurrentPrice( Settings.PriceTarget );
            }

            Menu = ( from restaurant in _fullMenu
                     let meals = from meal in restaurant.Meals
                                 where !forbiddenTypes.Any( type => meal.MealTypes.Contains( type ) )
                                 where meal.CurrentPrice == null || meal.CurrentPrice <= Settings.MaximumBudget
                                    || ( meal.HalfPortionPrice.HasValue && meal.HalfPortionPrice.Value <= Settings.MaximumBudget )
                                 select meal
                     where meals.Any()
                     orderby restaurant.Name ascending
                     select restaurant.CopyWithMeals( meals ) )
                    .ToArray();

            MenuStatus = Menu.Length == 0
                ? _fullMenu.Length == 0 ? SearchStatus.NoResults : SearchStatus.AllResultsFilteredOut
                : SearchStatus.Finished;
        }

        private static MealTime GetMealTime( int hour )
        {
            return hour <= LunchLimit ? MealTime.Lunch : MealTime.Dinner;
        }
    }
}