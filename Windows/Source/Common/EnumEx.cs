// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;

namespace PocketCampus.Common
{
    /// <summary>
    /// Utility class for enums.
    /// </summary>
    public static class EnumEx
    {
        /// <summary>
        /// A generic version of Enum.GetValues(Type).
        /// </summary>
        public static T[] GetValues<T>()
            where T : struct
        {
            return Enum.GetValues( typeof( T ) ).Cast<T>().ToArray();
        }
    }
}