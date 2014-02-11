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
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/food" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
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
        [LogParameter( "$Param.Name" )]
        public Command<Meal> RateMealCommand
        {
            get { return GetCommand<Meal>( RateMeal ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( INavigationService navigationService, IPluginSettings settings, IFoodService menuService )
        {
            Settings = settings;
            _navigationService = navigationService;
            _menuService = menuService;

            _mealDate = DateTime.Now;
            _mealTime = _mealDate.Hour <= LunchLimit ? MealTime.Lunch : MealTime.Dinner;
        }


        /// <summary>
        /// Executed when the user selects another meal time or date.
        /// </summary>
        private async void UpdateMenu()
        {
            await TryRefreshAsync( true );
        }

        /// <summary>
        /// Refreshes the data.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force )
            {
                var req = new FoodRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    MealTime = MealTime,
                    Date = MealDate
                };
                var resp = await _menuService.GetMenusAsync( req );

                if ( resp.Status != FoodStatus.Ok )
                {
                    throw new Exception( "An error occurred while fetching the menu on the server." );
                }

                _fullMenu = resp.Menu;
            }

            // using Any() on Settings.DisplayedDishTypes allows dishes with more than one type to be displayed
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
        }

        /// <summary>
        /// Shows a rating page for the specified meal.
        /// </summary>
        private void RateMeal( Meal meal )
        {
            _navigationService.NavigateTo<RatingViewModel, RatingInfo>( new RatingInfo( meal, MealTime, MealDate ) );
        }
    }
}