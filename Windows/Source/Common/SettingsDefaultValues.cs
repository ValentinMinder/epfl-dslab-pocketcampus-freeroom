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
    /// This class is an internal infrastructure class.
    /// Do not use this class from your code.
    /// </summary>
    /// <remarks>
    /// This is a hack to compensate for the lack of generic wildcards in .NET.
    /// </remarks>
    public abstract class SettingsDefaultValues
    {
        internal Dictionary<string, Func<object>> AsDictionary { get; private set; }

        protected SettingsDefaultValues()
        {
            AsDictionary = new Dictionary<string, Func<object>>();
        }
    }

    /// <summary>
    /// A dictionary containing setting default values.
    /// This class is intended to be used with the collection initialization syntax,
    /// e.g. new SettingsDefaultValues&lt;MySettings&gt; { { x => x.SomeSetting, 42 } }
    /// </summary>
    public sealed class SettingsDefaultValues<TSettings> : SettingsDefaultValues, IEnumerable
        where TSettings : SettingsBase
    {
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

        // Required for collection initializers, see §7.5.10.3 of the C# spec.
        public IEnumerator GetEnumerator()
        {
            throw new NotSupportedException();
        }
    }
}