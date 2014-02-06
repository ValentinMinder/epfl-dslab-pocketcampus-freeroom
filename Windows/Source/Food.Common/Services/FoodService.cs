// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Food.Models;
using ThriftSharp;

// Plumbing for IFoodService

namespace PocketCampus.Food.Services
{
    public sealed class FoodService : ThriftServiceImplementation<IFoodService>, IFoodService
    {
        public FoodService( IServerConfiguration config )
            : base( config.CreateCommunication( "food" ) )
        {

        }

        public Task<FoodResponse> GetMenusAsync( FoodRequest request )
        {
            return CallAsync<FoodRequest, FoodResponse>( x => x.GetMenusAsync, request );
        }

        public Task<VoteResponse> VoteAsync( VoteRequest request )
        {
            return CallAsync<VoteRequest, VoteResponse>( x => x.VoteAsync, request );
        }
    }
}