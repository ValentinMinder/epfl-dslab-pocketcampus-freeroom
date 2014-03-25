// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Windows.Data;

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
}