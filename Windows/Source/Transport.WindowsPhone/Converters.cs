// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Transport.Resources;

namespace PocketCampus.Transport
{
    /// <summary>
    /// Converts a date to a human-readable, relative time string.
    /// </summary>
    public sealed class DateToStringConverter : ValueConverter<DateTime, string>
    {
        private const int NegativeThreshold = -5;
        private const int Threshold = 20;

        /// <summary>
        /// Gets or sets the date format for absolute dates.
        /// </summary>
        public string Format { get; set; }

        public override string Convert( DateTime value )
        {
            int mins = (int) ( value.Subtract( DateTime.Now ) ).TotalMinutes;
            if ( mins == 0 || ( mins < 0 && mins > NegativeThreshold ) )
            {
                return PluginResources.DateNow;
            }
            if ( mins > 0 && mins < Threshold )
            {
                return string.Format( PluginResources.DateMinutes, mins );
            }
            return value.ToString( Format );
        }
    }

    /// <summary>
    /// Takes the first N elements of a sequence.
    /// </summary>
    public sealed class TakeNConverter : ValueConverter<IEnumerable<object>, IEnumerable<object>>
    {
        /// <summary>
        /// Gets or sets the number of elements to be taken.
        /// </summary>
        public int Count { get; set; }

        public override IEnumerable<object> Convert( IEnumerable<object> value )
        {
            return value == null ? Enumerable.Empty<object>() : value.Take( Count );
        }
    }
}