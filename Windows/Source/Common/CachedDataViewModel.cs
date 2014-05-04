using System;
using System.Threading;
using System.Threading.Tasks;
using ThinMvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// DataViewModel that can cache data.
    /// </summary>
    /// <typeparam name="TArg">The viewmodel's argument type.</typeparam>
    /// <typeparam name="TData">The type of the cached data.</typeparam>
    public abstract class CachedDataViewModel<TArg, TData> : DataViewModel<TArg>
    {
        private const int DefaultId = 0;
        private static readonly DateTime DefaultExpirationDate = DateTime.MaxValue;

        private readonly IDataCache _cache;

        private CacheStatus _cacheStatus;

        public CacheStatus CacheStatus
        {
            get { return _cacheStatus; }
            private set { SetProperty( ref _cacheStatus, value ); }
        }


        /// <summary>
        /// Creates a new instance of CachedDataViewModel that uses the specified data cache.
        /// </summary>
        /// <param name="cache">The data cache.</param>
        protected CachedDataViewModel( IDataCache cache )
        {
            _cache = cache;
        }


        protected abstract CachedTask<TData> GetData( bool force, CancellationToken token );

        protected abstract bool HandleData( TData data, CancellationToken token );


        protected override sealed async Task RefreshAsync( CancellationToken token, bool force )
        {
            var cachedData = GetData( force, token );

            TData data;
            if ( cachedData.ShouldBeCached && _cache.TryGet( this.GetType(), cachedData.Id ?? DefaultId, out data ) )
            {
                CacheStatus = CacheStatus.UsedTemporarily;
                HandleData( data, token );
            }
            else
            {
                CacheStatus = CacheStatus.Loading;
            }

            if ( !cachedData.HasNewData )
            {
                return;
            }

            try
            {
                data = await cachedData.GetDataAsync();
                if ( HandleData( data, token ) && cachedData.ShouldBeCached )
                {
                    _cache.Set( this.GetType(), cachedData.Id ?? DefaultId, cachedData.ExpirationDate ?? DefaultExpirationDate, data );
                    CacheStatus = CacheStatus.Unused;
                }
                else
                {
                    CacheStatus = CacheStatus.OptedOut;
                }
            }
            catch ( Exception e )
            {
#warning Change this!
                if ( e is System.Net.WebException || e is System.OperationCanceledException )
                {
                    if ( CacheStatus == CacheStatus.UsedTemporarily )
                    {
                        CacheStatus = CacheStatus.Used;
                    }
                }

                if ( CacheStatus != CacheStatus.Used )
                {
                    CacheStatus = CacheStatus.NoCache;
                    throw;
                }
            }
        }
    }
}