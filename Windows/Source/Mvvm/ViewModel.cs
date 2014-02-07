// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Runtime.CompilerServices;
using System.Threading.Tasks;
using System.Windows.Input;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Concrete implementation of IViewModel, also implementing ObservableObject.
    /// </summary>
    /// <remarks>
    /// IViewModel needs to be an interface to benefit from covariance.
    /// </remarks>
    public abstract class ViewModel<TArg> : ObservableObject, IViewModel<TArg>
    {
        private Dictionary<string, ICommand> _cache = new Dictionary<string, ICommand>();

        /// <summary>
        /// Called when the user navigates to the ViewModel.
        /// </summary>
        public virtual void OnNavigatedTo() { }

        /// <summary>
        /// Called when the user navigates from the ViewModel.
        /// </summary>
        public virtual void OnNavigatedFrom() { }


        /// <summary>
        /// Gets or create a parameterless Command that will be unique to this ViewModel.
        /// </summary>
        protected Command GetCommand( Action execute, Expression<Func<bool>> canExecute = null, [CallerMemberName] string name = "" )
        {
            if ( !_cache.ContainsKey( name ) )
            {
                _cache.Add( name, new Command( execute, canExecute ) );
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
                _cache.Add( name, new Command<T>( execute, canExecute ) );
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
                _cache.Add( name, new AsyncCommand( execute, canExecute ) );
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
                _cache.Add( name, new AsyncCommand<T>( execute, canExecute ) );
            }

            return (AsyncCommand<T>) _cache[name];
        }
    }
}