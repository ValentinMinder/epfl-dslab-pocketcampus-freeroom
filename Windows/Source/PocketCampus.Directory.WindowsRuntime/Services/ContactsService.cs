using System;
using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services
{
    public sealed class ContactsService : IContactsService
    {
        public void AddAsContact( Person person )
        {
            // TODO: Windows Phone doesn't implement ContactPickerUI yet :-/
            throw new NotSupportedException();
        }
    }
}