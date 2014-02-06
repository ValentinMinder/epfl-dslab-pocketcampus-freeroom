// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Services
{
    /// <summary>
    /// The schedule server service.
    /// </summary>
    [ThriftService( "IsAcademiaService" )]
    public interface IScheduleService : ITwoStepAuthenticator<ScheduleToken, ScheduleToken>
    {
        /// <summary>
        /// Asynchronously gets a token.
        /// </summary>
        /// <remarks>
        /// Single-step authentication (for now).
        /// </remarks>
        [ThriftMethod( "getScheduleToken" )]
        Task<ScheduleTokenResponse> GetTokenResponseAsync();

        /// <summary>
        /// Asynchronously gets the schedule for the specified request.
        /// </summary>
        [ThriftMethod( "getSchedule" )]
        Task<ScheduleResponse> GetScheduleAsync( [ThriftParameter( 1, "req" )] ScheduleRequest request );
    }
}