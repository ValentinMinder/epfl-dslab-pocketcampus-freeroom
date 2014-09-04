// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Windows;
using System.Windows.Data;
using System.Windows.Media;
using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Food.Resources;
using PocketCampus.Food.Services;

namespace PocketCampus.Food
{
    /// <summary>
    /// Converts a price to a display-friendly version.
    /// </summary>
    public sealed class PriceToStringConverter : ValueConverter<double?, string>
    {
        private const string IntegerPriceFormat = "{0}.- CHF";
        private const string DecimalPriceFormat = "{0:0.00} CHF";

        public override string Convert( double? value )
        {
            if ( value == null || value == 0.0 )
            {
                return PluginResources.UnknownPrice;
            }
            if ( Math.Round( (double) value ) == value )
            {
                return string.Format( CultureInfo.InvariantCulture, IntegerPriceFormat, value );
            }
            return string.Format( CultureInfo.InvariantCulture, DecimalPriceFormat, value );
        }
    }

    /// <summary>
    /// Converts meals to their price according to the current settings.
    /// </summary>
    /// <remarks>
    /// Since it needs IPluginSettings, it can't be a generic ValueConverter.
    /// </remarks>
    public sealed class MealPriceConverter : DependencyObject, IValueConverter
    {
        private PriceToStringConverter _priceToString = new PriceToStringConverter();

        public IPluginSettings Settings
        {
            get { return (IPluginSettings) GetValue( SettingsProperty ); }
            set { SetValue( SettingsProperty, value ); }
        }

        public static readonly DependencyProperty SettingsProperty =
            DependencyProperty.Register( "Settings", typeof( IPluginSettings ), typeof( MealPriceConverter ), new PropertyMetadata( null ) );

        public object Convert( object value, Type targetType, object parameter, CultureInfo culture )
        {
            var meal = (Meal) value;

            // Settings == null in design mode for some reason
            var target = Settings == null ? PriceTarget.All : Settings.PriceTarget;
            return _priceToString.Convert( meal.GetPrice( target ) );
        }

        public object ConvertBack( object value, Type targetType, object parameter, CultureInfo culture )
        {
            throw new NotSupportedException();
        }
    }

    /// <summary>
    /// Converts meals to images of their main type.
    /// </summary>
    public sealed class MealToImageConverter : ValueConverter<Meal, ImageSource>
    {
        private EnumToImageSourceConverter _converter = new EnumToImageSourceConverter();

        public override ImageSource Convert( Meal value )
        {
            MealType type = value.MealTypes.OrderByDescending( x => x ).First();
            return _converter.Convert( type );
        }
    }

    /// <summary>
    /// HACK: Allows Restaurants to be used with LongListSelector.
    /// </summary>
    public sealed class RestaurantsToGroupsConverter : ValueConverter<Restaurant[], RestaurantAsGroup[]>
    {
        public override RestaurantAsGroup[] Convert( Restaurant[] value )
        {
            if ( value == null )
            {
                return null;
            }

            return value.Select( r => new RestaurantAsGroup( r ) ).ToArray();
        }
    }

    /// <summary>
    /// Converts a number of votes to a human-readable string.
    /// </summary>
    public sealed class VoteCountToStringConverter : ValueConverter<int, string>
    {
        public override string Convert( int value )
        {
            return value == 0 ? PluginResources.NoVotesCast
                 : value == 1 ? PluginResources.OneVoteCast
                              : string.Format( PluginResources.ManyVotesCast, value );
        }
    }
}