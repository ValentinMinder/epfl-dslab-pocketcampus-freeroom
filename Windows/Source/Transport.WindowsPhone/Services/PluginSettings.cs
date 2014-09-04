// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.ObjectModel;
using PocketCampus.Common;
using PocketCampus.Transport.Models;
using PocketCampus.Transport.Resources;
using ThinMvvm;

namespace PocketCampus.Transport.Services
{
    /// <summary>
    /// Plugin-specific settings.
    /// </summary>
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        /// <summary>
        /// Gets or sets a value indicating whether to sort the stations
        /// according to their distance from the user.
        /// </summary>
        public bool SortByPosition
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the stations.
        /// </summary>
        public ObservableCollection<Station> Stations
        {
            get { return Get<ObservableCollection<Station>>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates new PluginSettings.
        /// </summary>
        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        /// <summary>
        /// Gets the settings default values.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.SortByPosition, () => MessageBoxEx.ShowPrompt( PluginResources.UseGeolocationCaption, PluginResources.UseGeolocationMessage ) },
                { x => x.Stations, () => new ObservableCollection<Station>() }
            };
        }
    }
}