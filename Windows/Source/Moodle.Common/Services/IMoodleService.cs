// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using ThriftSharp;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// The Moodle server service.
    /// </summary>
    [ThriftService( "MoodleService" )]
    public interface IMoodleService : ITwoStepAuthenticator<TequilaToken, MoodleSession>
    {
        /// <summary>
        /// First authentication step: asynchronously gets a token.
        /// </summary>
        [ThriftMethod( "getTequilaTokenForMoodle" )]
        new Task<TequilaToken> GetTokenAsync();

        /// <summary>
        /// Second authentication step: asynchronously gets a session from an authenticated token.
        /// </summary>
        [ThriftMethod( "getMoodleSession" )]
        new Task<MoodleSession> GetSessionAsync( [ThriftParameter( 1, "iTequilaToken" )] TequilaToken token );

        /// <summary>
        /// Asynchronously gets the courses for the specified request.
        /// </summary>
        [ThriftMethod( "getCoursesList" )]
        Task<CourseListResponse> GetCoursesAsync( [ThriftParameter( 1, "iRequest" )] MoodleRequest request );

        /// <summary>
        /// Asynchronously gets the sections of the specified course.
        /// </summary>
        [ThriftMethod( "getCourseSections" )]
        Task<CourseSectionListResponse> GetCourseSectionsAsync( [ThriftParameter( 1, "iRequest" )] MoodleRequest request );
    }
}