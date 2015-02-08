// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.ObjectModel;
using PocketCampus.Transport.Models;
using ThinMvvm;

namespace PocketCampus.Transport.Services
{
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        public bool SortByPosition
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        public ObservableCollection<Station> Stations
        {
            get { return Get<ObservableCollection<Station>>(); }
            set { Set( value ); }
        }


        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.SortByPosition, () => true },
                { x => x.Stations, () => new ObservableCollection<Station>() }
            };
        }
    }
}