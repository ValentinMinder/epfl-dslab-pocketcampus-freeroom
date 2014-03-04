// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using ThriftSharp;

// Plumbing for IMoodleService

namespace PocketCampus.Moodle.Services
{
    public sealed class MoodleService : ThriftServiceImplementation<IMoodleService>, IMoodleService
    {
        public MoodleService( IServerAccess access )
            : base( access.CreateCommunication( "moodle" ) )
        {

        }

        public Task<CourseListResponse> GetCoursesAsync( string dummy )
        {
            return CallAsync<string, CourseListResponse>( x => x.GetCoursesAsync, dummy );
        }

        public Task<CourseSectionListResponse> GetCourseSectionsAsync( string courseId )
        {
            return CallAsync<string, CourseSectionListResponse>( x => x.GetCourseSectionsAsync, courseId );
        }
    }
}