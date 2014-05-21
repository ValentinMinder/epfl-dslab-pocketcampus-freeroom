// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq.Expressions;
using System.Runtime.CompilerServices;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Input;
using ThinMvvm;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// UserControl that implements INotifyPropertyChanged.
    /// </summary>
    public abstract class ObservableControl : UserControl, INotifyPropertyChanged
    {
        private Dictionary<string, ICommand> _cache = new Dictionary<string, ICommand>();

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



        // Not very pretty, but it's the only way to do it
        // Sometimes I wish C# had traits
        // This is a straight copy-paste from ViewModel<T>

        /// <summary>
        /// Gets or create a parameterless Command that will be unique to this ViewModel.
        /// </summary>
        protected Command GetCommand( Action execute, Expression<Func<bool>> canExecute = null, [CallerMemberName] string name = "" )
        {
            if ( !_cache.ContainsKey( name ) )
            {
                _cache.Add( name, new Command( this, execute, canExecute ) );
            }

            return (Command) _cache[name];
        }

        /// <summary>
        /// Gets or create a Command that will be unique to this ViewModel.
        /// </summary>
        protected Command<T> GetCommand<T>( Action<T> execute, Expression<Func<T, bool>> canExecute = null, [CallerMemberName] string name = "" )
        {
            if ( !_cache.ContainsKey( name ) )
            {
                _cache.Add( name, new Command<T>( this, execute, canExecute ) );
            }

            return (Command<T>) _cache[name];
        }

        // N.B.: The following two methods cannot be called GetCommand because since the return type of a function is not part of its
        //       signature, overload resolution cannot decide whether a parameterless method is an Action or a Func<T>.

        /// <summary>
        /// Gets or create a parameterless AsyncCommand that will be unique to this ViewModel.
        /// </summary>
        protected AsyncCommand GetAsyncCommand( Func<Task> execute, Expression<Func<bool>> canExecute = null, [CallerMemberName] string name = "" )
        {
            if ( !_cache.ContainsKey( name ) )
            {
                _cache.Add( name, new AsyncCommand( this, execute, canExecute ) );
            }

            return (AsyncCommand) _cache[name];
        }

        /// <summary>
        /// Gets or create an AsyncCommand that will be unique to this ViewModel.
        /// </summary>
        protected AsyncCommand<T> GetAsyncCommand<T>( Func<T, Task> execute, Expression<Func<T, bool>> canExecute = null, [CallerMemberName] string name = "" )
        {
            if ( !_cache.ContainsKey( name ) )
            {
                _cache.Add( name, new AsyncCommand<T>( this, execute, canExecute ) );
            }

            return (AsyncCommand<T>) _cache[name];
        }
    }
}