// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using ThriftSharp;

// Plumbing for IFoodService

namespace PocketCampus.Food.Services
{
    public sealed class FoodService : ThriftServiceImplementation<IFoodService>, IFoodService
    {
        public FoodService( IServerAccess access ) : base( access.CreateCommunication( "food" ) ) { }

        public Task<FoodResponse> GetMenusAsync( FoodRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<FoodRequest, CancellationToken, FoodResponse>( x => x.GetMenusAsync, request, cancellationToken );
        }

        public Task<VoteResponse> VoteAsync( VoteRequest request )
        {
            return CallAsync<VoteRequest, VoteResponse>( x => x.VoteAsync, request );
        }
    }
}