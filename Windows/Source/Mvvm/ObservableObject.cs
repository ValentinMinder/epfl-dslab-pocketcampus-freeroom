// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Runtime.Serialization;
using System.Threading;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Base class to implement INotifyPropertyChanged easily.
    /// </summary>
    [DataContract]
    public abstract class ObservableObject : INotifyPropertyChanged
    {
        // Used to send PropertyChanged messages
        // otherwise they may result in invalid cross-thread access.
        private readonly SynchronizationContext _context;


        /// <summary>
        /// Creates a new ObservableObject.
        /// </summary>
        protected ObservableObject()
        {
            _context = SynchronizationContext.Current;
        }


        /// <summary>
        /// Occurs when a property value changes.
        /// </summary>
        public event PropertyChangedEventHandler PropertyChanged;
        protected void OnPropertyChanged( [CallerMemberName] string propertyName = "" )
        {
            var evt = this.PropertyChanged;
            if ( evt != null )
            {
                Action action = () => evt( this, new PropertyChangedEventArgs( propertyName ) );
                if ( _context == null )
                {
                    action();
                }
                else
                {
                    _context.Send( _ => action(), null );
                }
            }
        }

        /// <summary>
        /// Sets the specified field to the specified value and raises <see cref="PropertyChanged"/>.
        /// </summary>
        protected void SetProperty<T>( ref T field, T value, [CallerMemberName] string propertyName = "" )
        {
            if ( !object.Equals( field, value ) )
            {
                field = value;
                this.OnPropertyChanged( propertyName );
            }
        }
    }
}