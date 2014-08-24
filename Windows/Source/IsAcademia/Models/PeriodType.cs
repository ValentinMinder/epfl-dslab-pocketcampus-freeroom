// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// The period types.
    /// </summary>
    [ThriftEnum]
    public enum PeriodType
    {
        /// <summary>
        /// A lecture, usually in only 1 room.
        /// </summary>
        Lecture = 0,

        /// <summary>
        /// An exercise session, usually in many rooms.
        /// </summary>
        Exercises = 1,

        /// <summary>
        /// A lab session.
        /// </summary>
        Lab = 2,

        /// <summary>
        /// A project session.
        /// </summary>
        /// <remarks>
        /// Some periods marked as "project" are actually exercises.
        /// </remarks>
        Project = 3,

        /// <summary>
        /// An oral exam.
        /// </summary>
        OralExam = 4,

        /// <summary>
        /// A written exam.
        /// </summary>
        WrittenExam = 5
    }
}