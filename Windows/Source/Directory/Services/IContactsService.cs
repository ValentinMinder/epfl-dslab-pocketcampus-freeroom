// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services
{
    /// <summary>
    /// Allows editing the user's contact list.
    /// </summary>
    public interface IContactsService
    {
        /// <summary>
        /// Adds the specified person as a contact.
        /// </summary>
        void AddAsContact( Person person );
    }
}