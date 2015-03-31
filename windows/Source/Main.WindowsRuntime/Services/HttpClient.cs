// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using Windows.Security.ExchangeActiveSyncProvisioning;
using Windows.Web.Http;
using WinHttpClient = Windows.Web.Http.HttpClient;

namespace PocketCampus.Main.Services
{
    public sealed class HttpClient : IHttpClient
    {
        private const string UserAgentFormat = "PocketCampus/{0} ({1}; {2} {3})";
        private static readonly TimeSpan Timeout = TimeSpan.FromSeconds( 10 );
        private static readonly Encoding DefaultEncoding = Encoding.UTF8;

        /// <summary>
        /// Asynchronously gets a web page (using a GET query).
        /// </summary>
        /// <param name="url">The web page URL.</param>
        /// <param name="parameters">Optional. The query parameters.</param>
        /// <param name="encoding">Optional. The encoding. UTF-8 by default.</param>
        public async Task<HttpResponse> GetAsync( string url, IDictionary<string, string> parameters = null, Encoding encoding = null )
        {
            var client = GetHttpClient();
            var uri = new Uri( url + GetParametersString( parameters ), UriKind.Absolute );
            var tokenSource = new CancellationTokenSource( Timeout );
            var response = await client.GetAsync( uri ).AsTask( tokenSource.Token );
            return await ProcessResponseAsync( response, encoding ?? DefaultEncoding );
        }

        /// <summary>
        /// Asynchronously posts the specified query to the specified URL (using a POST query).
        /// </summary>
        /// <param name="url">The query URL.</param>
        /// <param name="parameters">The query parameters.</param>
        /// <param name="encoding">Optional. The encoding. UTF-8 by default.</param>
        public async Task<HttpResponse> PostAsync( string url, IDictionary<string, string> parameters, Encoding encoding = null )
        {
            var client = GetHttpClient();
            var uri = new Uri( url, UriKind.Absolute );
            var tokenSource = new CancellationTokenSource( Timeout );
            var response = await client.PostAsync( uri, new HttpFormUrlEncodedContent( parameters ) ).AsTask( tokenSource.Token );
            return await ProcessResponseAsync( response, encoding ?? DefaultEncoding );
        }

        /// <summary>
        /// Asynchronously downloads a file as a byte array from the specified URL.
        /// </summary>
        public async Task<byte[]> DownloadAsync( string url )
        {
            var client = GetHttpClient();
            var uri = new Uri( url, UriKind.Absolute );
            var tokenSource = new CancellationTokenSource( Timeout );
            var buffer = await client.GetBufferAsync( uri ).AsTask( tokenSource.Token );
            return buffer.ToArray();
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
            return "?" + string.Join( "&", parameters.Select( p => p.Key + "=" + WebUtility.UrlEncode( p.Value ) ) );
        }

        /// <summary>
        /// Asynchronously processes an HTTP response message, ensuring it was successful and extracting its content.
        /// </summary>
        private static async Task<HttpResponse> ProcessResponseAsync( HttpResponseMessage response, Encoding encoding )
        {
            response.EnsureSuccessStatusCode();
            var buffer = await response.Content.ReadAsBufferAsync();
            byte[] bytes = buffer.ToArray();
            string content = encoding.GetString( bytes, 0, bytes.Length );
            string requestUrl = response.RequestMessage.RequestUri.ToString();
            return new HttpResponse( content, requestUrl );
        }

        private static WinHttpClient GetHttpClient()
        {
            var client = new WinHttpClient();

            var info = new EasClientDeviceInformation();
            var currentAssemblyName = typeof( HttpClient ).GetTypeInfo().Assembly.GetName();
            string userAgent = string.Format(
                UserAgentFormat,
                currentAssemblyName.Version.ToString( 2 ),
                info.OperatingSystem, info.SystemManufacturer, info.SystemProductName );

            client.DefaultRequestHeaders.UserAgent.ParseAdd( userAgent );

            return client;
        }
    }
}