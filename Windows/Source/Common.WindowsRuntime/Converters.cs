// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections;
using System.Diagnostics;
using System.Linq;
using System.Reflection;
using Windows.UI.Xaml;

namespace PocketCampus.Common
{
    // FRAMEWORK BUG: Bindings to Dictionary<K,V> don't work.
    //    Workaround: This converts them to a custom class with which bindings work.
    public sealed class DictionaryFixer : ValueConverter<IDictionary, IEnumerable>
    {
        public override IEnumerable Convert( IDictionary value )
        {
            if ( value == null )
            {
                return Enumerable.Empty<KeyValuePair>();
            }
            return value.Cast<dynamic>().Select( d => new KeyValuePair( d.Key, d.Value ) ).ToArray();
        }

        public sealed class KeyValuePair
        {
            public object Key { get; private set; }
            public object Value { get; private set; }

            public KeyValuePair( object key, object value )
            {
                Key = key;
                Value = value;
            }
        }
    }

    // The parameter can be either a full format string, or only the format part.
    public sealed class StringFormatConverter : ValueConverter<object, string, string>
    {
        public override string Convert( object value, string parameter )
        {
            if ( parameter.Contains( ( "{0" ) ) )
            {
                return string.Format( parameter, value );
            }

            return string.Format( "{0:" + parameter + "}", value );
        }
    }

    public sealed class MoneyFormatConverter : ValueConverter<object, string>
    {
        public override string Convert( object value )
        {
            if ( value == null )
            {
                return ""; // for nullable prices
            }

            return string.Format( "{0:0.00} CHF", value );
        }
    }

    public sealed class DateToWeekStringConverter : ValueConverter<DateTime, string>
    {
        private const string DifferentMonthFormat = "{0:M} - {1:M}";

        // There's no standard way to display a localized version of "December 2 - 8", since order can vary (e.g. French is "2 - 8 décembre")
        // Simple workaround: Find the number.
        // The percent is necessary for 'd' to be interpreted as 'day between 1 and 31' instead of 'standard date format'.
        private static readonly string SameMonthFormat = DateTime.Now.ToString( "M" ).Split( ' ' )[0].Any( char.IsDigit ) ?
                                                         "{0:%d} - {1:M}"
                                                       : "{0:M} - {1:%d}";

        public override string Convert( DateTime value )
        {
            var end = value.AddDays( 6 );
            string format = value.Month == end.Month ? SameMonthFormat : DifferentMonthFormat;
            return string.Format( format, value, end );
        }
    }

    public sealed class EnumToValuesConverter : ValueConverter<Enum, Array>
    {
        public override Array Convert( Enum value )
        {
            return Enum.GetValues( value.GetType() );
        }
    }

    public sealed class EnumToStringConverter : ValueConverter<Enum, string>
    {
        public override string Convert( Enum value )
        {
            string enumName = value.GetType().Name;
            string val = value.ToString();

            try
            {
                return LocalizationHelper.GetLoaderForCurrentAssembly( enumName ).GetString( val );
            }
            catch
            {
                // Happens when a binding in page A is updated after a navigation to page B.
                Debug.WriteLine( "EnumToStringConverter: " + enumName + "." + val + " not found." );
                return "";
            }
        }
    }

    public sealed class DefaultToVisibilityConverter : ValueConverter<object, Visibility>
    {
        public bool IsReversed { get; set; }

        public override Visibility Convert( object value )
        {
            return IsDefaultValue( value ) ^ IsReversed ? Visibility.Visible : Visibility.Collapsed;
        }

        private static bool IsDefaultValue( object value )
        {
            if ( value == null )
            {
                return true;
            }

            var str = value as string;
            if ( str != null && str.Trim().Length == 0 )
            {
                return true;
            }

            var collection = value as IEnumerable;
            if ( collection != null )
            {
                return !collection.GetEnumerator().MoveNext();
            }

            var type = value.GetType();
            if ( type.GetTypeInfo().IsValueType )
            {
                return object.Equals( value, Activator.CreateInstance( type ) );
            }

            // not null, not a value type
            return false;
        }
    }
}