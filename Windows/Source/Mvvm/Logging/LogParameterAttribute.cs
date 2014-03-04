// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Mvvm.Logging
{
    /// <summary>
    /// Marks Commands to specify the parameter to be used with their event.
    /// </summary>
    [AttributeUsage( AttributeTargets.Property )]
    public sealed class LogParameterAttribute : Attribute
    {
        /// <summary>
        /// Refers to the parameter of the command inside a path.
        /// </summary>
        internal const string ParameterName = "$Param";

        /// <summary>
        /// The path separator for parameter paths.
        /// </summary>
        internal const char PathSeparator = '.';

        /// <summary>
        /// Gets the path to the parameter.
        /// Use $Param to refer to the command parameter itself.
        /// </summary>
        public string ParameterPath { get; private set; }


        /// <summary>
        /// Creates a new LogParameterAttribute.
        /// </summary>
        public LogParameterAttribute( string parameterPath )
        {
            ParameterPath = parameterPath;
        }
    }
}