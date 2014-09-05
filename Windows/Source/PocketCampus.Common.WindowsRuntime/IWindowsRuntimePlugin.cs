﻿using ThinMvvm.WindowsRuntime;

namespace PocketCampus.Common
{
    /// <summary>
    /// Windows Runtime extensions for IPlugin.
    /// </summary>
    public interface IWindowsRuntimePlugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's localized name.
        /// </summary>
        string Name { get; }

        /// <summary>
        /// Gets the plugin's icon.
        /// </summary>
        // TODO: Find a clean way to represent it, and re-generate all icons to the right sizes (also for the live tiles on the start screen)
        object Icon { get; }

        /// <summary>
        /// Performs the Windows Runtime-specific initialization for the plugin.
        /// </summary>
        void Initialize( IWindowsRuntimeNavigationService navigationService );
    }
}