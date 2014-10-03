// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Food.Models;
using ThriftSharp;

namespace PocketCampus.Food.Services
{
    /// <summary>
    /// The food server service.
    /// </summary>
    [ThriftService( "FoodService" )]
    public interface IFoodService
    {
        /// <summary>
        /// Asynchronously gets the menus for the specified request.
        /// </summary>
        [ThriftMethod( "getFood" )]
        Task<FoodResponse> GetMenusAsync( [ThriftParameter( 1, "foodReq" )] FoodRequest request, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously submits the specified vote request.
        /// </summary>
        [ThriftMethod( "vote" )]
        Task<VoteResponse> VoteAsync( [ThriftParameter( 1, "voteReq" )] VoteRequest request );
    }
}