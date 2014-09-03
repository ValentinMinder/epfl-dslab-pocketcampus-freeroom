// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.WindowsRuntime;
using Windows.ApplicationModel;

namespace PocketCampus.Main.Services
{
    public sealed class PluginLoader : IPluginLoader
    {
        private const string WindowsRuntimePluginAssembliesSuffix = ".WindowsRuntime.dll";
        private static readonly TypeInfo WindowsRuntimePluginTypeInfo = typeof( IWindowsRuntimePlugin ).GetTypeInfo();


        private IPlugin[] _plugins;


        public async Task<IPlugin[]> GetPluginsAsync()
        {
            if ( _plugins == null )
            {
                var plugins = new List<IPlugin>();
                foreach ( var file in await Package.Current.InstalledLocation.GetFilesAsync() )
                {
                    if ( file.Name.EndsWith( WindowsRuntimePluginAssembliesSuffix ) )
                    {
                        var assemblyName = new AssemblyName( file.DisplayName );
                        var assembly = Assembly.Load( assemblyName );
                        var pluginType = assembly.ExportedTypes.First( t => WindowsRuntimePluginTypeInfo.IsAssignableFrom( t.GetTypeInfo() ) );
                        var plugin = (IWindowsRuntimePlugin) Activator.CreateInstance( pluginType );
                        plugins.Add( plugin );
                    }
                }

                _plugins = plugins.ToArray();
            }
            return _plugins;
        }
    }
}