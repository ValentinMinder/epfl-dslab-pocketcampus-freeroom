// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO.IsolatedStorage;
using System.Security.Cryptography;
using System.Text;
using PocketCampus.Common;

namespace PocketCampus.Main
{
    /// <summary>
    /// Application settings that use plugin names and setting keys to store values.
    /// </summary>
    public sealed class ApplicationSettings : IApplicationSettings
    {
        // Encoding used when (de)crypting strings as byte arrays
        private static readonly Encoding Encoding = Encoding.UTF8;
        // Salt-like value to add entropy to encrypted strings
        // DO NOT CHANGE or some settings will be unreadable
        private static readonly byte[] EntropyBytes = { 0xDE, 0xAD, 0xBE, 0xEF };

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
        /// Gets the specified encrypted setting's value, or the default value if it wasn't declared.
        /// </summary>
        public string GetEncrypted( string pluginName, string key )
        {
            byte[] encryptedBytes = Get<byte[]>( pluginName, key );
            if ( encryptedBytes == null )
            {
                return null;
            }
            byte[] decryptedBytes = ProtectedData.Unprotect( encryptedBytes, EntropyBytes );
            return Encoding.GetString( decryptedBytes, 0, decryptedBytes.Length );
        }

        /// <summary>
        /// Sets the specified encrypted setting's value.
        /// </summary>
        public void SetEncrypted( string pluginName, string key, string value )
        {
            if ( value == null )
            {
                Set( pluginName, key, null );
            }
            else
            {
                byte[] stringBytes = Encoding.GetBytes( value );
                byte[] encryptedBytes = ProtectedData.Protect( stringBytes, EntropyBytes );
                Set( pluginName, key, encryptedBytes );
            }
        }

        /// <summary>
        /// Gets the actual key stored in the settings from the specified plugin name and setting key.
        /// </summary>
        private string GetKey( string pluginName, string key )
        {
            return pluginName + "." + key;
        }
    }
}