// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Directory.Models;

// Design data for PersonViewModel

namespace PocketCampus.Directory.ViewModels.Design
{
    public sealed class DesignPersonViewModel
    {
#if DEBUG
        public Person Person
        {
            get
            {
                return new Person
                {
                    FirstName = "John",
                    LastName = "Doe",
                    SciperNumber = "123456",
                    EmailAddress = "john.doe@epfl.ch",
                    Website = "http://example.org/john.doe",
                    PrivatePhoneNumber = "+0987 654 321",
                    OfficePhoneNumber = "+1234 567 890",
                    Office = "ABC 123",
                    GasparIdentifier = "johndoe",
                    Units = new[] { "ABC-1", "DEF-2" },
                    PictureUrl = "http://placekitten.com/150/200"
                };
            }
        }
#endif
    }
}