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
        private static readonly Dictionary<Type, object> _impls = new Dictionary<Type, object>();

        /// <summary>
        /// Binds a type to a concrete implementation.
        /// </summary>
        /// <returns>The instance of the implementation.</returns>
        public static TImpl Bind<TAbstract, TImpl>()
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
                throw new InvalidOperationException( "Cannot override an implementation." );
            }

            var implementation = Get( typeof( TImpl ), null );
            _impls.Add( key, implementation );
            return (TImpl) implementation;
        }

        /// <summary>
        /// Gets a concrete instance of the specified type, resolving constructor arguments as needed, 
        /// with an optional additional constructor argument.
        /// </summary>
        public static object Get( Type type, object argument )
        {
            TypeInfo typeInfo = type.GetTypeInfo();

            var existingImpl = _impls.FirstOrDefault( pair => typeInfo.IsAssignableFrom( pair.Key.GetTypeInfo() ) ).Value;
            if ( existingImpl != null )
            {
                return existingImpl;
            }

            if ( typeInfo.IsInterface || typeInfo.IsAbstract )
            {
                throw new ArgumentException( "Missing implementation: " + typeInfo.Name );
            }

            var ctor = typeInfo.DeclaredConstructors.SingleOrDefault( ci => !ci.IsStatic );
            if ( ctor == null )
            {
                throw new ArgumentException( "Could not find an unique constructor for type {0}", typeInfo.Name );
            }

            var argType = argument == null ? null : argument.GetType();
            bool argUsed = false;

            var ctorArgs =
                ctor.GetParameters()
                    .Select( param =>
                             {
                                 if ( argument != null && param.ParameterType.GetTypeInfo().IsAssignableFrom( argType.GetTypeInfo() ) )
                                 {
                                     if ( _impls.ContainsKey( param.ParameterType ) )
                                     {
                                         throw new ArgumentException( "Ambiguous match for constructor argument of type {0} between a dependency and the argument.", param.ParameterType.FullName );
                                     }

                                     if ( argUsed )
                                     {
                                         throw new InvalidOperationException( "Cannot use the argument twice in a constructor." );
                                     }

                                     argUsed = true;
                                     return argument;
                                 }
                                 return Get( param.ParameterType, null );
                             } )
                    .ToArray();
            return ctor.Invoke( ctorArgs );
        }

        /// <summary>
        /// Clears the container.
        /// </summary>
        /// <remarks>
        /// For use in unit tests.
        /// </remarks>
        internal static void Clear()
        {
            _impls.Clear();
        }
    }
}