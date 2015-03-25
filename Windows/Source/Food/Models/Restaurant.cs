// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using PocketCampus.Map.Models;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "EpflRestaurant" )]
    public sealed class Restaurant
    {
        [ThriftField( 1, true, "rId" )]
        public long Id { get; set; }

        [ThriftField( 2, true, "rName" )]
        public string Name { get; set; }

        [ThriftField( 3, true, "rMeals" )]
        public Meal[] Meals { get; set; }

        [ThriftField( 4, false, "rLocation" )]
        public MapItem MapItem { get; set; }

        [ThriftField( 5, true, "rRating" )]
        public Rating Rating { get; set; }


        public Restaurant CopyWithMeals( IEnumerable<Meal> meals )
        {
            return new Restaurant
            {
                Id = Id,
                Name = Name,
                Meals = meals.ToArray(),
                MapItem = MapItem,
                Rating = Rating
            };
        }
    }
}