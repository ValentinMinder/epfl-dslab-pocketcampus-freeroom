// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Services
{
    /// <summary>
    /// The IS-Academia server service.
    /// </summary>
    [ThriftService( "IsAcademiaService" )]
    public interface IIsAcademiaService
    {
        /// <summary>
        /// Asynchronously gets the schedule for the specified request.
        /// </summary>
        [ThriftMethod( "getSchedule" )]
        Task<ScheduleResponse> GetScheduleAsync( [ThriftParameter( 1, "req" )] ScheduleRequest request, CancellationToken cancellationToken );
    }
}