// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq.Expressions;
using System.Threading.Tasks;
using System.Windows.Input;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Asynchronous parameterless ICommand.
    /// </summary>
    public sealed class AsyncCommand : CommandBase, ICommand
    {
        private readonly Func<Task> _execute;
        private readonly Func<bool> _canExecute;

        /// <summary>
        /// Creates a new AsyncCommand from the specified action and optional condition.
        /// </summary>
        /// <param name="execute">The action to execute when the command is executed.</param>
        /// <param name="canExecute">Optional. The predicate indicating whether the command can be executed.</param>
        public AsyncCommand( Func<Task> execute, Expression<Func<bool>> canExecute = null )
            : base( canExecute )
        {
            _execute = execute;
            _canExecute = canExecute == null ? null : canExecute.Compile();
        }

        /// <summary>
        /// Asynchronously executes the command.
        /// </summary>
        /// <remarks>
        /// For use in unit tests.
        /// </remarks>
        public Task ExecuteAsync()
        {
            return _execute();
        }

        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        public bool CanExecute()
        {
            return ( (ICommand) this ).CanExecute( null );
        }

        #region ICommand implementation
        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <param name="parameter">Ignored.</param>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        bool ICommand.CanExecute( object parameter )
        {
            return _canExecute == null || _canExecute();
        }

        /// <summary>
        /// Defines the method to be called when the command is invoked.
        /// </summary>
        /// <param name="parameter">Ignored.</param>
        async void ICommand.Execute( object parameter )
        {
            OnExecuted();
            await _execute();
        }
        #endregion
    }

    /// <summary>
    /// Asynchronous ICommand that takes a parameter.
    /// </summary>
    public sealed class AsyncCommand<T> : CommandBase, ICommand
    {
        private readonly Func<T, Task> _execute;
        private readonly Func<T, bool> _canExecute;

        /// <summary>
        /// Creates a new AsyncCommand from the specified action and optional condition.
        /// </summary>
        /// <param name="execute">The action to execute when the command is executed.</param>
        /// <param name="canExecute">Optional. The predicate indicating whether the command can be executed.</param>
        public AsyncCommand( Func<T, Task> execute, Expression<Func<T, bool>> canExecute = null )
            : base( canExecute )
        {
            _execute = execute;
            _canExecute = canExecute == null ? null : canExecute.Compile();
        }

        /// <summary>
        /// Asynchronously executes the command.
        /// </summary>
        /// <param name="parameter">Data used by the command. If the command does not require data to be passed, this object can be set to a default value.</param>
        /// <remarks>
        /// For use in unit tests, mostly.
        /// </remarks>
        public Task ExecuteAsync( T parameter )
        {
            OnExecuted();
            return _execute( parameter );
        }

        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        public bool CanExecute( T parameter )
        {
            return _canExecute == null || _canExecute( parameter );
        }

        #region ICommand implementation
        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        bool ICommand.CanExecute( object parameter )
        {
            return CanExecute( (T) parameter );
        }

        /// <summary>
        /// Defines the method to be called when the command is invoked.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        async void ICommand.Execute( object parameter )
        {
            await ExecuteAsync( (T) parameter );
        }
        #endregion
    }
}