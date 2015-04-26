// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftEnum]
    public enum MealType
    {
        Unknown = 1,
        Fish = 2,
        Meat = 3,
        Poultry = 4,
        Vegetarian = 5,
        GreenFork = 6,
        Pasta = 7,
        Pizza = 8,
        Thai = 9,
        Indian = 10,
        Lebanese = 11
    }
}