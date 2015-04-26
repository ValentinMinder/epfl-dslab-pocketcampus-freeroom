// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events.Services
{
    /// <summary>
    /// Lets the user scan QR codes for event pool and items.
    /// </summary>
    public interface ICodeScanner
    {
        /// <summary>
        /// Requests a QR code scan to the user.
        /// </summary>
        void ScanCode();
    }
}