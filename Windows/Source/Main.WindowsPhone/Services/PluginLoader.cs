// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Reflection;
using System.Windows;
using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Loads plugins from the currently referenced assemblies.
    /// </summary>
    public sealed class PluginLoader : IPluginLoader
    {
        // The prefix assemblies with plugins have in their names
        private const string PluginAssembliesPrefix = "PocketCampus";
        // The suffix assemblies have, which needs to be removed from their name to load them
        private const string AssemblySuffix = ".dll";

        private IPlugin[] _plugins;

        /// <summary>
        /// Gets all available plugins.
        /// </summary>
        public IPlugin[] GetPlugins()
        {
            if ( _plugins == null )
            {
                _plugins = ( from part in Deployment.Current.Parts
                             let name = part.Source.Replace( AssemblySuffix, "" )
                             where name.StartsWith( PluginAssembliesPrefix )
                             let assembly = Assembly.Load( name )
                             from type in assembly.ExportedTypes
                             where InheritsInterface( type, typeof( IWindowsPhonePlugin ) )
                             let inst = (IWindowsPhonePlugin) GetInstance( type )
                             orderby inst.Name ascending
                             select inst )
                           .ToArray();
            }

            return _plugins;
        }

        /// <summary>
        /// Checks whether the specified type inherits the specified interface.
        /// </summary>
        private static bool InheritsInterface( Type type, Type interfaceType )
        {
            return type != interfaceType
                && interfaceType.IsAssignableFrom( type );
        }

        /// <summary>
        /// Gets an instance of the specified type.
        /// </summary>
        private static object GetInstance( Type type )
        {
            return type.GetConstructor( Type.EmptyTypes ).Invoke( null );
        }
    }
}