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
        public MoodleService( IServerConfiguration config )
            : base( config.CreateCommunication( "moodle" ) )
        {

        }

        public Task<TequilaToken> GetTokenAsync()
        {
            return CallAsync<TequilaToken>( x => x.GetTokenAsync );
        }

        public Task<MoodleSession> GetSessionAsync( TequilaToken token )
        {
            return CallAsync<TequilaToken, MoodleSession>( x => x.GetSessionAsync, token );
        }

        public Task<CourseListResponse> GetCoursesAsync( MoodleRequest request )
        {
            return CallAsync<MoodleRequest, CourseListResponse>( x => x.GetCoursesAsync, request );
        }

        public Task<CourseSectionListResponse> GetCourseSectionsAsync( MoodleRequest request )
        {
            return CallAsync<MoodleRequest, CourseSectionListResponse>( x => x.GetCourseSectionsAsync, request );
        }
    }
}