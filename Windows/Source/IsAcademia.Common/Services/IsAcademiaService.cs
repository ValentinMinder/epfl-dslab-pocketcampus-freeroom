// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using ThriftSharp;

// Plumbing for IScheduleService

namespace PocketCampus.IsAcademia.Services
{
    public sealed class IsAcademiaService : ThriftServiceImplementation<IIsAcademiaService>, IIsAcademiaService
    {
        public IsAcademiaService( IServerAccess access )
            : base( access.CreateCommunication( "isacademia" ) )
        {

        }

        public Task<TokenResponse> GetTokenAsync()
        {
            return CallAsync<TokenResponse>( x => x.GetTokenAsync );
        }

        public Task<SessionResponse> GetSessionAsync( string token )
        {
            return CallAsync<string, SessionResponse>( x => x.GetSessionAsync, token );
        }

        async Task<AuthenticationToken> ITwoStepAuthenticator<AuthenticationToken, string>.GetTokenAsync()
        {
            var response = await GetTokenAsync();
            if ( response.Status == ResponseStatus.Ok )
            {
                return new AuthenticationToken( response.Token );
            }
            throw new Exception( "An error occurred on the server." );
        }

        async Task<string> ITwoStepAuthenticator<AuthenticationToken, string>.GetSessionAsync( AuthenticationToken token )
        {
            var response = await GetSessionAsync( token.AuthenticationKey );
            if ( response.Status == ResponseStatus.Ok )
            {
                return response.Session;
            }
            throw new Exception( "An error occurred on the server." );
        }


        public Task<ScheduleResponse> GetScheduleAsync( ScheduleRequest request )
        {
            return CallAsync<ScheduleRequest, ScheduleResponse>( x => x.GetScheduleAsync, request );
        }
    }
}