// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IDeviceIdentifier

#if DEBUG
namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignDeviceIdentifier : IDeviceIdentifier
    {
        public string Current
        {
            get { return "xyz"; }
        }
    }
}
#endif