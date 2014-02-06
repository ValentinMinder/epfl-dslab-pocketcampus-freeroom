// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Price targets for meals.
    /// </summary>
    [ThriftEnum( "PriceTarget" )]
    public enum PriceTarget
    {
        /// <summary>
        /// Students. (Bachelor or Master)
        /// </summary>
        [ThriftEnumMember( "STUDENT", 1 )]
        Student,

        /// <summary>
        /// PhD students. (not postdocs)
        /// </summary>
        [ThriftEnumMember( "PHD_STUDENT", 2 )]
        PhDStudent,

        /// <summary>
        /// Staff (e.g. professors, secretaries)
        /// </summary>
        [ThriftEnumMember( "STAFF", 3 )]
        Staff,

        /// <summary>
        /// Visitors.
        /// </summary>
        [ThriftEnumMember( "VISITOR", 4 )]
        Visitor,

        /// <summary>
        /// All.
        /// This is generally used on its own, implying that there is no subsidy.
        /// </summary>
        [ThriftEnumMember( "ALL", 5 )]
        All
    }
}