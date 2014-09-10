using System;
using System.Collections;
using System.Diagnostics;
using System.Linq;
using Windows.UI.Xaml;

namespace PocketCampus.Common
{
    // HACK. TODO this should be deleted once the bug with Dictionary<K,V> binding is fixed
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

        private sealed class KeyValuePair
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

    // TODO sort this mess of converters, make some more generic ones
    public sealed class StringFormatConverter : ValueConverter<object, string, string>
    {
        public override string Convert( object value, string parameter )
        {
            return string.Format( parameter.ToString(), value );
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

    public sealed class BoolToVisibilityConverter : ValueConverter<bool, Visibility>
    {
        public bool IsReversed { get; set; }

        public override Visibility Convert( bool value )
        {
            if ( IsReversed )
            {
                return value ? Visibility.Collapsed : Visibility.Visible;
            }

            return value ? Visibility.Visible : Visibility.Collapsed;
        }
    }

    public sealed class EnumToVisibilityConverter : ValueConverter<Enum, string, Visibility>
    {
        public override Visibility Convert( Enum value, string parameter )
        {
            // for some reason, == returns false on equal enums...
            // TODO: Investigate.
            return object.Equals( value, Enum.Parse( value.GetType(), parameter ) ) ? Visibility.Visible : Visibility.Collapsed;
        }
    }

    public sealed class NoItemsToVisibilityConverter : ValueConverter<IEnumerable, Visibility>
    {
        public override Visibility Convert( IEnumerable value )
        {
            if ( value == null )
            {
                // this will be used to show "no items" messages, if items is null then it hasn't been populated yet
                return Visibility.Collapsed;
            }

            return value.GetEnumerator().MoveNext() ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    public sealed class StringToVisibilityConverter : ValueConverter<string, Visibility>
    {
        public bool IsReversed { get; set; }

        public override Visibility Convert( string value )
        {
            if ( IsReversed )
            {
                return string.IsNullOrWhiteSpace( value ) ? Visibility.Visible : Visibility.Collapsed;
            }

            return string.IsNullOrWhiteSpace( value ) ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    public sealed class NonNullToVisibilityConverter : ValueConverter<object, Visibility>
    {
        public override Visibility Convert( object value )
        {
            return value == null ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    public sealed class NonZeroToVisibilityConverter : ValueConverter<int, Visibility>
    {
        public override Visibility Convert( int value )
        {
            return value == 0 ? Visibility.Collapsed : Visibility.Visible;
        }
    }
}