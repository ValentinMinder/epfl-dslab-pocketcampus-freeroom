// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Moodle.Models;
using ThriftSharp;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// The Moodle server service.
    /// </summary>
    [ThriftService( "MoodleService" )]
    public interface IMoodleService
    {
        /// <summary>
        /// Asynchronously gets the courses for the specified request.
        /// </summary>
        /// <remarks>The argument is useless. (not kidding)</remarks>
        [ThriftMethod( "getCoursesListAPI" )]
        Task<CourseListResponse> GetCoursesAsync( [ThriftParameter( 1, "dummy" )] string ignore, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously gets the sections of the specified course.
        /// </summary>
        /// <remarks>The argument is a actually an int. (not kidding either)</remarks>
        [ThriftMethod( "getCourseSectionsAPI" )]
        Task<CourseSectionListResponse> GetCourseSectionsAsync( [ThriftParameter( 1, "courseId" )] string courseId, CancellationToken cancellationToken );
    }
}