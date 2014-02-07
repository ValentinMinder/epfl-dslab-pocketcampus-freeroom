// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using Windows.Phone.System.Analytics;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Uniquely identifies a Windows Phone.
    /// </summary>
    public sealed class DeviceIdentifier : IDeviceIdentifier
    {
        /// <summary>
        /// Gets the identifier of the user's device.
        /// </summary>
        public string Current
        {
            get { return HostInformation.PublisherHostId; }
        }
    }
}