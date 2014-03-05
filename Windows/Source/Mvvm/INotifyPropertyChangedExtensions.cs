// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.ComponentModel;
using System.Linq.Expressions;
using PocketCampus.Mvvm.Internals;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Extensions for INotifyPropertyChanged.
    /// </summary>
    public static class INotifyPropertyChangedExtensions
    {
        /// <summary>
        /// Adds a listener to changes on the specified property of the item.
        /// </summary>
        /// <typeparam name="T">The type of the item.</typeparam>
        /// <param name="item">The item.</param>
        /// <param name="propertyExpr">An expression that returns the property to listen to.</param>
        /// <param name="listener">The listener.</param>
        public static void ListenToProperty<T>( this T item, Expression<Func<T, object>> propertyExpr, Action listener )
            where T : INotifyPropertyChanged
        {
            string name = ExpressionHelper.GetPropertyName( propertyExpr );
            item.PropertyChanged += ( _, e ) =>
            {
                if ( e.PropertyName == name )
                {
                    listener();
                }
            };
        }
    }
}