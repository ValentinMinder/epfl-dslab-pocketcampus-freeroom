// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Simple container for dependency injection (DI).
    /// </summary>
    public static class Container
    {
        private static readonly Dictionary<Type, Type> _impls = new Dictionary<Type, Type>();
        private static readonly Dictionary<Type, object> _staticImpls = new Dictionary<Type, object>();

        /// <summary>
        /// Binds a type to a concrete implementation.
        /// </summary>
        public static void Bind<TAbstract, TImpl>()
            where TImpl : TAbstract
        {
            Type key = typeof( TAbstract ), value = typeof( TImpl );
            var implInfo = typeof( TImpl ).GetTypeInfo();

            if ( typeof( TAbstract ) == typeof( TImpl ) )
            {
                throw new ArgumentException( "Cannot bind a type to itself.", "TImpl" );
            }
            if ( implInfo.IsInterface || implInfo.IsAbstract )
            {
                throw new ArgumentException( "The implementation type must be concrete." );
            }
            if ( _staticImpls.ContainsKey( key ) )
            {
                throw new InvalidOperationException( "Cannot override an implementation. (the existing one was a singleton)" );
            }
            if ( _impls.ContainsKey( key ) )
            {
                throw new InvalidOperationException( "Cannot override an implementation." );
            }

            _impls.Add( key, value );
        }

        /// <summary>
        /// Binds a type to a singleton instance of a concrete type.
        /// </summary>
        public static TImpl BindOnce<TAbstract, TImpl>()
            where TImpl : TAbstract
        {
            Type key = typeof( TAbstract );
            var implInfo = typeof( TImpl ).GetTypeInfo();

            if ( typeof( TAbstract ) == typeof( TImpl ) )
            {
                throw new ArgumentException( "Cannot bind a type to itself." );
            }
            if ( implInfo.IsInterface || implInfo.IsAbstract )
            {
                throw new ArgumentException( "The implementation type must be concrete." );
            }
            if ( _impls.ContainsKey( key ) )
            {
                throw new InvalidOperationException( "Cannot override an implementation. (the existing one was not a singleton)" );
            }
            if ( _staticImpls.ContainsKey( key ) )
            {
                throw new InvalidOperationException( "Cannot override an implementation." );
            }

            var implementation = Get( typeof( TImpl ), null );
            _staticImpls.Add( key, implementation );
            return (TImpl) implementation;
        }

        /// <summary>
        /// Gets a concrete instance of the specified type, resolving constructor arguments as needed, 
        /// with an optional additional constructor argument.
        /// </summary>
        public static object Get( Type type, object argument )
        {
            if ( _staticImpls.ContainsKey( type ) )
            {
                return _staticImpls[type];
            }

            TypeInfo toCreate = type.GetTypeInfo();

            if ( toCreate.IsInterface || toCreate.IsAbstract )
            {
                if ( _impls.ContainsKey( type ) )
                {
                    toCreate = _impls[type].GetTypeInfo();
                }
                else if ( argument == null )
                {
                    throw new ArgumentException( "Missing implementation: " + type.Name );
                }
            }

            var ctor = toCreate.DeclaredConstructors.First( ci => !ci.IsStatic );
            var argType = argument == null ? null : argument.GetType();
            bool argUsed = false;

            var ctorArgs =
                ctor.GetParameters()
                    .Select( pi => pi.ParameterType )
                    .Select( typ =>
                             {
                                 if ( argument != null && typ.GetTypeInfo().IsAssignableFrom( argType.GetTypeInfo() ) )
                                 {
                                     if ( _staticImpls.ContainsKey( typ ) || _impls.ContainsKey( typ ) )
                                     {
                                         throw new ArgumentException( "Ambiguous match for constructor argument of type {0} between a dependency and the argument.", typ.FullName );
                                     }

                                     if ( argUsed )
                                     {
                                         throw new InvalidOperationException( "Cannot use the argument twice in a constructor." );
                                     }

                                     argUsed = true;
                                     return argument;
                                 }
                                 return Get( typ, null );
                             } )
                    .ToArray();
            return ctor.Invoke( ctorArgs );
        }

        /// <summary>
        /// Clears the container.
        /// </summary>
        /// <remarks>
        /// Used in unit tests.
        /// </remarks>
        internal static void Clear()
        {
            _impls.Clear();
            _staticImpls.Clear();
        }
    }
}