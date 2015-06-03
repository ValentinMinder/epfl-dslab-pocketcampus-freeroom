// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Provides access to a phone, if available.
    /// </summary>
    public interface IPhoneService
    {
        /// <summary>
        /// Indicates whether the current device can make phone calls.
        /// </summary>
        bool CanCall { get; }

        /// <summary>
        /// Calls the specified number, displaying the specified name.
        /// </summary>
        void Call( string name, string number );
    }
}