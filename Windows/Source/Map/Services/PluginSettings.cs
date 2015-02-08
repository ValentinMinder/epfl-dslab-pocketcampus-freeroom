// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;

namespace PocketCampus.Map.Services
{
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        public bool UseGeolocation
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }


        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.UseGeolocation, () => true },
            };
        }
    }
}