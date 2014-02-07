// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Directory.Models;

// Design data for MainViewModel

namespace PocketCampus.Directory.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public string Query { get { return "PocketCampus guys"; } }

        public Person[] SearchResults
        {
            get
            {
                return new[]
                {
                    new Person
                    {
                        FirstName = "George",
                        LastName = "Candea",
                        SciperNumber = "172241",
                        EmailAddress = "george.candea@epfl.ch",
                        Website = "http://dslab.epfl.ch/people/candea",
                        OfficePhoneNumber = "+41 21 6934648",
                        Office = "INN 330",
                        GasparIdentifier = "candea",
                        Units = new[] { "DSLAB", "SIN-ENS" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=172241"
                    },
                    new Person
                    {
                        FirstName = "Silviu",
                        LastName = "Andrica",
                        SciperNumber = "184038",
                        EmailAddress = "silviu.andrica@epfl.ch",
                        Website = "http://personnes.epfl.ch/silviu.andrica",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        GasparIdentifier = "andrica",
                        Units = new[] { "DSLAB", "EDIC" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=184038"
                    },
                    new Person
                    {
                        FirstName = "Loïc",
                        LastName = "Gardiol",
                        SciperNumber = "185853",
                        EmailAddress = "loic.gardiol@epfl.ch",
                        Website = "http://www.loicgardiol.ch/",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        GasparIdentifier = "gardiol",
                        Units = new[] { "IN-PME", "DSLAB" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=185853"
                    },
                    new Person
                    {
                        FirstName = "Amer",
                        LastName = "Chamseddine",
                        SciperNumber = "211338",
                        EmailAddress = "amer.chamseddine@epfl.ch",
                        Website = "http://www.accandme.com/",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        GasparIdentifier = "chamsedd",
                        Units = new[] { "IN-PME", "DSLAB", "EDOC-IC", "EDIC" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=211338"
                    },
                    new Person
                    {
                        FirstName = "Solal",
                        LastName = "Pirelli",
                        SciperNumber = "223572",
                        EmailAddress = "solal.pirelli@epfl.ch",
                        Website = "http://personnes.epfl.ch/solal.pirelli",
                        GasparIdentifier = "pirelli",
                        Units = new[] { "IN-BA3" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=223572"
                    }
                };
            }
        }

        public bool AnySearchResults { get { return true; } }
#endif
    }
}