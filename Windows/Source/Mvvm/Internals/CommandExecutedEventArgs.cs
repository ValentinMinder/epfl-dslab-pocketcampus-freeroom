// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Mvvm.Internals
{
    /// <summary>
    /// Provides data for the CommandBase.Executed event.
    /// </summary>
    internal sealed class CommandExecutedEventArgs : EventArgs
    {
        /// <summary>
        /// Gets the parameter with which the command was executed.
        /// </summary>
        public object Parameter { get; private set; }


        /// <summary>
        /// Creates a new CommandExecutedEventArgs.
        /// </summary>
        public CommandExecutedEventArgs( object parameter )
        {
            Parameter = parameter;
        }
    }
}