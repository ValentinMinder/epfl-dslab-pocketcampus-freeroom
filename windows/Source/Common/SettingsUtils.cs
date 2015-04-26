// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;

namespace PocketCampus.Common
{
    /// <summary>
    /// Utility class for settings.
    /// </summary>
    public static class SettingsUtils
    {
        /// <summary>
        /// Gets (enum value, is selected) pairs from a list of enum values and optional exclusions.
        /// </summary>
        public static Pair<T, bool>[] GetEnumPairs<T>( ICollection<T> values, params T[] excluded )
            where T : struct
        {
            return EnumEx.GetValues<T>()
                         .Where( val => !excluded.Contains( val ) )
                         .Select( val => Pair.Create( val, ( values ).Contains( val ) ) )
                         .ToArray();
        }

        /// <summary>
        /// Inverse of the GetEnumPairs method; gets an array of enum values from the pairs indicating whether they should be in it.
        /// </summary>
        public static T[] GetEnumList<T>( IEnumerable<Pair<T, bool>> pairs )
            where T : struct
        {
            return pairs.Where( pair => pair.Item2 )
                        .Select( pair => pair.Item1 )
                        .ToArray();
        }
    }
}