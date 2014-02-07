// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Map.Resources;

namespace PocketCampus.Map
{
    /// <summary>
    /// Settings for the map plugin.
    /// </summary>
    public sealed class PluginSettings : SettingsBase, IPluginSettings
    {
        /// <summary>
        /// Gets or sets a value indicating whether the map should display and use the user's position.
        /// </summary>
        public bool UseGeolocation
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates a new PluginSettings.
        /// </summary>
        public PluginSettings( IApplicationSettings settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values of the settings.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues<PluginSettings>
            {
                { x => x.UseGeolocation, () => MessageBoxEx.ShowPrompt( PluginResources.UseGeolocationCaption, PluginResources.UseGeolocationMessage ) },
            };
        }
    }
}