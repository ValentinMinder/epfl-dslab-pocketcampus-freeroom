// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

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

    public abstract class ValueConverter<TFrom, TParameter, TTo> : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, string language )
        {
            return Convert( (TFrom) value, (TParameter) parameter );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            return ConvertBack( (TTo) value, (TParameter) parameter );
        }

        public abstract TTo Convert( TFrom value, TParameter parameter );

        public virtual TFrom ConvertBack( TTo value, TParameter parameter )
        {
            throw new NotImplementedException();
        }
    }
}