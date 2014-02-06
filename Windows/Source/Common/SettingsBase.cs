// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Reflection;
using System.Runtime.CompilerServices;
using PocketCampus.Mvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// A base class for settings.
    /// Its usage is not required, but it significantly eases the process.
    /// </summary>
    public abstract class SettingsBase : ObservableObject
    {
        private string _pluginKey;
        private Dictionary<string, Func<object>> _defaultValues;

        protected IApplicationSettings Settings { get; private set; }


        /// <summary>
        /// Creates a new PluginSettingsBase.
        /// </summary>
        /// <param name="settings">The application settings. Your constructor should also take them as a parameter.</param>
        protected SettingsBase( IApplicationSettings settings )
        {
            _pluginKey = GetType().Namespace;
            Settings = settings;
        }


        /// <summary>
        /// Gets the default values for all settings.
        /// </summary>
        protected abstract SettingsDefaultValues GetDefaultValues();


        /// <summary>
        /// Gets the specified setting's value, as an object of the specified type.
        /// This method is intended to be used from a property's get block.
        /// </summary>
        protected T Get<T>( [CallerMemberName] string propertyName = "" )
        {
            SetIfUndefined<T>( propertyName, false );
            return Settings.Get<T>( _pluginKey, propertyName );
        }

        /// <summary>
        /// Sets the specified setting's value.
        /// This method is intended to be used from a property's set block.
        /// </summary>
        protected void Set( object value, [CallerMemberName] string propertyName = "" )
        {
            Settings.Set( _pluginKey, propertyName, value );
            OnPropertyChanged( propertyName );

            var propNotif = value as INotifyPropertyChanged;
            if ( propNotif != null )
            {
                propNotif.PropertyChanged += Value_SomethingChanged;
            }

            var collNotif = value as INotifyCollectionChanged;
            if ( collNotif != null )
            {
                collNotif.CollectionChanged += Value_SomethingChanged;
            }
        }


        /// <summary>
        /// Gets the specified encrypted setting's value.
        /// The value is decrypted for you.
        /// Use this for user names, passwords, and other kinds of sensitive data.
        /// </summary>
        protected string GetEncrypted( [CallerMemberName] string propertyName = "" )
        {
            SetIfUndefined<string>( propertyName, true );
            return Settings.GetEncrypted( _pluginKey, propertyName );
        }

        /// <summary>
        /// Sets the specified encrypted setting's value.
        /// The value is encrypted for you.
        /// Use this for user names, passwords, and other kinds of sensitive data.
        /// </summary>
        protected void SetEncrypted( string value, [CallerMemberName] string propertyName = "" )
        {
            Settings.SetEncrypted( _pluginKey, propertyName, value );
            OnPropertyChanged( propertyName );
        }


        /// <summary>
        /// If the specified setting is undefined, set it to its default value.
        /// </summary>
        private void SetIfUndefined<T>( string propertyName, bool encrypted )
        {
            if ( Settings.IsDefined( _pluginKey, propertyName ) )
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

            if ( encrypted )
            {
                SetEncrypted( (string) _defaultValues[propertyName](), propertyName );
            }
            else
            {
                Set( _defaultValues[propertyName](), propertyName );
            }
        }


        /// <summary>
        /// Executed when a property or collection changes.
        /// </summary>
        private void Value_SomethingChanged( object sender, EventArgs e )
        {
            // Unfortunately, every setting has to be saved again, we can't know what changed.
            foreach ( var prop in this.GetType().GetTypeInfo().DeclaredProperties )
            {
                // we know it's not encrypted, strings don't change
                Settings.Set( _pluginKey, prop.Name, prop.GetValue( this ) );
            }
        }
    }
}