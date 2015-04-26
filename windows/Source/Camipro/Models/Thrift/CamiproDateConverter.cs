// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using ThriftSharp;

namespace PocketCampus.Camipro.Models.Thrift
{
    /// <summary>
    /// Converts DateTimes from strings in the format the server uses.
    /// </summary>
    public sealed class CamiproDateConverter : ThriftValueConverter<string, DateTime>
    {
        private const string Format = @"dd.MM.yy HH\hmm";

        protected override DateTime Convert( string value )
        {
            return DateTime.ParseExact( value, Format, CultureInfo.InvariantCulture );
        }

        protected override string ConvertBack( DateTime value )
        {
            return value.ToString( Format, CultureInfo.InvariantCulture );
        }
    }
}