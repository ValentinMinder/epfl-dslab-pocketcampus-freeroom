// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The types of meals.
    /// </summary>
    [ThriftEnum( "MealType" )]
    public enum MealTypes
    {
        /// <summary>
        /// Unknown type.
        /// This is used when data is missing, or a bug occurred somewhere.
        /// </summary>
        [ThriftEnumMember( "UNKNOWN", 1 )]
        Unknown,

        /// <summary>
        /// Fish.
        /// </summary>
        [ThriftEnumMember( "FISH", 2 )]
        Fish,

        /// <summary>
        /// Meat.
        /// </summary>
        [ThriftEnumMember( "MEAT", 3 )]
        Meat,

        /// <summary>
        /// Poultry, e.g. chicken or turkey.
        /// </summary>
        [ThriftEnumMember( "POULTRY", 4 )]
        Poultry,

        /// <summary>
        /// Vegetarian.
        /// </summary>
        [ThriftEnumMember( "VEGETARIAN", 5 )]
        Vegetarian,

        /// <summary>
        /// "Green Fork" label.
        /// Meals with this type are healthy and balanced.
        /// </summary>
        [ThriftEnumMember( "GREEN_FORK", 6 )]
        GreenFork,

        /// <summary>
        /// Pasta.
        /// </summary>
        [ThriftEnumMember( "PASTA", 7 )]
        Pasta,

        /// <summary>
        /// Pizza. (including various forms of vegetarian pizza, e.g. röstipizza)
        /// </summary>
        [ThriftEnumMember( "PIZZA", 8 )]
        Pizza,

        /// <summary>
        /// Thai.
        /// </summary>
        [ThriftEnumMember( "THAI", 9 )]
        Thai,

        /// <summary>
        /// Indian.
        /// </summary>
        [ThriftEnumMember( "INDIAN", 10 )]
        Indian,

        /// <summary>
        /// Lebanese.
        /// </summary>
        [ThriftEnumMember( "LEBANESE", 11 )]
        Lebanese
    }
}