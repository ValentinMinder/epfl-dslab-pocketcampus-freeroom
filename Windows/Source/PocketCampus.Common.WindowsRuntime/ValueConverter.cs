using System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;

namespace PocketCampus.Common
{
    public abstract class ValueConverter<TFrom, TTo> : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, string language )
        {
            if ( !( value is TFrom ) )
            {
                return DependencyProperty.UnsetValue;
            }

            return Convert( (TFrom) value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            if ( !( value is TTo ) )
            {
                return DependencyProperty.UnsetValue;
            }

            return ConvertBack( (TTo) value );
        }

        public abstract TTo Convert( TFrom value );

        public virtual TFrom ConvertBack( TTo value )
        {
            throw new NotImplementedException();
        }
    }

    public abstract class ValueConverter<TFrom, TParameter, TTo> : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, string language )
        {
            if ( !( value is TFrom ) )
            {
                return DependencyProperty.UnsetValue;
            }

            return Convert( (TFrom) value, (TParameter) parameter );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            if ( !( value is TTo ) )
            {
                return DependencyProperty.UnsetValue;
            }

            return ConvertBack( (TTo) value, (TParameter) parameter );
        }

        public abstract TTo Convert( TFrom value, TParameter parameter );

        public virtual TFrom ConvertBack( TTo value, TParameter parameter )
        {
            throw new NotImplementedException();
        }
    }
}