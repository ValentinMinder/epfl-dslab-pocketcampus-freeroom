// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftEnum]
    public enum PriceTarget
    {
        Student = 1,
        PhDStudent = 2,
        Staff = 3,
        Visitor = 4,
        All = 5
    }
}