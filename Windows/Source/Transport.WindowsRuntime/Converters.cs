// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Transport.Models;
using Windows.ApplicationModel.Resources;

namespace PocketCampus.Transport
{
    // N.B.: This won't work with IEnumerables of value types, but we don't need it for now
    public sealed class TakeNConverter : ValueConverter<IEnumerable<object>, IEnumerable<object>>
    {
        public int Count { get; set; }

        public override IEnumerable<object> Convert( IEnumerable<object> value )
        {
            if ( value == null )
            {
                return Enumerable.Empty<object>();
            }

            return value.Take( Count );
        }
    }

    // TODO use DateTimeOffset everywhere (Thrift# included)
    public sealed class DateFormatConverter : ValueConverter<DateTime, string>
    {
        private const int NegativeThreshold = -5;
        private const int Threshold = 20;

        private readonly ResourceLoader _loader = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Transport.WindowsRuntime/Date" );

        // for absolute dates
        public string Format { get; set; }

        public override string Convert( DateTime value )
        {
            int mins = (int) ( value.Subtract( DateTime.Now ) ).TotalMinutes;
            if ( mins == 0 || ( mins < 0 && mins > NegativeThreshold ) )
            {
                return _loader.GetString( "Now" );
            }
            if ( mins > 0 && mins < Threshold )
            {
                return string.Format( _loader.GetString( "MinutesFormat" ), mins );
            }
            return value.ToString( Format );
        }
    }

    public sealed class ConnectionToLineSymbolConverter : ValueConverter<Connection, string>
    {
        public override string Convert( Connection value )
        {
            if ( value.IsOnFoot )
            {
                return "🚶";
            }
            return value.Line.Name;
        }
    }
}