// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftEnum]
    public enum FoodStatus
    {
        Success = 200,
        NetworkError = 404
    }
}