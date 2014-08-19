// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    public sealed class ViewEventPoolRequest
    {
        /// <summary>
        /// Gets the pool's ID.
        /// </summary>
        public long PoolId { get; private set; }

        /// <summary>
        /// Gets the user's ticket, if there is one.
        /// </summary>
        public string UserTicket { get; private set; }


        /// <summary>
        /// Creates a new ViewEventPoolRequest.
        /// </summary>
        public ViewEventPoolRequest( long poolId, string userTicket = null )
        {
            PoolId = poolId;
            UserTicket = userTicket;
        }
    }
}