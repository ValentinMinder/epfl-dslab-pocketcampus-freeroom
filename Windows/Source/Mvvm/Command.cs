// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq.Expressions;
using System.Windows.Input;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Parameterless ICommand.
    /// </summary>
    public sealed class Command : CommandBase, ICommand
    {
        private readonly Action _execute;
        private readonly Func<bool> _canExecute;

        /// <summary>
        /// Creates a new Command from the specified action and optional condition.
        /// </summary>
        /// <param name="execute">The action to execute when the command is executed.</param>
        /// <param name="canExecute">Optional. The predicate indicating whether the command can be executed.</param>
        public Command( object owner, Action execute, Expression<Func<bool>> canExecute = null )
            : base( owner, canExecute )
        {
            _execute = execute;
            _canExecute = canExecute == null ? null : canExecute.Compile();
        }

        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        public bool CanExecute()
        {
            return ( (ICommand) this ).CanExecute( null );
        }

        /// <summary>
        /// Defines the method to be called when the command is invoked.
        /// </summary>
        public void Execute()
        {
            ( (ICommand) this ).Execute( null );
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
        void ICommand.Execute( object parameter )
        {
            OnExecuted();
            _execute();
        }
        #endregion
    }

    /// <summary>
    /// ICommand that takes a parameter.
    /// </summary>
    public sealed class Command<T> : CommandBase, ICommand
    {
        private readonly Action<T> _execute;
        private readonly Func<T, bool> _canExecute;

        /// <summary>
        /// Creates a new Command from the specified action and optional condition.
        /// </summary>
        /// <param name="execute">The action to execute when the command is executed.</param>
        /// <param name="canExecute">Optional. The predicate indicating whether the command can be executed.</param>
        public Command( object owner, Action<T> execute, Expression<Func<T, bool>> canExecute = null )
            : base( owner, canExecute )
        {
            _execute = execute;
            _canExecute = canExecute == null ? null : canExecute.Compile();
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

        /// <summary>
        /// Defines the method to be called when the command is invoked.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        public void Execute( T parameter )
        {
            OnExecuted( parameter );
            _execute( parameter );
        }

        #region ICommand implementation
        /// <summary>
        /// Defines the method that determines whether the command can execute in its current state.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        /// <returns>True if this command can be executed; otherwise, false.</returns>
        bool ICommand.CanExecute( object parameter )
        {
            if ( parameter is T )
            {
                return CanExecute( (T) parameter );
            }

            return false;
        }

        /// <summary>
        /// Defines the method to be called when the command is invoked.
        /// </summary>
        /// <param name="parameter">Data used by the command.</param>
        void ICommand.Execute( object parameter )
        {
            if ( parameter is T )
            {
                Execute( (T) parameter );
            }
        }
        #endregion
    }
}