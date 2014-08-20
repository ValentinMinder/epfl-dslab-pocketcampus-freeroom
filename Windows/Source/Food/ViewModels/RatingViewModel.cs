// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The ViewModel that lets the user rate meals.
    /// </summary>
    [LogId( "/food/rating" )]
    public sealed class RatingViewModel : DataViewModel<Meal>
    {
        private const int MinRatingHour = 11;

        private readonly IFoodService _foodService;
        private readonly IPluginSettings _pluginSettings;
        private readonly INavigationService _navigationService;
        private readonly IDeviceIdentifier _deviceIdentifier;

        private VoteStatus _status;
        private UserRating _rating;

        /// <summary>
        /// Gets the meal to be rated.
        /// </summary>
        public Meal Meal { get; private set; }

        /// <summary>
        /// Gets the vote status.
        /// </summary>
        public VoteStatus Status
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
        [LogId( "Rate" )]
        public Command RateCommand
        {
            get { return this.GetCommand( Rate, () => DataStatus != DataStatus.Loading ); }
        }


        /// <summary>
        /// Creates a new RatingViewModel.
        /// </summary>
        public RatingViewModel( IFoodService foodService, IPluginSettings pluginSettings,
                                INavigationService navigationService, IDeviceIdentifier deviceIdentifier,
                                Meal meal )
        {
            _foodService = foodService;
            _pluginSettings = pluginSettings;
            _navigationService = navigationService;
            _deviceIdentifier = deviceIdentifier;

            Meal = meal;
            Rating = UserRating.Neutral;
            Status = VoteStatus.Success;
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

                if ( response.Status == VoteStatus.Success )
                {
                    Meal.Rating = UpdateRating( Meal.Rating, value );
                    Meal.Restaurant.Rating = UpdateRating( Meal.Restaurant.Rating, value );

                    _navigationService.NavigateBack();
                }
                else
                {
                    Status = response.Status;
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