// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// An HTTP client able to make GET and POST requests.
    /// </summary>
    public sealed class HttpClient : IHttpClient
    {
        private static readonly Encoding DefaultEncoding = Encoding.UTF8;

        // The client used to perform requests; its full name is required to avoid ambiguity
        private System.Net.Http.HttpClient _client;


        /// <summary>
        /// Creates a new HttpClient.
        /// </summary>
        public HttpClient()
        {
            _client = new System.Net.Http.HttpClient( new System.Net.Http.HttpClientHandler { AllowAutoRedirect = false } );
        }


        /// <summary>
        /// Asynchronously gets a web page (using a GET query).
        /// </summary>
        /// <param name="url">The web page URL.</param>
        /// <param name="parameters">Optional. The query parameters.</param>
        public async Task<HttpResponse> GetAsync( string url, IDictionary<string, string> parameters = null, Encoding encoding = null )
        {
            var response = await _client.GetAsync( url + GetParametersString( parameters ) );
            return await ProcessResponseAsync( response, encoding ?? DefaultEncoding );
        }

        /// <summary>
        /// Asynchronously posts the specified query to the specified URL (using a POST query).
        /// </summary>
        /// <param name="url">The query URL.</param>
        /// <param name="parameters">The query parameters.</param>
        public async Task<HttpResponse> PostAsync( string url, IDictionary<string, string> parameters, Encoding encoding = null )
        {
            var response = await _client.PostAsync( url, new FormUrlEncodedContent( parameters ) );
            return await ProcessResponseAsync( response, encoding ?? DefaultEncoding );
        }

        /// <summary>
        /// Asynchronously downloads a file as a byte array from the specified URL.
        /// </summary>
        public Task<byte[]> DownloadAsync( string url )
        {
            return _client.GetByteArrayAsync( url );
        }

        /// <summary>
        /// Transforms GET parameters into a string.
        /// </summary>
        private static string GetParametersString( IDictionary<string, string> parameters )
        {
            if ( parameters == null || parameters.Count == 0 )
            {
                return string.Empty;
            }
            return "?" + string.Join( "&", parameters.Select( p => p.Key + "=" + p.Value ) );
        }

        /// <summary>
        /// Asynchronously processes an HTTP response message, ensuring it was successful and extracting its content.
        /// </summary>
        private static async Task<HttpResponse> ProcessResponseAsync( HttpResponseMessage response, Encoding encoding )
        {
            // HACK: If it's a redirect, just return an empty response, we're interested in the cookies
            if ( (int) response.StatusCode / 100 == 3 )
            {
                return new HttpResponse( "", "" );
            }

            response.EnsureSuccessStatusCode();
            byte[] bytes = await response.Content.ReadAsByteArrayAsync();
            string content = encoding.GetString( bytes, 0, bytes.Length );
            string requestUrl = response.RequestMessage.RequestUri.ToString();
            return new HttpResponse( content, requestUrl );
        }
    }
}