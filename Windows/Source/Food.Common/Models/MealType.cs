// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The types of meals.
    /// </summary>
    [ThriftEnum]
    public enum MealType
    {
        /// <summary>
        /// Unknown type.
        /// This is used when data is missing, or a bug occurred somewhere.
        /// </summary>
        Unknown = 1,

        /// <summary>
        /// Fish.
        /// </summary>
        Fish = 2,

        /// <summary>
        /// Meat.
        /// </summary>
        Meat = 3,

        /// <summary>
        /// Poultry, e.g. chicken or turkey.
        /// </summary>
        Poultry = 4,

        /// <summary>
        /// Vegetarian.
        /// </summary>
        Vegetarian = 5,

        /// <summary>
        /// "Green Fork" label.
        /// Meals with this type are healthy and balanced.
        /// </summary>
        GreenFork = 6,

        /// <summary>
        /// Pasta.
        /// </summary>
        Pasta = 7,

        /// <summary>
        /// Pizza. (including e.g. röstipizza)
        /// </summary>
        Pizza = 8,

        /// <summary>
        /// Thai.
        /// </summary>
        Thai = 9,

        /// <summary>
        /// Indian.
        /// </summary>
        Indian = 10,

        /// <summary>
        /// Lebanese.
        /// </summary>
        Lebanese = 11
    }
}