// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq.Expressions;

namespace PocketCampus.Common
{
    /// <summary>
    /// A dictionary containing setting default values.
    /// This class is intended to be used with the collection initialization syntax,
    /// e.g. new SettingsDefaultValues&lt;MySettings&gt; { { x => x.SomeSetting, 42 } }
    /// </summary>
    public sealed class SettingsDefaultValues<TSettings> : IEnumerable
        where TSettings : SettingsBase<TSettings>
    {
        internal Dictionary<string, Func<object>> AsDictionary { get; private set; }

        public SettingsDefaultValues()
        {
            AsDictionary = new Dictionary<string, Func<object>>();
        }

        public void Add<TProp>( Expression<Func<TSettings, TProp>> expr, Func<TProp> value )
        {
            AsDictionary.Add( GetPropertyName<TProp>( expr ), () => (object) value() );
        }

        private static string GetPropertyName<T>( Expression<Func<TSettings, T>> expr )
        {
            if ( !( expr.Body is MemberExpression ) )
            {
                throw new ArgumentException( "Invalid expression; it must return a property." );
            }
            return ( (MemberExpression) expr.Body ).Member.Name;
        }

        // Required for collection initializers, see §7.6.10.3 of the C# spec.
        public IEnumerator GetEnumerator()
        {
            throw new NotSupportedException();
        }
    }
}