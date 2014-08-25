// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
namespace PocketCampus.Common
{
    /// <summary>
    /// Store for credentials, which should be encrypted.
    /// </summary>
    public interface ICredentialsStore : INotifyPropertyChanged
    {
        /// <summary>
        /// Gets or sets the user name.
        /// </summary>
        string UserName { get; set; }

        /// <summary>
        /// Gets or sets the password.
        /// </summary>
        string Password { get; set; }
    }
}