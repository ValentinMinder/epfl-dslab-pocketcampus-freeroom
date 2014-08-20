// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
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
                    FirstName = "George",
                    LastName = "Candea",
                    EmailAddress = "george.candea@epfl.ch",
                    OfficePhoneNumber = "+41 21 6934648",
                    Office = "INN 330",
                    PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=172241",
                    Roles = new Dictionary<string, PersonRole>
                    {
                        { "DSLAB", new PersonRole { Unit = "Laboratoire des systèmes fiables", Title = "Professeur associé" } },
                        { "SIN-ENS", new PersonRole { Unit = "SIN - Enseignement", Title = "Professeur associé" } }
                    },
                    Homepages = new Dictionary<string, string>
                    {
                        { "Personal Home Page", "http://dslab.epfl.ch/people/candea" },
                        { "Personal profile", "http://personnes.epfl.ch/george.candea" }
                    }
                };
            }
        }
#endif
    }
}