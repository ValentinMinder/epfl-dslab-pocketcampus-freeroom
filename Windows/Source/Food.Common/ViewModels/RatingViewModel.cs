// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The ViewModel that lets the user rate meals.
    /// </summary>
    [PageLogId( "/food/rating" )]
    public sealed class RatingViewModel : DataViewModel<RatingInfo>
    {
        private const int MinRatingHour = 11;

        private readonly IFoodService _foodService;
        private readonly IPluginSettings _pluginSettings;
        private readonly INavigationService _navigationService;
        private readonly IDeviceIdentifier _deviceIdentifier;

        private RatingStatus _status;
        private UserRating _rating;

        private MealTime _mealTime;

        /// <summary>
        /// Gets the meal to be rated.
        /// </summary>
        public Meal Meal { get; private set; }

        /// <summary>
        /// Gets the rating status.
        /// </summary>
        public RatingStatus Status
        {
            get { return _status; }
            private set { SetProperty( ref _status, value ); }
        }

        /// <summary>
        /// Gets or sets the user's rating.
        /// </summary>
        public UserRating Rating
        {
            get { return _rating; }
            set { SetProperty( ref _rating, value ); }
        }

        /// <summary>
        /// Gets the command executed to rate the meal.
        /// </summary>
        [CommandLogId( "Rate" )]
        public Command RateCommand
        {
            get { return GetCommand( Rate, () => Status == RatingStatus.Ok && !IsLoading ); }
        }


        /// <summary>
        /// Creates a new RatingViewModel.
        /// </summary>
        public RatingViewModel( IFoodService foodService, IPluginSettings pluginSettings,
                                INavigationService navigationService, IDeviceIdentifier deviceIdentifier,
                                RatingInfo info )
        {
            _foodService = foodService;
            _pluginSettings = pluginSettings;
            _navigationService = navigationService;
            _deviceIdentifier = deviceIdentifier;

            Rating = UserRating.Neutral;

            Meal = info.Meal;
            _mealTime = info.MealTime;

            if ( DateTime.Now.Hour < MinRatingHour )
            {
                Status = RatingStatus.TooEarly;
            }
            else if ( DateTime.Now - _pluginSettings.LastVotes[info.MealTime] < TimeSpan.FromDays( 1 ) )
            {
                Status = RatingStatus.AlreadyVotedToday;
            }
            else if ( DateTime.Now - info.MealDate > TimeSpan.FromDays( 1 ) )
            {
                Status = RatingStatus.MealFromThePast;
            }
            else if ( info.MealDate - DateTime.Now > TimeSpan.FromDays( 1 ) )
            {
                Status = RatingStatus.MealFromTheFuture;
            }
            else
            {
                Status = RatingStatus.Ok;
            }
        }

        /// <summary>
        /// Rates the meal.
        /// </summary>
        private async void Rate()
        {
            await TryExecuteAsync( async _ =>
            {
                double value = Rating == UserRating.Good ? 1.0
                             : Rating == UserRating.Neutral ? 0.5
                                                         : 0.0;

                var request = new VoteRequest { DeviceId = _deviceIdentifier.Current, MealId = Meal.Id, RatingValue = value };
                var response = await _foodService.VoteAsync( request );

                if ( response.Status == VoteStatus.AlreadyVoted )
                {
                    Status = RatingStatus.AlreadyVotedToday;
                }
                else if ( response.Status == VoteStatus.TooEarly )
                {
                    Status = RatingStatus.TooEarly;
                }
                else
                {
                    Meal.Rating = UpdateRating( Meal.Rating, value );
                    Meal.Restaurant.Rating = UpdateRating( Meal.Restaurant.Rating, value );

                    _pluginSettings.LastVotes[_mealTime] = DateTime.Now;
                    _navigationService.NavigateBack();
                }
            } );
        }

        /// <summary>
        /// Creates a new rating by adding a vote to an existing rating.
        /// </summary>
        private static Rating UpdateRating( Rating rating, double vote )
        {
            return new Rating
            {
                Value = ( rating.Value * rating.VoteCount + vote ) / ( rating.VoteCount + 1 ),
                VoteCount = rating.VoteCount + 1
            };
        }
    }
}