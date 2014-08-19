// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Main.Services;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Main
{
    public sealed class CustomAppDependencies : AppDependencies
    {
        public IPluginLoader PluginLoader { get; private set; }

        public CustomAppDependencies( IWindowsPhoneNavigationService navigationService, IPluginLoader pluginLoader )
            : base( navigationService )
        {
            PluginLoader = pluginLoader;
        }
    }
}