// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.IsAcademia.Models;

namespace PocketCampus.IsAcademia.Services.Design
{
    public sealed class DesignIsAcademiaService : IIsAcademiaService
    {
        // Ensure the courses are always in the current week
        private static int GetDayIndex( DayOfWeek dow )
        {
            return dow == DayOfWeek.Sunday ? 6 : (int) dow - 1;
        }
        private static DateTime GetWeekStart( DateTime date )
        {
            return date.Date.Subtract( TimeSpan.FromDays( GetDayIndex( date.DayOfWeek ) - GetDayIndex( DayOfWeek.Monday ) ) );
        }
        private static DateTime _weekStart = GetWeekStart( DateTime.Now );


        public Task<ScheduleResponse> GetScheduleAsync( ScheduleRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new ScheduleResponse
                {
                    Status = ResponseStatus.Success,
                    Days = new[] 
                    { 
                        new StudyDay
                        {
                            Day = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day ),
                            Periods = new[]
                            {
                                new Period
                                {
                                    CourseName = "Analyse II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 8, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 10, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CE 6" }
                                },
                                new Period
                                {
                                    CourseName = "Analyse II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 13, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 14, 0, 0 ),
                                    PeriodType = PeriodType.Exercises,
                                    Rooms = new[] { "CO 011", "CO 016", "CO 017", "CO 123", "CO 015", "CO 010", "CO 124"}
                                },
                                new Period
                                {
                                    CourseName = "Théorie et pratique de la programmation",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 14, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 16, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CO 1" }
                                },
                                new Period
                                {
                                    CourseName = "Discrete structures",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 16, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day, 18, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CO 1" }
                                }
                            }
                        },
                        new StudyDay
                        {
                            Day = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1 ),
                            Periods = new[]
                            {
                                // Conflict tests!
                                new Period
                                {
                                    CourseName = "Projet de technologie de l'information",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 8, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 10, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020"}
                                },
                                new Period
                                {
                                    CourseName = "Projet de technologie de l'information",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 9, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 12, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 1", "CO 023", "CO 020" }
                                },
                                new Period
                                {
                                    CourseName = "Projet de technologie de l'information",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 12, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 13, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                                },
                                new Period
                                {
                                    CourseName = "Projet de technologie de l'information",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 12, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 13, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                                },
                                new Period
                                {
                                    CourseName = "Projet de technologie de l'information",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 14, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 15, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                                },
                                new Period
                                {
                                    CourseName = "Discrete structures",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 15, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 17, 0, 0 ),
                                    PeriodType = PeriodType.Exercises,
                                    Rooms = new[] { "CO 1" }
                                },
                                new Period
                                {
                                    CourseName = "Actualité et théories économiques",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 16, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 1, 19, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "SG 1" }
                                }
                            }
                        },
                        new StudyDay
                        {
                            Day = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2 ),
                            Periods = new[]
                            {
                                new Period
                                {
                                    CourseName = "Analyse II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 10, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 11, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CE 6" }
                                },
                                new Period
                                {
                                    CourseName = "Discrete structures",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 13, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 15, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CO 1" }
                                },
                                new Period
                                {
                                    CourseName = "Discrete structures",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 15, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 2, 17, 0, 0 ),
                                    PeriodType = PeriodType.Exercises,
                                    Rooms = new[] { "CM 1 104", "CO 016", "CO 017", "CO 123", "CM 5", "CO 122", "CO 124" }
                                }
                            }
                        },
                        new StudyDay
                        {
                            Day = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3 ),
                            Periods = new[]
                            {
                                new Period
                                {
                                    CourseName = "Analyse II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 8, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 10, 0, 0 ),
                                    PeriodType = PeriodType.Exercises,
                                    Rooms = new[] { "CO 011", "CO 016", "CO 017", "CM 0 9", "CO 015", "CM 0 10", "CO 010" }
                                },
                                new Period
                                {
                                    CourseName = "Systèmes logiques II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 14, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 16, 0, 0 ),
                                    PeriodType = PeriodType.Lecture,
                                    Rooms = new[] { "CO 1" }
                                },
                                new Period
                                {
                                    CourseName = "Systèmes logiques II",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 16, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 3, 18, 0, 0 ),
                                    PeriodType = PeriodType.Lab,
                                    Rooms = new[] { "BC 07-08" }
                                }
                            }
                        },
                        new StudyDay
                        {
                            Day = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 4 ),
                            Periods = new[]
                            {
                                new Period
                                {
                                    CourseName = "Théorie et pratique de la programmation",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 4, 11, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 4, 13, 0, 0 ),
                                    PeriodType = PeriodType.Exercises,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                                },
                                new Period
                                {
                                    CourseName = "Théorie et pratique de la programmation",
                                    Start = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 4, 13, 15, 0 ),
                                    End = new DateTime( _weekStart.Year, _weekStart.Month, _weekStart.Day + 4, 14, 0, 0 ),
                                    PeriodType = PeriodType.Project,
                                    Rooms = new[] { "CO 021", "CO 023", "CO 020" }
                                }
                            }
                        }
                    }
                }
            );
        }
    }
}
#endif