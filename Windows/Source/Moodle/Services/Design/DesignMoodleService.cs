// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for IMoodleService

#if DEBUG
using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services.Design
{
    public sealed class DesignMoodleService : IMoodleService
    {
        public Task<CoursesResponse> GetCoursesAsync( CoursesRequest request, CancellationToken token )
        {
            return Task.FromResult( new CoursesResponse
            {
                Status = MoodleStatus.Success,
                Courses = new[]
                {
                    new Course
                    {
                        Id = 14174,
                        Name = "Theory of computation",
                    },
                    new Course
                    {
                        Id = 14153,
                        Name = "Architecture des ordinateurs II",
                    },
                    new Course
                    {
                        Id = 14084,
                        Name = "Architecture des ordinateurs I",
                    },
                    new Course
                    {
                        Id = 13768,
                        Name = "Algorithms",
                    },
                    new Course
                    {
                        Id = 2671,
                        Name = "Concurrence",
                    },
                    new Course
                    {
                        Id = 6731,
                        Name = "Programmation orientée système",
                    },
                    new Course
                    {
                        Id = 13726,
                        Name = "Electronique I",
                    },
                    new Course
                    {
                        Id = 14155,
                        Name = "Physique Générale II (pour IC)",
                    },
                    new Course
                    {
                        Id = 14030,
                        Name = "Physique Générale I pour IC",
                    },
                    new Course
                    {
                        Id = 5191,
                        Name = "Circuits et Systèmes I",
                    },
                    new Course
                    {
                        Id = 13839,
                        Name = "Santé, Population, Société",
                    }
                }
            } );
        }

        public Task<CourseSectionsResponse> GetSectionsAsync( CourseSectionsRequest request, CancellationToken token )
        {
            return Task.FromResult( new CourseSectionsResponse
            {
                Status = MoodleStatus.Success,
                Sections = new[] 
                { 
                    new CourseSection
                    {
                        Title = "General",
                        Details = @"<div class=""no-overflow"">
<p>Bienvenue au cours Santé, Populations, Société.</p>
<p>Le cours a lieu le mardi de 15h à 17h à la salle CE2</p><p>Les examens auront lieu le 27 mai à la salle CM2 (prendre avec soi un crayon noir)</p>
<p>&nbsp;</p></div>",
                        Resources = new MoodleResource[0]
                    },
                    new CourseSection
                    {
                        StartDate = new DateTime( 2014, 02, 17 ),
                        EndDate = new DateTime( 2014, 02, 23 ),
                        Details = @"<p>Epidemiology and its methods</p>",
                        Resources = new[]
                        {
                            new MoodleResource
                            {
                                File = new MoodleFile
                                {
                                    Name = "Paccaud Epidemiology and its methods",
                                    Extension="pdf",
                                    IconUrl = "http://moodlearchives.epfl.ch/2013-2014/theme/image.php/epfl_cdh/core/1405587910/f/pdf-{size}",
                                    DownloadUrl = ""
                                }
                            },
                            // adapted from the original data
                            new MoodleResource
                            {
                                Folder = new MoodleFolder
                                {
                                    Name = "Example folder",
                                    Files = new[]
                                    {
                                        new MoodleFile
                                        {
                                            Name = "Bodenmann Migrations and Health 140520",
                                            Extension="pdf",
                                            DownloadUrl = ""
                                        }
                                    }
                                }
                            },
                            new MoodleResource
                            {
                                Link = new MoodleLink
                                {
                                    Name = "Example link",
                                    Url = ""
                                }
                            }
                        }
                    },
                    new CourseSection
                    {
                        StartDate = new DateTime( 2014, 02, 24 ),
                        EndDate = new DateTime( 2014, 03, 02 ),
                        Details = @"<p>Demography, Aging and Health</p>",
                        Resources = new[]
                        {
                            new MoodleResource
                            {
                                File = new MoodleFile
                                {
                                    Name = "Paccaud Demography, Aging and Health 140225-rt",
                                    Extension="pdf",
                                    IconUrl = "http://moodlearchives.epfl.ch/2013-2014/theme/image.php/epfl_cdh/core/1405587910/f/pdf-{size}",
                                    DownloadUrl = ""
                                }
                            }
                        }
                    },
                    new CourseSection
                    {
                        StartDate = new DateTime( 2014, 03, 03 ),
                        EndDate = new DateTime( 2014, 03, 09 ),
                        Details = @"<p>Economics of health and health care</p>",
                        Resources = new[]
                        {
                            new MoodleResource
                            {
                                File = new MoodleFile
                                {
                                    Name = "Maurer J Economics of health and health care 140304",
                                    Extension="pdf",
                                    IconUrl = "http://moodlearchives.epfl.ch/2013-2014/theme/image.php/epfl_cdh/core/1405587910/f/pdf-{size}",
                                    DownloadUrl = ""
                                }
                            }
                        }
                    },
                    new CourseSection
                    {
                        StartDate = new DateTime( 2014, 05, 05 ),
                        EndDate = new DateTime( 2014, 05, 11 ),
                        Details = @"<p>Social Environment and Health</p>",
                        Resources = new[]
                        {
                            new MoodleResource
                            {
                                File = new MoodleFile
                                {
                                    Name = "Stringhini S Social Environment and Health 140506",
                                    Extension="pdf",
                                    IconUrl = "http://moodlearchives.epfl.ch/2013-2014/theme/image.php/epfl_cdh/core/1405587910/f/powerpoint-{size}",
                                    DownloadUrl = ""
                                }
                            }
                        }
                    }
                }
            } );
        }

        public Task<PrintFileResponse> PrintFileAsync( PrintFileRequest request, CancellationToken token )
        {
            return Task.FromResult( new PrintFileResponse
            {
                Status = MoodleStatus.NetworkError
            } );
        }
    }
}
#endif