using System;
using System.Threading.Tasks;

namespace PocketCampus.Common
{
    public sealed class CachedTask<T>
    {
        private readonly Func<Task<T>> _getter;

        public int? Id { get; private set; }

        public DateTime? ExpirationDate { get; private set; }

        public bool HasNewData { get; private set; }

        public bool ShouldBeCached { get; private set; }


        internal CachedTask( Func<Task<T>> getter, int? id, DateTime? expirationDate, bool hasNewData, bool shouldBeCached )
        {
            _getter = getter;
            Id = id;
            ExpirationDate = expirationDate;
            HasNewData = hasNewData;
            ShouldBeCached = shouldBeCached;
        }


        public Task<T> GetDataAsync()
        {
            return _getter();
        }
    }

    public static class CachedTask
    {
        public static CachedTask<T> Create<T>( Func<Task<T>> getter, int? id = null, DateTime? expirationDate = null )
        {
            return new CachedTask<T>( getter, id, expirationDate, true, true );
        }

        public static CachedTask<T> DoNotCache<T>( Func<Task<T>> getter )
        {
            return new CachedTask<T>( getter, null, null, true, false );
        }

        public static CachedTask<T> NoNewData<T>()
        {
            return new CachedTask<T>( null, null, null, false, false );
        }
    }
}