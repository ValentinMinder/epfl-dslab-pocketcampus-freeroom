// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using ThinMvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// Base class for settings.
    /// </summary>
    public abstract class SettingsBase<TSelf> : ObservableObject
        where TSelf : SettingsBase<TSelf>
    {
        private readonly IApplicationSettings _settings;
        private readonly string _pluginKey;

        private Dictionary<string, Func<object>> _defaultValues;

        /// <summary>
        /// Creates a new PluginSettingsBase.
        /// </summary>
        /// <param name="settings">The application settings. Your constructor should also take them as a parameter.</param>
        protected SettingsBase( IApplicationSettings settings )
        {
            _settings = settings;
            _pluginKey = GetType().Namespace;
        }


        /// <summary>
        /// Gets the default values for all settings.
        /// </summary>
        protected abstract SettingsDefaultValues<TSelf> GetDefaultValues();


        /// <summary>
        /// Gets the specified setting's value, as an object of the specified type.
        /// This method is intended to be used from a property's get block.
        /// </summary>
        protected T Get<T>( [CallerMemberName] string propertyName = "" )
        {
            SetIfUndefined( propertyName );
            return _settings.Get<T>( _pluginKey, propertyName );
        }

        /// <summary>
        /// Sets the specified setting's value.
        /// This method is intended to be used from a property's set block.
        /// </summary>
        protected void Set( object value, [CallerMemberName] string propertyName = "" )
        {
            _settings.Set( _pluginKey, propertyName, value );

            OnPropertyChanged( propertyName );

            var propNotif = value as INotifyPropertyChanged;
            if ( propNotif != null )
            {
                propNotif.PropertyChanged += ( s, _ ) => _settings.Set( _pluginKey, propertyName, s );
            }

            var collNotif = value as INotifyCollectionChanged;
            if ( collNotif != null )
            {
                collNotif.CollectionChanged += ( s, _ ) => _settings.Set( _pluginKey, propertyName, s );
            }
        }


        /// <summary>
        /// If the specified setting is undefined, set it to its default value.
        /// </summary>
        private void SetIfUndefined( string propertyName )
        {
            if ( _settings.IsDefined( _pluginKey, propertyName ) )
            {
                return;
            }

            if ( _defaultValues == null )
            {
                _defaultValues = GetDefaultValues().AsDictionary;
            }

            if ( !_defaultValues.ContainsKey( propertyName ) )
            {
                throw new InvalidOperationException( "No default value found for property " + propertyName );
            }

            Set( _defaultValues[propertyName](), propertyName );
        }
    }
}