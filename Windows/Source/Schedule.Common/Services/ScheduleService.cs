// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Schedule.Models;
using ThriftSharp;

// Plumbing for IScheduleService

namespace PocketCampus.Schedule.Services
{
    public sealed class ScheduleService : ThriftServiceImplementation<IScheduleService>, IScheduleService
    {
        public ScheduleService( IServerAccess access )
            : base( access.CreateCommunication( "isacademia" ) )
        {

        }

        public Task<ScheduleTokenResponse> GetTokenResponseAsync()
        {
            return CallAsync<ScheduleTokenResponse>( x => x.GetTokenResponseAsync );
        }

        // TODO: This is bad. No exception checking in ContinueWith...
        public Task<ScheduleToken> GetTokenAsync()
        {
            return GetTokenResponseAsync().ContinueWith( t => t.Result.Token );
        }

        public Task<ScheduleToken> GetSessionAsync( ScheduleToken token )
        {
            return Task.FromResult( token );
        }

        public Task<ScheduleResponse> GetScheduleAsync( ScheduleRequest request )
        {
            return CallAsync<ScheduleRequest, ScheduleResponse>( x => x.GetScheduleAsync, request );
        }
    }
}