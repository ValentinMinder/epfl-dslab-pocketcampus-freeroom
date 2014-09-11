using System;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.Food
{
    public sealed class VoteCountToStringConverter : ValueConverter<int, string>
    {
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Food.WindowsRuntime/Votes" );

        public override string Convert( int value )
        {
            if ( value == 0 )
            {
                return _resources.GetString( "None" );
            }

            if ( value == 1 )
            {
                return _resources.GetString( "One" );
            }

            return string.Format( _resources.GetString( "ManyFormat" ), value );
        }
    }

    public sealed class MealPriceConverter : DependencyObject, IValueConverter
    {
        private readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Food.WindowsRuntime/Price" );
        private readonly MoneyFormatConverter _moneyFormatter = new MoneyFormatConverter();

        public IPluginSettings Settings
        {
            get { return (IPluginSettings) GetValue( SettingsProperty ); }
            set { SetValue( SettingsProperty, value ); }
        }

        public static readonly DependencyProperty SettingsProperty =
            DependencyProperty.Register( "Settings", typeof( IPluginSettings ), typeof( MealPriceConverter ), new PropertyMetadata( null ) );

        public object Convert( object value, Type targetType, object parameter, string language )
        {
            var meal = (Meal) value;

            // Settings == null in design mode for some reason (TODO: check if this is still true)
            var target = Settings == null ? PriceTarget.All : Settings.PriceTarget;
            double? price = meal.GetPrice( target );
            if ( price == null )
            {
                return _resources.GetString( "Unknown" );
            }
            return _moneyFormatter.Convert( price.Value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            throw new NotSupportedException();
        }
    }

    public sealed class MealTypeToImageConverter : ValueConverter<MealType, ImageSource>
    {
        public override ImageSource Convert( MealType value )
        {
            return new BitmapImage( new Uri( "ms-appx:///PocketCampus.Food.WindowsRuntime/Images/MealType_" + value.ToString() + ".png", UriKind.Absolute ) );
        }
    }

    public sealed class MealToImageConverter : ValueConverter<Meal, ImageSource>
    {
        private readonly MealTypeToImageConverter _typeToImage = new MealTypeToImageConverter();

        public override ImageSource Convert( Meal value )
        {
            MealType type = value.MealTypes.OrderByDescending( x => x ).First();
            return _typeToImage.Convert( type );
        }
    }
}