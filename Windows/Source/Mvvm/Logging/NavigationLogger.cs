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
        // TODO: Remove the need for this
        private string _currentViewModelId;

        /// <summary>
        /// Creates a new NavigationLogger.
        /// </summary>
        protected NavigationLogger()
        {
            Messenger.Register<CommandLoggingRequest>( req => EnableCommandLogging( req.Object ) );
            Messenger.Register<EventLogRequest>( req => LogEvent( _currentViewModelId, req.EventId, req.Label ) );
        }

        /// <summary>
        /// Logs a navigation to the specified ViewModel, and a value indicating whether it's a navigation to a new one.
        /// </summary>
        public void LogNavigation( object viewModel, bool isForward )
        {
            var viewModelType = viewModel.GetType();
            var vmLogAttr = viewModelType.GetTypeInfo().GetCustomAttribute<LogIdAttribute>();
            if ( vmLogAttr == null )
            {
                Debug.WriteLine( "WARNING: Page {0} has no LogId attribute.", viewModelType.FullName );
            }
            else
            {
                _currentViewModelId = vmLogAttr.Id;

                if ( isForward )
                {
                    LogNavigation( vmLogAttr.Id );
                    EnableCommandLogging( viewModel );
                }
            }
        }

        /// <summary>
        /// Logs a navigation with the specified ID.
        /// </summary>
        protected abstract void LogNavigation( string id );

        /// <summary>
        /// Logs a command execution on the specified ViewModel with the specified ID and label.
        /// </summary>
        protected abstract void LogEvent( string viewModelId, string eventId, string label );


        /// <summary>
        /// Enables logging of commands on the specified object with the specified name.
        /// </summary>
        private void EnableCommandLogging( object obj )
        {
            foreach ( var prop in GetAllProperties( obj.GetType().GetTypeInfo() ).Where( pi => IsCommand( pi.PropertyType ) ) )
            {
                var idAttr = prop.GetCustomAttribute<LogIdAttribute>();
                if ( idAttr == null )
                {
                    Debug.WriteLine( "WARNING: Command {0} on object {1} has no LogIdAttribute.", prop.Name, obj.GetType().Name );
                }
                else
                {
                    var command = (CommandBase) prop.GetValue( obj );
                    Func<object, string> getLabel = _ => null;

                    var parameterAttr = prop.GetCustomAttribute<LogParameterAttribute>();
                    if ( parameterAttr != null )
                    {
                        var parameterPath = parameterAttr.ParameterPath.Split( LogParameterAttribute.PathSeparator );

                        var valuesAttr = prop.GetCustomAttributes<LogValueConverterAttribute>();
                        if ( valuesAttr == null )
                        {
                            getLabel = param => GetPathValue( parameterPath, _currentViewModelId, param ).ToString();
                        }
                        else
                        {
                            var dic = valuesAttr.ToDictionary( a => a.Value, a => a.LoggedValue );
                            getLabel = param =>
                            {
                                string value;
                                object key = GetPathValue( parameterPath, command.Owner, param );
                                dic.TryGetValue( key, out value );
                                return value ?? key.ToString();
                            };
                        }
                    }

                    command.Executed += ( s, e ) =>
                    {
                        string label;
                        try
                        {
                            label = getLabel( e.Parameter );
                        }
                        catch ( Exception exn )
                        {
                            throw new InvalidOperationException( "An error occurred while evaluating the event label.", exn );
                        }

                        LogEvent( _currentViewModelId, idAttr.Id, label );
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

        /// <summary>
        /// Evaluates a path on a root, with a parameter that may be used depending on the path.
        /// </summary>
        private static object GetPathValue( string[] path, object root, object parameter )
        {
            int n = 0;
            if ( path[0] == LogParameterAttribute.ParameterName )
            {
                root = parameter;
                n++;
            }

            while ( n < path.Length )
            {
                root = GetAllProperties( root.GetType().GetTypeInfo() ).First( p => p.Name == path[n] ).GetValue( root );
                n++;
            }

            return root;
        }
    }
}