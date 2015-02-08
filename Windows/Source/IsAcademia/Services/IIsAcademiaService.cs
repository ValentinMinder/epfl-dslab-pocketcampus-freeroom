// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Services
{
    [ThriftService( "IsAcademiaService" )]
    public interface IIsAcademiaService
    {
        [ThriftMethod( "getSchedule" )]
        Task<ScheduleResponse> GetScheduleAsync( [ThriftParameter( 1, "req" )] ScheduleRequest request, CancellationToken cancellationToken );
    }
}