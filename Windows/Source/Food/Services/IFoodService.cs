// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Food.Models;
using ThriftSharp;

namespace PocketCampus.Food.Services
{
    [ThriftService( "FoodService" )]
    public interface IFoodService
    {
        [ThriftMethod( "getFood" )]
        Task<FoodResponse> GetMenusAsync( [ThriftParameter( 1, "foodReq" )] FoodRequest request, CancellationToken cancellationToken );

        [ThriftMethod( "vote" )]
        Task<VoteResponse> VoteAsync( [ThriftParameter( 1, "voteReq" )] VoteRequest request );
    }
}