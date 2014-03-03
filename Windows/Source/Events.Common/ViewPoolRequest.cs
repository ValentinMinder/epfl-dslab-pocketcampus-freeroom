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