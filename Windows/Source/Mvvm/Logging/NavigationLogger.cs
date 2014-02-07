// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// Logs navigations to ViewModels.
    /// </summary>
    public abstract class NavigationLogger
    {
        private string _currentViewModelId;

        /// <summary>
        /// Creates a new NavigationLogger.
        /// </summary>
        protected NavigationLogger()
        {
            Messenger.Register<CommandLoggingRequest>( req => EnableCommandLogging( req.Object ) );
            Messenger.Register<EventLogRequest>( req => LogEvent( _currentViewModelId, req.EventId ) );
        }

        /// <summary>
        /// Logs a navigation to the specified ViewModel.
        /// </summary>
        public void LogNavigation( object viewModel )
        {
            var viewModelType = viewModel.GetType();

            var vmLogAttr = viewModelType.GetTypeInfo().GetCustomAttribute<PageLogIdAttribute>();
            if ( vmLogAttr == null )
            {
                Debug.WriteLine( "WARNING: Page {0} has no PageLogId attribute.", viewModelType.FullName );
            }
            else
            {
                _currentViewModelId = vmLogAttr.Id;
                LogNavigation( vmLogAttr.Id );
                EnableCommandLogging( viewModel );
            }
        }

        /// <summary>
        /// Logs a navigation with the specified ID.
        /// </summary>
        protected abstract void LogNavigation( string id );

        /// <summary>
        /// Logs a command execution on the specified ViewModel with the specified ID.
        /// </summary>
        protected abstract void LogEvent( string viewModelId, string eventId );


        /// <summary>
        /// Enables logging of commands on the specified object with the specified name.
        /// </summary>
        private void EnableCommandLogging( object obj )
        {
            foreach ( var commandProp in GetAllProperties( obj.GetType().GetTypeInfo() ).Where( pi => IsCommand( pi.PropertyType ) ) )
            {
                var commandLogAttr = commandProp.GetCustomAttribute<CommandLogIdAttribute>();
                if ( commandLogAttr == null )
                {
                    Debug.WriteLine( "WARNING: Command {0} on object {1} has no CommandLogIdAttribute.", commandProp.Name, obj.GetType().Name );
                }
                else
                {
                    var command = (CommandBase) commandProp.GetValue( obj );
                    command.Executed += ( s, e ) =>
                    {
                        LogEvent( _currentViewModelId, commandLogAttr.Id );
                    };
                }
            }
        }


        /// <summary>
        /// Get all properties of a type info, including those declared by base types.
        /// </summary>
        private static IEnumerable<PropertyInfo> GetAllProperties( TypeInfo type )
        {
            foreach ( var prop in type.DeclaredProperties )
            {
                yield return prop;
            }
            if ( type.BaseType != null )
            {
                foreach ( var prop in GetAllProperties( type.BaseType.GetTypeInfo() ) )
                {
                    yield return prop;
                }
            }
        }

        /// <summary>
        /// Indicates whether the specified type derives from CommandBase.
        /// </summary>
        private static bool IsCommand( Type type )
        {
            return typeof( CommandBase ).GetTypeInfo().IsAssignableFrom( type.GetTypeInfo() );
        }
    }
}