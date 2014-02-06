// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Reflection;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Simple messenger implementation.
    /// Unregistering functionality is not provided.
    /// </summary>
    public static class Messenger
    {
        private static readonly HashSet<Recipient> _recipients = new HashSet<Recipient>();

        /// <summary>
        /// Registers the specified handler for the specified message type.
        /// </summary>
        public static void Register<T>( Action<T> action )
        {
            _recipients.Add( Recipient.Create( action ) );
        }

        /// <summary>
        /// Sends the specified message.
        /// </summary>
        public static void Send( object message )
        {
            _recipients.RemoveWhere( r => !r.TryReceive( message ) );
        }

        /// <summary>
        /// Clears the registered actions.
        /// </summary>
        /// <remarks>
        /// For use in unit tests.
        /// </remarks>
        internal static void Clear()
        {
            _recipients.Clear();
        }


        private sealed class Recipient
        {
            private const string ClosureMethodToken = "<";

            // if the action is a closure we need a strong reference to the target
#pragma warning disable 0414
            private object _targetStrongRef;
#pragma warning restore 0414
            private bool _hasNoTarget;
            private WeakReference<object> _targetRef;
            private MethodInfo _method;
            private Type _messageType;

            public static Recipient Create<T>( Action<T> action )
            {
                return new Recipient
                {
                    _targetStrongRef = action.GetMethodInfo().Name.Contains( ClosureMethodToken ) ? action.Target : null,
                    _hasNoTarget = action.Target == null,
                    _method = action.GetMethodInfo(),
                    _targetRef = new WeakReference<object>( action.Target ),
                    _messageType = typeof( T )
                };
            }

            public bool TryReceive( object message )
            {
                object target = null;
                if ( _hasNoTarget || _targetRef.TryGetTarget( out target ) )
                {
                    if ( message.GetType() == _messageType )
                    {
                        _method.Invoke( target, new[] { message } );
                    }
                    return true;
                }

                return false;
            }
        }
    }
}