// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Food.Models;
using PocketCampus.Map.Models;

// Plumbing to show restaurants in LongListSelectors

namespace PocketCampus.Food
{
    public sealed class RestaurantAsGroup : List<Meal>
    {
        public string Name { get; set; }

        public MapItem MapItem { get; set; }

        public RestaurantAsGroup( Restaurant restaurant )
            : base( restaurant.Meals )
        {
            Name = restaurant.Name;
            MapItem = restaurant.MapItem;
        }
    }
}