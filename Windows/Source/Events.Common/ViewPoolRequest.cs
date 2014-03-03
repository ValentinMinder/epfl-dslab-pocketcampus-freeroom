// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    public sealed class ViewPoolRequest
    {
        public long PoolId { get; set; }

        public string UserTicket { get; set; }

        public ViewPoolRequest( long poolId, string userTicket = null )
        {
            PoolId = poolId;
            UserTicket = userTicket;
        }
    }
}