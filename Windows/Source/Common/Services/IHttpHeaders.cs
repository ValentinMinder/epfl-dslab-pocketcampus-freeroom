using System.Collections.Generic;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Infrastructure.
    /// Contains HTTP headers for server communication.
    /// </summary>
    public interface IHttpHeaders
    {
        /// <summary>
        /// Gets the HTTP headers that must be sent with each request.
        /// </summary>
        IReadOnlyDictionary<string, string> Current { get; }
    }
}