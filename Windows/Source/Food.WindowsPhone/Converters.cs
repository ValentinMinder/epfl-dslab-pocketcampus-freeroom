// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Windows;
using System.Windows.Data;
using System.Windows.Media;
using System.Windows.Media.Imaging;
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
    /// Converts meals to images of their main type.
    /// </summary>
    public sealed class MealToImageConverter : ValueConverter<Meal, ImageSource>
    {
        protected override ImageSource Convert( Meal value )
        {
            string type = value.MealTypes.OrderByDescending( x => x ).First().ToString();
            return new BitmapImage( new Uri( string.Format( "/Assets/MealTypes_{0}.png", type ), UriKind.Relative ) );
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