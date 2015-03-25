// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services
{
    public sealed class ContactsService : IContactsService
    {
        public void AddAsContact( Person person )
        {
            // FRAMEWORK MISSING FEATURE: Windows Phone doesn't implement ContactPickerUI yet.
            throw new NotSupportedException();
        }
    }
}