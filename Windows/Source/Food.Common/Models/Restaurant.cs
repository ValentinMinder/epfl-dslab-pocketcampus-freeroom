// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using PocketCampus.Map.Models;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// A restaurant on the EPFL campus, which may be a cafeteria, a take-away or an actual restaurant.
    /// </summary>
    [ThriftStruct( "EpflRestaurant" )]
    public sealed class Restaurant
    {
        /// <summary>
        /// The restaurant's ID.
        /// </summary>
        [ThriftField( 1, true, "rId" )]
        public long Id { get; set; }

        /// <summary>
        /// The restaurant's name.
        /// </summary>
        [ThriftField( 2, true, "rName" )]
        public string Name { get; set; }

        /// <summary>
        /// The meals available at the restaurant.
        /// </summary>
        [ThriftField( 3, true, "rMeals" )]
        public Meal[] Meals { get; set; }

        /// <summary>
        /// The restaurant's location on the map.
        /// </summary>
        [ThriftField( 4, false, "rLocation" )]
        public MapItem MapItem { get; set; }

        /// <summary>
        /// The restaurant's rating.
        /// </summary>
        [ThriftField( 5, true, "rRating" )]
        public Rating Rating { get; set; }


        /// <summary>
        /// Creates a copy of the restaurant, replacing the meals with the specified ones.
        /// </summary>
        public Restaurant WithMeals( IEnumerable<Meal> meals )
        {
            return new Restaurant
            {
                Id = this.Id,
                Name = this.Name,
                Meals = meals.ToArray(),
                MapItem = this.MapItem,
                Rating = this.Rating
            };
        }
    }
}