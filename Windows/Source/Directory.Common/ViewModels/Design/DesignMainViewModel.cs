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

        public bool IsLoadingMoreResults { get { return false; } }

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
                        EmailAddress = "george.candea@epfl.ch",
                        Website = "http://dslab.epfl.ch/people/candea",
                        OfficePhoneNumber = "+41 21 6934648",
                        Office = "INN 330",
                        Units = new[] { "DSLAB", "SIN-ENS" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=172241"
                    },
                    new Person
                    {
                        FirstName = "Silviu",
                        LastName = "Andrica",
                        EmailAddress = "silviu.andrica@epfl.ch",
                        Website = "http://personnes.epfl.ch/silviu.andrica",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        Units = new[] { "DSLAB" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=184038"
                    },
                    new Person
                    {
                        FirstName = "Loïc",
                        LastName = "Gardiol",
                        EmailAddress = "loic.gardiol@epfl.ch",
                        Website = "http://www.loicgardiol.ch/",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        Units = new[] { "DSLAB" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=185853"
                    },
                    new Person
                    {
                        FirstName = "Amer",
                        LastName = "Chamseddine",
                        EmailAddress = "amer.chamseddine@epfl.ch",
                        Website = "http://www.accandme.com/",
                        OfficePhoneNumber = "+41 21 6938188",
                        Office = "INN 329",
                        Units = new[] { "DSLAB", "EDOC-IC", "EDIC", "CHILI" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=211338"
                    },
                    new Person
                    {
                        FirstName = "Solal",
                        LastName = "Pirelli",
                        EmailAddress = "solal.pirelli@epfl.ch",
                        Website = "http://personnes.epfl.ch/solal.pirelli",
                        Units = new[] { "IN-BA4" },
                        PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=223572"
                    }
                };
            }
        }

        public bool AnySearchResults { get { return true; } }
#endif
    }
}