// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Reflection;
using System.Windows;
using PocketCampus.Common;
using ThinMvvm;

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
        // The format of the settings key for the cached plugin types
        private const string CachedTypesKeyFormat = "PocketCampus.Main.CachedPlugins_{0}";

        private ISettingsStorage _settings;
        private string _cachedTypesKey;
        private IPlugin[] _plugins;


        /// <summary>
        /// Creates a new instance of the <see cref="PluginLoader" /> class.
        /// </summary>
        public PluginLoader( ISettingsStorage settings )
        {
            _settings = settings;
            _cachedTypesKey = string.Format( CachedTypesKeyFormat, Assembly.GetExecutingAssembly().GetName().Version.ToString() );
        }


        /// <summary>
        /// Gets all available plugins.
        /// </summary>
        public IPlugin[] GetPlugins()
        {
            if ( _plugins == null )
            {
                string[] typeNames;
                if ( _settings.IsDefined( _cachedTypesKey ) )
                {
                    typeNames = _settings.Get<string[]>( _cachedTypesKey );
                }
                else
                {
                    typeNames = ( from part in Deployment.Current.Parts
                                  let name = part.Source.Replace( AssemblySuffix, "" )
                                  where name.StartsWith( PluginAssembliesPrefix )
                                  let assembly = Assembly.Load( name )
                                  from type in assembly.ExportedTypes
                                  where InheritsInterface( type, typeof( IWindowsPhonePlugin ) )
                                  select type.AssemblyQualifiedName )
                             .ToArray();

                    _settings.Set( _cachedTypesKey, typeNames );
                }

                _plugins = ( from name in typeNames
                             let type = Type.GetType( name )
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
            return type != interfaceType && interfaceType.IsAssignableFrom( type );
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