// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Food.Models;
using Windows.ApplicationModel.Resources;
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

    public sealed class MealTypeToImageConverter : ValueConverter<MealType, ImageSource>
    {
        public override ImageSource Convert( MealType value )
        {
            return new BitmapImage( new Uri( "ms-appx:///PocketCampus.Food.WindowsRuntime/Images/MealType_" + value + ".png", UriKind.Absolute ) );
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