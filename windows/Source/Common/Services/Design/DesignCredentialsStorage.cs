// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ICredentialsStorage

#if DEBUG
namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignCredentialsStorage : ICredentialsStorage
    {
        public string UserName
        {
            get { return "johndoe"; }
        }

        public string Password
        {
            get { return "123456"; }
        }

        public void SetCredentials( string userName, string password ) { }

        public void DeleteCredentials() { }
    }
}
#endif