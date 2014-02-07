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
    public interface IIsAcademiaService : ITwoStepAuthenticator<AuthenticationToken, string>
    {
        /// <summary>
        /// First authentication step: asynchronously gets a token.
        /// </summary>
        [ThriftMethod( "getIsaTequilaToken" )]
        new Task<TokenResponse> GetTokenAsync();

        /// <summary>
        /// Second authentication step: get a session from a token.
        /// </summary>
        [ThriftMethod( "getIsaSessionId" )]
        Task<SessionResponse> GetSessionAsync( [ThriftParameter( 1, "tequilaToken" )] string token );

        /// <summary>
        /// Asynchronously gets the schedule for the specified request.
        /// </summary>
        [ThriftMethod( "getSchedule" )]
        Task<ScheduleResponse> GetScheduleAsync( [ThriftParameter( 1, "req" )] ScheduleRequest request );
    }
}