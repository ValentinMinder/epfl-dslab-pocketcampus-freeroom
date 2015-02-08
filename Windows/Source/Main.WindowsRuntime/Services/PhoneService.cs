// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using Windows.ApplicationModel.Calls;

namespace PocketCampus.Main.Services
{
    public sealed class PhoneService : IPhoneService
    {
        public bool CanCall
        {
            get { return true; }
        }

        public void Call( string name, string number )
        {
            PhoneCallManager.ShowPhoneCallUI( number, name );
        }
    }
}