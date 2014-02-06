// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq.Expressions;

namespace PocketCampus.Mvvm.Internals
{
    /// <summary>
    /// Internal utility class for expressions.
    /// </summary>
    internal static class ExpressionHelper
    {
        /// <summary>
        /// Gets the name of the property in a property expression.
        /// </summary>
        public static string GetPropertyName<TObj, TProp>( Expression<Func<TObj, TProp>> expr )
        {
            if ( !( expr.Body is MemberExpression ) )
            {
                throw new ArgumentException( "Invalid expression; it must return a property." );
            }
            return ( (MemberExpression) expr.Body ).Member.Name;
        }
    }
}