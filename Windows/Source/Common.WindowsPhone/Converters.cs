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

        public abstract TTo Convert( TFrom value );
        public virtual TFrom ConvertBack( TTo value ) { throw new NotSupportedException(); }
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

        public override string Convert( Enum value )
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
    /// Converts an e-mail sending status to a visibility, for a "request e-mail" button: if it hasn't been requested, it's visible.
    /// </summary>
    public sealed class EmailNotRequestedToVisibilityConverter : ValueConverter<EmailSendingStatus, Visibility>
    {
        public override Visibility Convert( EmailSendingStatus value )
        {
            return value == EmailSendingStatus.NoneRequested ? Visibility.Visible : Visibility.Collapsed;
        }
    }

    /// <summary>
    /// Converts an e-mail sending status to a boolean, for the "request e-mail" button: true if it has been requested, false otherwise.
    /// </summary>
    public sealed class EmailRequestedToBooleanConverter : ValueConverter<EmailSendingStatus, bool>
    {
        public override bool Convert( EmailSendingStatus value )
        {
            return value != EmailSendingStatus.NoneRequested;
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
        public override string Convert( double value )
        {
            return value.ToString( NumberFormatInfo.InvariantInfo );
        }

        public override double ConvertBack( string value )
        {
            return double.Parse( value, NumberFormatInfo.InvariantInfo );
        }
    }

    /// <summary>
    /// Converts an enum value to an image, formatted as "{EnumName}_{Value}.png".
    /// </summary>
    public sealed class EnumToImageSourceConverter : ValueConverter<Enum, ImageSource>
    {
        public override ImageSource Convert( Enum value )
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
        public override bool Convert( GeoLocationStatus value )
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

        public override Visibility Convert( bool value )
        {
            return ( IsReversed ? !value : value ) ? Visibility.Visible : Visibility.Collapsed;
        }

        public override bool ConvertBack( Visibility value )
        {
            return IsReversed ? value == Visibility.Collapsed : value == Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts strings to visibilities; null/whitespace -> Collapsed; anything else -> Visible.
    /// </summary>
    public sealed class StringToVisibilityConverter : ValueConverter<string, Visibility>
    {
        public bool IsReversed { get; set; }

        public override Visibility Convert( string value )
        {
            bool isEmpty = string.IsNullOrWhiteSpace( value );
            return ( IsReversed ? !isEmpty : isEmpty ) ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts objects to visibilities; null -> Collapsed, anything else -> Visible.
    /// </summary>
    public sealed class NonNullToVisibilityConverter : ValueConverter<object, Visibility>
    {
        public override Visibility Convert( object value )
        {
            return value == null ? Visibility.Collapsed : Visibility.Visible;
        }
    }

    /// <summary>
    /// Converts integers to visibilities; 0 -> Collapsed, anything else -> Visible.
    /// </summary>
    public sealed class IntegerToVisibilityConverter : ValueConverter<int, Visibility>
    {
        public override Visibility Convert( int value )
        {
            return value == 0 ? Visibility.Collapsed : Visibility.Visible;
        }
    }
}