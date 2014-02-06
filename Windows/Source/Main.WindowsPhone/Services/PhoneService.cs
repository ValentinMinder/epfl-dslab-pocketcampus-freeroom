// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Phone.Tasks;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Provides access to the phone component of a Windows Phone.
    /// </summary>
    public sealed class PhoneService : IPhoneService
    {
        /// <summary>
        /// Windows Phones are phones; they can make calls.
        /// </summary>
        public bool CanCall
        {
            get { return true; }
        }

        /// <summary>
        /// Calls the specified number, displaying the specified name.
        /// </summary>
        public void Call( string name, string number )
        {
            new PhoneCallTask { DisplayName = name, PhoneNumber = number }.Show();
        }
    }
}