// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO.IsolatedStorage;
using PocketCampus.Common;

namespace PocketCampus.Main
{
    /// <summary>
    /// Application settings that use plugin names and setting keys to store values.
    /// </summary>
    public sealed class ApplicationSettings : IApplicationSettings
    {
        // The settings storage
        private readonly IsolatedStorageSettings _settings;


        /// <summary>
        /// Creates a new ApplicationSettings.
        /// </summary>
        public ApplicationSettings()
        {
            _settings = IsolatedStorageSettings.ApplicationSettings;
        }


        /// <summary>
        /// Indicates whether the specified setting is defined.
        /// </summary>
        public bool IsDefined( string pluginName, string key )
        {
            return _settings.Contains( GetKey( pluginName, key ) );
        }

        /// <summary>
        /// Gets the specified setting's value, or the default value if it wasn't declared.
        /// </summary>
        public T Get<T>( string pluginName, string key )
        {
            string settingKey = GetKey( pluginName, key );
            if ( _settings.Contains( settingKey ) )
            {
                return (T) _settings[settingKey];
            }
            return default( T );
        }

        /// <summary>
        /// Sets the specified setting's value.
        /// </summary>
        public void Set( string pluginName, string key, object value )
        {
            string settingKey = GetKey( pluginName, key );
            if ( !_settings.Contains( settingKey ) )
            {
                _settings.Add( settingKey, value );
            }
            else
            {
                _settings[GetKey( pluginName, key )] = value;
            }

            _settings.Save();
        }


        /// <summary>
        /// Gets the actual key stored in the settings from the specified plugin name and setting key.
        /// </summary>
        private static string GetKey( string pluginName, string key )
        {
            return pluginName + "." + key;
        }
    }
}