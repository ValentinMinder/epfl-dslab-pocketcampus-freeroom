// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Windows;
using System.Windows.Data;
using System.Windows.Media;
using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Food.Resources;

namespace PocketCampus.Food
{
    /// <summary>
    /// Converts meals to their price according to the current settings.
    /// </summary>
    /// <remarks>
    /// Since it needs IPluginSettings, it can't be a generic ValueConverter.
    /// </remarks>
    public sealed class MealPriceConverter : DependencyObject, IValueConverter
    {
        private const string PriceFormat = "{0:0.00} CHF";

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

            double? price = meal.GetPrice( target );
            return price == null ? PluginResources.UnknownPrice : string.Format( PriceFormat, price );
        }

        public object ConvertBack( object value, Type targetType, object parameter, CultureInfo culture )
        {
            throw new NotSupportedException();
        }
    }

    /// <summary>
    /// Converts a meal type to a brush.
    /// </summary>
    public sealed class MealTypeToBrushConverter : ValueConverter<MealTypes, Brush>
    {
        private const byte Alpha = 255;

        private static Dictionary<MealTypes, Brush> Values = new Dictionary<MealTypes, Brush>
        {
            { MealTypes.Unknown, new SolidColorBrush( Color.FromArgb( Alpha, 0x00, 0x00, 0x00 ) ) },
            { MealTypes.GreenFork, new SolidColorBrush( Color.FromArgb( Alpha, 0x77, 0xD9, 0x4E ) ) },
            { MealTypes.Fish, new SolidColorBrush( Color.FromArgb( Alpha, 0x00, 0xA2, 0xFF ) ) },
            { MealTypes.Meat, new SolidColorBrush( Color.FromArgb( Alpha, 0xEF, 0x07, 0x07 ) ) },
            { MealTypes.Poultry, new SolidColorBrush( Color.FromArgb( Alpha, 0xC4, 0x6C, 0x00 ) ) },
            { MealTypes.Vegetarian, new SolidColorBrush( Color.FromArgb( Alpha, 0x45, 0xDE, 0x46 ) ) },
            { MealTypes.Pasta, new SolidColorBrush( Color.FromArgb( Alpha, 0xF2, 0xE1, 0x32 ) ) },
            { MealTypes.Pizza, new SolidColorBrush( Color.FromArgb( Alpha, 0xEA, 0xC5, 0x4F ) ) },
            { MealTypes.Thai, new SolidColorBrush( Color.FromArgb( Alpha, 0xE0, 0xE0, 0xE0 ) ) },
            { MealTypes.Indian, new SolidColorBrush( Color.FromArgb( Alpha, 0xE0, 0xE0, 0xE0 ) ) },
            { MealTypes.Lebanese, new SolidColorBrush( Color.FromArgb( Alpha, 0xE0, 0xE0, 0xE0 ) ) }
        };

        protected override Brush Convert( MealTypes value )
        {
            return Values[value];
        }
    }

    /// <summary>
    /// HACK: Allows Restaurants to be used with LongListSelector.
    /// </summary>
    public sealed class RestaurantsToGroupsConverter : ValueConverter<Restaurant[], RestaurantAsGroup[]>
    {
        protected override RestaurantAsGroup[] Convert( Restaurant[] value )
        {
            if ( value == null )
            {
                return null;
            }

            return value.Select( r => new RestaurantAsGroup( r ) ).ToArray();
        }
    }
}