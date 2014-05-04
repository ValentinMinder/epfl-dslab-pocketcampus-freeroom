namespace PocketCampus.Common
{
    /// <summary>
    /// The possible statuses of cache usage.
    /// </summary>
    public enum CacheStatus
    {
        /// <summary>
        /// No cache is available.
        /// </summary>
        NoCache,

        /// <summary>
        /// Data is being loaded.
        /// </summary>
        Loading,

        /// <summary>
        /// Cached data is used temporarily while loading data.
        /// </summary>
        UsedTemporarily,

        /// <summary>
        /// Cached data is used because live data is not available.
        /// </summary>
        Used,

        /// <summary>
        /// The code opted out of using caching, and live data is used.
        /// </summary>
        OptedOut,

        /// <summary>
        /// Live data is used.
        /// </summary>
        Unused
    }
}