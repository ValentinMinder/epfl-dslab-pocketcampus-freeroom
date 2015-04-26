// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.ComponentModel;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Stores settings related to the server connection.
    /// </summary>
    public interface IServerSettings : INotifyPropertyChanged
    {
        /// <summary>
        /// Gets or sets the server configuration used to connect.
        /// </summary>
        ServerConfiguration Configuration { get; set; }

        /// <summary>
        /// Gets or sets the session for the server.
        /// </summary>
        string Session { get; set; }

        /// <summary>
        /// Gets or sets the per-plugin sessions for the old authentication model.
        /// </summary>
        Dictionary<string, string> Sessions { get; set; }

        /// <summary>
        /// Gets or sets the user's authentication status.
        /// </summary>
        SessionStatus SessionStatus { get; set; }
    }
}