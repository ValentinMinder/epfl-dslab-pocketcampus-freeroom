// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common
{
    /// <summary>
    /// Response to an HTTP request.
    /// </summary>
    public sealed class HttpResponse
    {
        /// <summary>
        /// Gets the response's content.
        /// </summary>
        public string Content { get; private set; }

        /// <summary>
        /// Gets the final URL of the response, taking redirects into account.
        /// </summary>
        public string RequestUrl { get; private set; }


        /// <summary>
        /// Creates a new HttpResponse.
        /// </summary>
        public HttpResponse( string content, string requestUrl )
        {
            Content = content;
            RequestUrl = requestUrl;
        }
    }
}