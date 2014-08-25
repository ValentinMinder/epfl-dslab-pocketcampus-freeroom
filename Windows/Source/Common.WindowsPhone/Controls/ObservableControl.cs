// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Controls;
using ThinMvvm;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// UserControl that implements INotifyPropertyChanged.
    /// </summary>
    public abstract class ObservableControl : UserControl, INotifyPropertyChanged, ICommandOwner
    {
        /// <summary>
        /// Occurs when a property value changes.
        /// </summary>
        public event PropertyChangedEventHandler PropertyChanged;
        /// <summary>
        /// Triggers the <see cref="PropertyChanged"/> event.
        /// </summary>
        protected void OnPropertyChanged( [CallerMemberName] string propertyName = "" )
        {
            var evt = this.PropertyChanged;
            if ( evt != null )
            {
                evt( this, new PropertyChangedEventArgs( propertyName ) );
            }
        }

        /// <summary>
        /// Sets the specified field to the specified value and raises <see cref="PropertyChanged"/> if needed.
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