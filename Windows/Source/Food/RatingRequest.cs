// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Food.Models;

namespace PocketCampus.Food
{
    public sealed class RatingRequest
    {
        public Meal Meal { get; private set; }

        public MealTime MealTime { get; private set; }

        public DateTime MealDate { get; private set; }


        public RatingRequest( Meal meal, MealTime mealTime, DateTime mealDate )
        {
            Meal = meal;
            MealTime = mealTime;
            MealDate = mealDate;
        }
    }
}