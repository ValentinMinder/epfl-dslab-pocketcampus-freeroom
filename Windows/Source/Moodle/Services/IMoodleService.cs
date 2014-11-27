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
        [ThriftMethod( "getCourses" )]
        Task<CoursesResponse> GetCoursesAsync( [ThriftParameter( 1, "request" )] CoursesRequest request, CancellationToken token );

        [ThriftMethod( "getSections" )]
        Task<CourseSectionsResponse> GetSectionsAsync( [ThriftParameter( 1, "request" )] CourseSectionsRequest request, CancellationToken token );
    }
}