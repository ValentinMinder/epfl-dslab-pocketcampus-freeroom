using System;
using System.Diagnostics;
using System.Globalization;
using Windows.UI.Xaml;

namespace PocketCampus.Common
{
    public sealed class StringFormatConverter : ValueConverter<object, string, string>
    {
        public override string Convert( object value, string parameter )
        {
            return string.Format( parameter.ToString(), value );
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

    public sealed class EnumToVisibilityConverter : ValueConverter<Enum, string, Visibility>
    {
        public override Visibility Convert( Enum value, string parameter )
        {
            // for some reason, == returns false on equal enums...
            // TODO: Investigate.
            return object.Equals( value, Enum.Parse( value.GetType(), parameter ) ) ? Visibility.Visible : Visibility.Collapsed;
        }
    }

    public sealed class CurrencyFormatter : ValueConverter<double, string>
    {
        public override string Convert( double value )
        {
            var format = (NumberFormatInfo) CultureInfo.CurrentUICulture.NumberFormat.Clone();
            format.CurrencySymbol = "CHF";
            // C = currency, 2 = decimal places
            return value.ToString( "C2", format );
        }
    }
}