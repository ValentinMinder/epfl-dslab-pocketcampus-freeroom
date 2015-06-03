// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    [LogId( "/food/rating" )]
    public sealed class RatingViewModel : DataViewModel<Meal>
    {
        private readonly IFoodService _foodService;
        private readonly INavigationService _navigationService;
        private readonly IDeviceIdentifier _deviceIdentifier;

        private VoteStatus _status;
        private UserRating _rating;


        public Meal Meal { get; private set; }

        public VoteStatus Status
        {
            get { return _status; }
            private set { SetProperty( ref _status, value ); }
        }

        public UserRating Rating
        {
            get { return _rating; }
            set { SetProperty( ref _rating, value ); }
        }

        [LogId( "Rate" )]
        public Command RateCommand
        {
            get { return this.GetCommand( Rate, () => DataStatus != DataStatus.Loading ); }
        }


        public RatingViewModel( IFoodService foodService, INavigationService navigationService, IDeviceIdentifier deviceIdentifier,
                                Meal meal )
        {
            _foodService = foodService;
            _navigationService = navigationService;
            _deviceIdentifier = deviceIdentifier;

            Meal = meal;
            Rating = UserRating.Neutral;
            Status = VoteStatus.Success;
        }


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
                    Meal.Rating.AddVote( value );
                    Meal.Restaurant.Rating.AddVote( value );

                    _navigationService.NavigateBack();
                }
                else
                {
                    Status = response.Status;
                }
            } );
        }
    }
}