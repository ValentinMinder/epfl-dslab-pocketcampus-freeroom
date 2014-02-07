// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Resources;
using System.Windows;
using System.Windows.Data;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace PocketCampus.Common
{
    /// <summary>
    /// Base converter class that adds type safety and reduces the number of parameters
    /// (since most converters don't use them).
    /// </summary>
    public abstract class ValueConverter<TFrom, TTo> : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, CultureInfo culture )
        {
            return Convert( (TFrom) value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, CultureInfo culture )
        {
            return ConvertBack( (TTo) value );
        }

        protected abstract TTo Convert( TFrom value );
        protected virtual TFrom ConvertBack( TTo value ) { throw new NotSupportedException(); }
    }


    /// <summary>
    /// Converts enums to localized strings.
    /// </summary>
    public sealed class EnumToLocalizedStringConverter : ValueConverter<Enum, string>
    {
        /// <summary>
        /// An object with a Resources property returning resources.
        /// </summary>
        /// <remarks>
        /// Yes, this is a terrible way of doing it, but getting the resources directly requires binding
        /// which requires a DependencyObject, which is not shareable, which causes all kinds of problems
        /// when included in a XAML resource dictionary.
        /// </remarks>
        public dynamic Strings { get; set; }

        private ResourceManager _manager;

        protected override string Convert( Enum value )
        {
            if ( _manager == null )
            {
                var res = Strings.Resources;
                _manager = new ResourceManager( res.GetType() );
            }

            string enumName = value.GetType().Name;
            return _manager.GetString( enumName + "_" + value );
        }
    }

    /// <summary>
    /// Converts doubles to strings and vice-versa.
    /// </summary>
    /// <remarks>
    /// Needed because WP apparently can't do it; it probably attempts to use the
    /// current culture even though the keyboard uses a '.' as its decimal separator.
    /// </remarks>
    public sealed class DoubleToStringConverter : ValueConverter<double, string>
    {
        protected override string Convert( double value )
        {
            return value.ToString( NumberFormatInfo.InvariantInfo );
        }

        protected override double ConvertBack( string value )
        {
            return double.Parse( value, NumberFormatInfo.InvariantInfo );
        }
    }

    /// <summary>
    /// Converts an enum value to an image, formatted as "{EnumName}_{Value}.png".
    /// </summary>
    public sealed class EnumToImageSourceConverter : ValueConverter<Enum, ImageSource>
    {
        protected override ImageSource Convert( Enum value )
        {
            string enumName = value.GetType().Name;
            string uriString = string.Format( "/Assets/{0}_{1}.png", enumName, value.ToString() );
            return new BitmapImage( new Uri( uriString, UriKind.Relative ) );
        }
    }

    /// <summary>
    /// Converts a GeoLocationStatus to a boolean indicating whether an error prompt should be displayed.
    /// </summary>
    public sealed class GeoLocationStatusToErrorBooleanConverter : ValueConverter<GeoLocationStatus, bool>
    {
        protected override bool Convert( GeoLocationStatus value )
        {
            return value == GeoLocationStatus.Error;
        }
    }

    /// <summary>
    /// Converts a booleans to visibilities; true -> Visible and false -> Collapsed.
    /// </summary>
    public sealed class BooleanToVisibilityConverter : ValueConverter<bool, Visibility>
    {
        public bool IsReversed { get; set; }

        protected override Visibility Convert( bool value )
        {
            return ( IsReversed ? !value : value ) ? Visibility.Visible : Visibility.Collapsed;
        }

        protected override bool ConvertBack( Visibility value )
        {
            return IsReversed ? value == Visibility.Collapsed : value == Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts strings to visibilities; null/whitespace -> Collapsed; anything else -> Visible.
    /// </summary>
    public sealed class StringToVisibilityConverter : ValueConverter<string, Visibility>
    {
        protected override Visibility Convert( string value )
        {
            return string.IsNullOrWhiteSpace( value ) ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts objects to visibilities; null -> Collapsed, anything else -> Visible.
    /// </summary>
    public sealed class NonNullToVisibilityConverter : ValueConverter<object, Visibility>
    {
        protected override Visibility Convert( object value )
        {
            return value == null ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts integers to visibilities; 0 -> Collapsed, anything else -> Visible.
    /// </summary>
    public sealed class IntegerToVisibilityConverter : ValueConverter<int, Visibility>
    {
        protected override Visibility Convert( int value )
        {
            return value == 0 ? Visibility.Collapsed : Visibility.Visible;
        }
    }
}