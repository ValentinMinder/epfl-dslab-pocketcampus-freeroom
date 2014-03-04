// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// The period types.
    /// </summary>
    [ThriftEnum( "StudyPeriodType" )]
    public enum PeriodType
    {
        /// <summary>
        /// A lecture, usually in only 1 room.
        /// </summary>
        [ThriftEnumMember( "LECTURE", 0 )]
        Lecture,

        /// <summary>
        /// An exercise session, usually in many rooms.
        /// </summary>
        [ThriftEnumMember( "EXERCISES", 1 )]
        Exercises,

        /// <summary>
        /// A lab session.
        /// </summary>
        [ThriftEnumMember( "LAB", 2 )]
        Lab,

        /// <summary>
        /// A project session.
        /// </summary>
        /// <remarks>
        /// Some periods marked as "project" are actually exercises.
        /// </remarks>
        [ThriftEnumMember( "PROJECT", 3 )]
        Project
    }
}