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

    public sealed class StringFormatConverter : ValueConverter<object, string, string>
    {
        public override string Convert( object value, string parameter )
        {
            return string.Format( "{0:" + parameter.ToString() + "}", value );
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

    public sealed class DefaultToVisibilityConverter : ValueConverter<object, Visibility>
    {
        public bool IsReversed { get; set; }

        public override Visibility Convert( object value )
        {
            // kind of hack-y... but it works; Collapsed if non-reversed and default or reversed and non-default, Visible otherwise.
            return ( value == Activator.CreateInstance( value.GetType() ) ^ IsReversed ) ? Visibility.Collapsed : Visibility.Visible;
        }
    }
}