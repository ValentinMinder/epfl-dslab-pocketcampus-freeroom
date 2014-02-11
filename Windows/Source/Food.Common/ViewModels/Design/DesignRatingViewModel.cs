// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Windows.Input;
using PocketCampus.Food.Models;
using PocketCampus.Mvvm;

// Design data for RatingViewModel

namespace PocketCampus.Food.ViewModels.Design
{
    public sealed class DesignRatingViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public Meal Meal
        {
            get
            {
                return new Meal
                {
                    Name = "Suprême de poulet (CH) sauce curry jaune",
                    Description = @"Jardinière de légumes
Riz parfumé
Buffet de salades",
                    MealTypes = new[] { MealType.Thai },
                    Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 9.50 },
                                                                 { PriceTarget.Visitor, 12.00 },
                                                                 { PriceTarget.PhDStudent, 9.50 },
                                                                 { PriceTarget.Staff, 9.50 }},
                    HalfPortionPrice = 7.50,
                    Rating = new Rating { Value = 1.00, VoteCount = 1 },
                    Restaurant = new Restaurant
                    {
                        Name = "Atlantide",
                        Rating = new Rating { Value = 0.75, VoteCount = 2 },
                    }
                };
            }
        }

        public RatingStatus Status
        {
            get { return RatingStatus.AlreadyVotedToday; }
        }

        public UserRating Rating
        {
            get { return UserRating.Good; }
        }

        public ICommand VoteCommand
        {
            get { return new Command( this, () => { }, () => Status == RatingStatus.Ok ); }
        }
#endif
    }
}