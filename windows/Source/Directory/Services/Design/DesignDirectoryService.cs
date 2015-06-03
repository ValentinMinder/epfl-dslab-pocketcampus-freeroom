// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IDirectoryService

#if DEBUG
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Directory.Models;

namespace PocketCampus.Directory.Services.Design
{
    public sealed class DesignDirectoryService : IDirectoryService
    {
        public Task<SearchResponse> SearchAsync( SearchRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new SearchResponse
                {
                    Status = SearchStatus.Success,
                    Results = new[] 
                    {
                        new Person
                        {
                            FirstName = "George",
                            LastName = "Candea",
                            EmailAddress = "george.candea@epfl.ch",
                            OfficePhoneNumber = "+41 21 6934648",
                            Office = "INN 330",
                            PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=172241",
                            Roles = new Dictionary<string,PersonRole>
                            {
                                { "DSLAB", new PersonRole { Unit = "Laboratoire des systèmes fiables", Title = "Professeur associé" } },
                                { "SIN-ENS", new PersonRole { Unit = "SIN - Enseignement", Title = "Professeur associé" } }
                            },
                            Homepages = new Dictionary<string,string>
                            {
                                { "Personal Home Page", "http://dslab.epfl.ch/people/candea" },
                                { "Personal profile", "http://personnes.epfl.ch/george.candea" }
                            }
                        },
                        new Person
                        {
                            FirstName = "Silviu",
                            LastName = "Andrica",
                            EmailAddress = "silviu.andrica@epfl.ch",
                            OfficePhoneNumber = "+41 21 6938188",
                            Office = "INN 329",
                            PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=184038",
                            Roles = new Dictionary<string,PersonRole>
                            {
                                { "DSLAB", new PersonRole { Unit = "Laboratoire des systèmes fiables", Title = "Post-Doctorant" } }
                            },
                            Homepages = new Dictionary<string,string>
                            {
                                { "Personal profile", "http://personnes.epfl.ch/silviu.andrica" }
                            }
                        },
                        new Person
                        {
                            FirstName = "Loïc",
                            LastName = "Gardiol",
                            EmailAddress = "loic.gardiol@epfl.ch",
                            OfficePhoneNumber = "+41 21 6938188",
                            Office = "INN 329",
                            PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=185853",
                            Roles = new Dictionary<string,PersonRole>
                            {
                                { "DSLAB", new PersonRole { Unit = "Laboratoire des systèmes fiables", Title = "Etudiant en projet" } }
                            },
                            Homepages = new Dictionary<string,string>
                            {
                                { "Personal Home Page", "http://loicgardiol.ch/" },
                                { "Personal profile", "http://personnes.epfl.ch/loic.gardiol" }
                            }
                        },
                        new Person
                        {
                            FirstName = "Amer",
                            LastName = "Chamseddine",
                            EmailAddress = "amer.chamseddine@epfl.ch",
                            OfficePhoneNumber = "+41 21 6938188",
                            Office = "INN 329",
                            PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=211338",
                            Roles = new Dictionary<string,PersonRole>
                            {
                                { "DSLAB", new PersonRole { Unit = "Laboratoire des systèmes fiables", Title = "Etudiant en projet" } },
                                { "CHILI", new PersonRole { Unit = "Laboratoire d'ergonomie éducative", Title = "Assistant-doctorant" } },
                                { "EDOC-IC", new PersonRole { Unit = "Programme boursier en informatique, communications et information", Title = "Assistant-doctorant" } },
                                { "EDIC", new PersonRole { Unit = "Programme doctoral en informatique, communications et information", Title = "Etudiant" } }
                            },
                            Homepages = new Dictionary<string,string>
                            {
                                { "Personal profile", "http://personnes.epfl.ch/amer.chamseddine" }
                            }
                        },
                        new Person
                        {
                            FirstName = "Solal",
                            LastName = "Pirelli",
                            EmailAddress = "solal.pirelli@epfl.ch",
                            PictureUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=223572",
                            Roles = new Dictionary<string,PersonRole>
                            {
                                { "SIN-BA5", new PersonRole { Unit = "Section d'informatique - Bachelor semestre 5", Title = "Etudiant" } }
                            },
                            Homepages = new Dictionary<string,string>
                            {
                                { "Personal profile", "http://personnes.epfl.ch/solal.pirelli" }
                            }
                        }
                    }
                }
            );
        }
    }
}
#endif