// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using PocketCampus.Map;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/food" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, FoodResponse>
    {
        // On startup, lunch is selected if the current hour is less than or equal to this one
        private const int LunchLimit = 18;

        private readonly INavigationService _navigationService;
        private readonly IFoodService _menuService;

        // The unfiltered menu
        private Restaurant[] _fullMenu;

        private Restaurant[] _menu;
        private bool _anyMeals;
        private bool _anyFilterResults;
        private MealTime _mealTime;
        private DateTime _mealDate;

        /// <summary>
        /// Gets the filtered list of menus.
        /// </summary>
        public Restaurant[] Menu
        {
            get { return _menu; }
            private set { SetProperty( ref _menu, value ); }
        }

        /// <summary>
        /// Gets a value indicating whether there are any meals, even filtered ones.
        /// </summary>
        public bool AnyMeals
        {
            get { return _anyMeals; }
            private set { SetProperty( ref _anyMeals, value ); }
        }

        /// <summary>
        /// Gets a value indicating whether there are any filter results.
        /// </summary>
        public bool AnyFilterResults
        {
            get { return _anyFilterResults; }
            private set { SetProperty( ref _anyFilterResults, value ); }
        }

        /// <summary>
        /// Gets or sets the menu's meal time.
        /// </summary>
        public MealTime MealTime
        {
            get { return _mealTime; }
            set { SetProperty( ref _mealTime, value ); UpdateMenu(); }
        }

        /// <summary>
        /// Gets or sets the menu's date.
        /// </summary>
        public DateTime MealDate
        {
            get { return _mealDate; }
            set { SetProperty( ref _mealDate, value ); UpdateMenu(); }
        }

        /// <summary>
        /// Gets a value indicating whether the ratings are enabled.
        /// </summary>
        public bool AreRatingsEnabled { get; private set; }

        /// <summary>
        /// Gets the settings.
        /// </summary>
        public IPluginSettings Settings { get; private set; }

        /// <summary>
        /// Gets the command executed to view a restaurant on the map.
        /// </summary>
        [LogId( "ViewRestaurantOnMap" )]
        [LogParameter( "$Param.Name" )]
        public Command<Restaurant> ViewMapItemCommand
        {
            get { return GetCommand<Restaurant>( r => Messenger.Send( new MapSearchRequest( r.MapItem ) ) ); }
        }

        /// <summary>
        /// Gets the command executed to show the settings.
        /// </summary>
        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }


        /// <summary>
        /// Gets the command executed to vote on a meal.
        /// </summary>
        [LogId( "RateMeal" )]
        public Command<Meal> RateMealCommand
        {
            get { return GetCommand<Meal>( _navigationService.NavigateTo<RatingViewModel, Meal>, _ => AreRatingsEnabled ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( ICache cache, INavigationService navigationService, IFoodService menuService,
                              IPluginSettings settings, IServerSettings serverSettings )
            : base( cache )
        {
            _navigationService = navigationService;
            _menuService = menuService;

            _mealDate = DateTime.Now;
            _mealTime = GetMealTime( _mealDate.Hour );

            AreRatingsEnabled = serverSettings.Configuration.AreFoodRatingsEnabled != 0;
            Settings = settings;
        }


        /// <summary>
        /// Executed when the user selects another meal time or date.
        /// </summary>
        private async void UpdateMenu()
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
                Date = MealDate
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

            foreach ( var restaurant in data.Menu )
            {
                foreach ( var meal in restaurant.Meals )
                {
                    meal.Restaurant = restaurant;
                }
            }

            _fullMenu = data.Menu;

            // the simple solution, using Any() on Settings.DisplayedMealTypes, displays meals with more than one type
            // even if the user doesn't want the second type to appear
            var forbiddenTypes = EnumEx.GetValues<MealType>().Where( type => !Settings.DisplayedMealTypes.Contains( type ) );

            var menu = from restaurant in _fullMenu
                       let meals = from meal in restaurant.Meals
                                   where !forbiddenTypes.Any( type => meal.MealTypes.Contains( type ) )
                                   let mealPrice = meal.GetPrice( Settings.PriceTarget )
                                   where mealPrice == null || mealPrice <= Settings.MaximumBudget
                                      || ( meal.HalfPortionPrice != null && meal.HalfPortionPrice <= Settings.MaximumBudget )
                                   select meal
                       where meals.Any()
                       orderby restaurant.Name ascending
                       select restaurant.WithMeals( meals );

            if ( !token.IsCancellationRequested )
            {
                Menu = menu.ToArray();
                AnyMeals = _fullMenu.Any();
                AnyFilterResults = Menu.Any();
            }

            return true;
        }


        /// <summary>
        /// Gets the meal time associated with the specified hour.
        /// </summary>
        private static MealTime GetMealTime( int hour )
        {
            return hour <= LunchLimit ? MealTime.Lunch : MealTime.Dinner;
        }
    }
}