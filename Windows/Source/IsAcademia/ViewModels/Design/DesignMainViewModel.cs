// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.IsAcademia.Models;
using ThinMvvm;

// Design data for MainViewModel

namespace PocketCampus.IsAcademia.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public DataStatus DataStatus { get { return DataStatus.DataLoaded; } }
        public CacheStatus CacheStatus { get { return CacheStatus.OptedOut; } }

        public StudyDay[] Days
        {
            get
            {
                return new[]
                {
                    new StudyDay
                    {
                        Day = new DateTime( 2013, 5, 6 ),
                        Periods = new[]
                        {
                            new Period
                            {
                                CourseName = "Analyse II",
                                Start = new DateTime( 2013, 5, 6, 8, 15, 0 ),
                                End = new DateTime( 2013, 5, 6, 10, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CE 6" }
                            },
                            new Period
                            {
                                CourseName = "Analyse II",
                                Start = new DateTime( 2013, 5, 6, 13, 15, 0 ),
                                End = new DateTime( 2013, 5, 6, 14, 0, 0 ),
                                PeriodType = PeriodType.Exercises,
                                Rooms = new[] { "CO 011", "CO 016", "CO 017", "CO 123", "CO 015", "CO 010", "CO 124"}
                            },
                            new Period
                            {
                                CourseName = "Théorie et pratique de la programmation",
                                Start = new DateTime( 2013, 5, 6, 14, 15, 0 ),
                                End = new DateTime( 2013, 5, 6, 16, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CO 1" }
                            },
                            new Period
                            {
                                CourseName = "Discrete structures",
                                Start = new DateTime( 2013, 5, 6, 16, 15, 0 ),
                                End = new DateTime( 2013, 5, 6, 18, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CO 1" }
                            }
                        }
                    },
                    new StudyDay
                    {
                        Day = new DateTime( 2013, 5, 7 ),
                        Periods = new[]
                        {
                            // Conflict tests!
                            new Period
                            {
                                CourseName = "Projet de technologie de l'information",
                                Start = new DateTime( 2013, 5, 7, 8, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 10, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020"}
                            },
                            new Period
                            {
                                CourseName = "Projet de technologie de l'information",
                                Start = new DateTime( 2013, 5, 7, 9, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 12, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 1", "CO 023", "CO 020" }
                            },
                            new Period
                            {
                                CourseName = "Projet de technologie de l'information",
                                Start = new DateTime( 2013, 5, 7, 12, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 13, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                            },
                            new Period
                            {
                                CourseName = "Projet de technologie de l'information",
                                Start = new DateTime( 2013, 5, 7, 12, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 13, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                            },
                            new Period
                            {
                                CourseName = "Projet de technologie de l'information",
                                Start = new DateTime( 2013, 5, 7, 14, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 15, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                            },
                            new Period
                            {
                                CourseName = "Discrete structures",
                                Start = new DateTime( 2013, 5, 7, 15, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 17, 0, 0 ),
                                PeriodType = PeriodType.Exercises,
                                Rooms = new[] { "CO 1" }
                            },
                            new Period
                            {
                                CourseName = "Actualité et théories économiques",
                                Start = new DateTime( 2013, 5, 7, 16, 15, 0 ),
                                End = new DateTime( 2013, 5, 7, 19, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "SG 1" }
                            }
                        }
                    },
                    new StudyDay
                    {
                        Day = new DateTime( 2013, 5, 8 ),
                        Periods = new[]
                        {
                            new Period
                            {
                                CourseName = "Analyse II",
                                Start = new DateTime( 2013, 5, 8, 10, 15, 0 ),
                                End = new DateTime( 2013, 5, 8, 11, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CE 6" }
                            },
                            new Period
                            {
                                CourseName = "Discrete structures",
                                Start = new DateTime( 2013, 5, 8, 13, 15, 0 ),
                                End = new DateTime( 2013, 5, 8, 15, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CO 1" }
                            },
                            new Period
                            {
                                CourseName = "Discrete structures",
                                Start = new DateTime( 2013, 5, 8, 15, 15, 0 ),
                                End = new DateTime( 2013, 5, 8, 17, 0, 0 ),
                                PeriodType = PeriodType.Exercises,
                                Rooms = new[] { "CM 1 104", "CO 016", "CO 017", "CO 123", "CM 5", "CO 122", "CO 124" }
                            }
                        }
                    },
                    new StudyDay
                    {
                        Day = new DateTime( 2013, 5, 9 ),
                        Periods = new[]
                        {
                            new Period
                            {
                                CourseName = "Analyse II",
                                Start = new DateTime( 2013, 5, 9, 8, 15, 0 ),
                                End = new DateTime( 2013, 5, 9, 10, 0, 0 ),
                                PeriodType = PeriodType.Exercises,
                                Rooms = new[] { "CO 011", "CO 016", "CO 017", "CM 0 9", "CO 015", "CM 0 10", "CO 010" }
                            },
                            new Period
                            {
                                CourseName = "Systèmes logiques II",
                                Start = new DateTime( 2013, 5, 9, 14, 15, 0 ),
                                End = new DateTime( 2013, 5, 9, 16, 0, 0 ),
                                PeriodType = PeriodType.Lecture,
                                Rooms = new[] { "CO 1" }
                            },
                            new Period
                            {
                                CourseName = "Systèmes logiques II",
                                Start = new DateTime( 2013, 5, 9, 16, 15, 0 ),
                                End = new DateTime( 2013, 5, 9, 18, 0, 0 ),
                                PeriodType = PeriodType.Lab,
                                Rooms = new[] { "BC 07-08" }
                            }
                        }
                    },
                    new StudyDay
                    {
                        Day = new DateTime( 2013, 5, 10 ),
                        Periods = new[]
                        {
                            new Period
                            {
                                CourseName = "Théorie et pratique de la programmation",
                                Start = new DateTime( 2013, 5, 10, 11, 15, 0 ),
                                End = new DateTime( 2013, 5, 10, 13, 0, 0 ),
                                PeriodType = PeriodType.Exercises,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                            },
                            new Period
                            {
                                CourseName = "Théorie et pratique de la programmation",
                                Start = new DateTime( 2013, 5, 10, 13, 15, 0 ),
                                End = new DateTime( 2013, 5, 10, 14, 0, 0 ),
                                PeriodType = PeriodType.Project,
                                Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                            }
                        }
                    }
                };
            }
        }

        public DateTime WeekDate { get { return new DateTime( 2013, 5, 6 ); } }
#endif
    }
}