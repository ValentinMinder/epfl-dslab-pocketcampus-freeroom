// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
using Microsoft.Phone.Tasks;
using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services
{
    /// <summary>
    /// Allows editing the user's contact list.
    /// </summary>
    public sealed class ContactsService : IContactsService
    {
        /// <summary>
        /// Adds the specified person as a contact.
        /// </summary>
        public void AddAsContact( Person person )
        {
            new SaveContactTask
            {
                FirstName = person.FirstName,
                LastName = person.LastName,
                HomePhone = person.PrivatePhoneNumber,
                WorkPhone = person.OfficePhoneNumber,
                WorkEmail = person.EmailAddress,
                Website = person.Homepages.Any() ? person.Homepages.First().Value : null
            }.Show();
        }
    }
}