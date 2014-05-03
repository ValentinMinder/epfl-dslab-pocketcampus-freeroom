using System;
using System.Threading.Tasks;
using ThinMvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// DataViewModel that can cache data.
    /// </summary>
    /// <typeparam name="TArg">The viewmodel's argument type.</typeparam>
    public abstract class CachedDataViewModel<TArg> : DataViewModel<TArg>
    {
        private const int DefaultId = 0;
        private static readonly DateTime DefaultExpirationDate = DateTime.MaxValue;

        private readonly IDataCache _cache;

        private bool _isDataCached;

        /// <summary>
        /// Gets a value indicating whether the last call to <see cref="GetWithCache" /> returned cached data.
        /// </summary>
        public bool IsDataCached
        {
            get { return _isDataCached; }
            protected set { SetProperty( ref _isDataCached, value ); }
        }


        /// <summary>
        /// Creates a new instance of CachedDataViewModel that uses the specified data cache.
        /// </summary>
        /// <param name="cache">The data cache.</param>
        protected CachedDataViewModel( IDataCache cache )
        {
            _cache = cache;
        }


        /// <summary>
        /// Asynchronously gets data with the specified getter method and caches it, or returns cached data if the method throws a network error.
        /// </summary>
        /// <typeparam name="T">The data type.</typeparam>
        /// <param name="getter">The data getter method.</param>
        /// <param name="expirationDate">The date until which to cache the data, if it's fetched successfully.</param>
        /// <returns>The data, which may be cached.</returns>
        /// <remarks>
        /// Exceptions are considered to be network errors if they are (or inherit from) one of the declared network exception types in
        /// <see cref="DataViewModelOptions" />.
        /// </remarks>
        protected async Task<T> GetWithCacheAsync<T>( Func<Task<T>> getter, int? id = null, DateTime? expirationDate = null )
        {
            try
            {
                var value = await getter();

                _cache.Set( this.GetType(), id ?? DefaultId, expirationDate ?? DefaultExpirationDate, value );
                IsDataCached = false;
                return value;
            }
            catch ( Exception e )
            {
#warning Change this!
                if ( e is System.Net.WebException || e is System.OperationCanceledException )
                {
                    T data;
                    if ( _cache.TryGet( this.GetType(), id ?? DefaultId, out data ) )
                    {
                        IsDataCached = true;
                        return data;
                    }
                }
                throw;
            }
        }

        /// <summary>
        /// Clears the cache.
        /// </summary>
        protected void ClearCache()
        {
            _cache.Remove( this.GetType() );
        }
    }
}