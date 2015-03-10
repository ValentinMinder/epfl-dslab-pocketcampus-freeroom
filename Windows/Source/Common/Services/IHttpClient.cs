// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// An HTTP client able to make GET and POST requests.
    /// </summary>
    public interface IHttpClient
    {
        /// <summary>
        /// Asynchronously gets a resource (using HTTP GET) from the specified URL, with optional parameters.
        /// </summary>
        Task<HttpResponse> GetAsync( string url, IDictionary<string, string> parameters = null, Encoding encoding = null );

        /// <summary>
        /// Asynchronously gets a resource (using HTTP POST) from the specified URL, with the specified parameters.
        /// </summary>
        Task<HttpResponse> PostAsync( string url, IDictionary<string, string> parameters, Encoding encoding = null );

        /// <summary>
        /// Asynchronously downloads a file as a byte array from the specified URL.
        /// </summary>
        Task<byte[]> DownloadAsync( string url );
    }
}