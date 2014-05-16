// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Price targets for meals.
    /// </summary>
    [ThriftEnum]
    public enum PriceTarget
    {
        /// <summary>
        /// Students. (Bachelor or Master)
        /// </summary>
        Student = 1,

        /// <summary>
        /// PhD students. (not postdocs)
        /// </summary>
        PhDStudent = 2,

        /// <summary>
        /// Staff.
        /// </summary>
        Staff = 3,

        /// <summary>
        /// Visitors.
        /// </summary>
        Visitor = 4,

        /// <summary>
        /// All.
        /// This is generally used on its own, implying that there is no subsidy.
        /// </summary>
        All = 5
    }
}