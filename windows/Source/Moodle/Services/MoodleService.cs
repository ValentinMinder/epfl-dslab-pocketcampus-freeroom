// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using ThriftSharp;

// Plumbing for IMoodleService

namespace PocketCampus.Moodle.Services
{
    public sealed class MoodleService : ThriftServiceImplementation<IMoodleService>, IMoodleService
    {
        public MoodleService( IServerAccess access ) : base( access.CreateCommunication( "moodle" ) ) { }


        public Task<CoursesResponse> GetCoursesAsync( CoursesRequest request, CancellationToken token )
        {
            return CallAsync<CoursesRequest, CancellationToken, CoursesResponse>( x => x.GetCoursesAsync, request, token );
        }

        public Task<CourseSectionsResponse> GetSectionsAsync( CourseSectionsRequest request, CancellationToken token )
        {
            return CallAsync<CourseSectionsRequest, CancellationToken, CourseSectionsResponse>( x => x.GetSectionsAsync, request, token );
        }

        public Task<PrintFileResponse> PrintFileAsync( PrintFileRequest request, CancellationToken token )
        {
            return CallAsync<PrintFileRequest, CancellationToken, PrintFileResponse>( x => x.PrintFileAsync, request, token );
        }
    }
}