// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IContactsService

#if DEBUG
using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services.Design
{
    public sealed class DesignContactsService : IContactsService
    {
        public void AddAsContact( Person person ) { }
    }
}
#endif