using System;
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
            return LocalizationHelper.GetLoaderForCurrentAssembly( value.GetType().Name ).GetString( value.ToString() );
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
}