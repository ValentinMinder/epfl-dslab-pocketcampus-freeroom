// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPhoneService

#if DEBUG
namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignPhoneService : IPhoneService
    {
        public bool CanCall
        {
            get { return false; }
        }

        public void Call( string name, string number ) { }
    }
}
#endif