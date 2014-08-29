// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ICredentialsStorage

#if DEBUG
using System.ComponentModel;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignCredentialsStorage : ICredentialsStorage
    {
        public string UserName
        {
            get { return "johndoe"; }
            set { }
        }

        public string Password
        {
            get { return "123456"; }
            set { }
        }

#pragma warning disable 0067 // unused event
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif