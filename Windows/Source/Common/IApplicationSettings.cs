// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common
{
    /// <summary>
    /// Application settings that use plugin names and setting keys to store values.
    /// </summary>
    public interface IApplicationSettings
    {
        /// <summary>
        /// Indicates whether the specified setting is defined.
        /// </summary>
        bool IsDefined( string pluginName, string key );


        /// <summary>
        /// Gets the specified setting value as an object of the specified type.
        /// </summary>
        T Get<T>( string pluginName, string key );

        /// <summary>
        /// Sets the specified setting's value.
        /// </summary>
        void Set( string pluginName, string key, object value );
    }
}