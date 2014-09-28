﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    public sealed class PluginLoader : IPluginLoader
    {
        // PERFORMANCE: Referencing all plugin assemblies from Main is required by WinRT anyway, and reflecting over loaded assemblies is too slow
        private readonly IPlugin[] _plugins =
        {
            new Authentication.WindowsRuntimePlugin(),
            new Camipro.WindowsRuntimePlugin(),
            new Directory.WindowsRuntimePlugin(),
            new Events.WindowsRuntimePlugin(),
            new Food.WindowsRuntimePlugin(),
            new IsAcademia.WindowsRuntimePlugin(),
            new Map.WindowsRuntimePlugin(),
            new Moodle.WindowsRuntimePlugin(),
            new News.WindowsRuntimePlugin(),
            new Satellite.WindowsRuntimePlugin(),
            new Transport.WindowsRuntimePlugin()
        };


        public IPlugin[] GetPlugins()
        {
            return _plugins;
        }
    }
}