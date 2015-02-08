// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

// Plumbing for IIsAcademiaService

namespace PocketCampus.IsAcademia.Services
{
    public sealed class IsAcademiaService : ThriftServiceImplementation<IIsAcademiaService>, IIsAcademiaService
    {
        public IsAcademiaService( IServerAccess access ) : base( access.CreateCommunication( "isacademia" ) ) { }

        public Task<ScheduleResponse> GetScheduleAsync( ScheduleRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<ScheduleRequest, CancellationToken, ScheduleResponse>( x => x.GetScheduleAsync, request, cancellationToken );
        }
    }
}