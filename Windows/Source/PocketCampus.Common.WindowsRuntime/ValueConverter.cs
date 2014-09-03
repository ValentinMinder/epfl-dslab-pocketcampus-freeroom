using System;
using Windows.UI.Xaml.Data;

namespace PocketCampus.Common
{
    public abstract class ValueConverter<TFrom, TTo> : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, string language )
        {
            return Convert( (TFrom) value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            return ConvertBack( (TTo) value );
        }

        public abstract TTo Convert( TFrom value );

        public virtual TFrom ConvertBack( TTo value )
        {
            throw new NotImplementedException();
        }
    }
}