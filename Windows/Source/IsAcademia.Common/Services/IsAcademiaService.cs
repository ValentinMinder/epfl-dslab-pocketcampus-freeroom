// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

// Plumbing for IIsAcademiaService

namespace PocketCampus.IsAcademia.Services
{
    public sealed class IsAcademiaService : ThriftServiceImplementation<IIsAcademiaService>, IIsAcademiaService
    {
        public IsAcademiaService( IServerAccess access )
            : base( access.CreateCommunication( "isacademia" ) )
        {

        }

        public Task<ScheduleResponse> GetScheduleAsync( ScheduleRequest request )
        {
            return CallAsync<ScheduleRequest, ScheduleResponse>( x => x.GetScheduleAsync, request );
        }
    }
}