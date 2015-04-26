// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Uniquely identifies a device.
    /// </summary>
    public interface IDeviceIdentifier
    {
        /// <summary>
        /// Gets the identifier of the user's device.
        /// </summary>
        string Current { get; }
    }
}