// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Net.Http;
using System.Threading.Tasks;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Downloads Moodle files.
    /// </summary>
    public sealed class MoodleDownloader : IMoodleDownloader
    {
        /// <summary>
        /// Asynchronously downloads a file from the specified URL, with the specified Moodle cookie.
        /// </summary>
        public Task<byte[]> DownloadAsync( string url, string cookie )
        {
            var client = new HttpClient(); // not the PocketCampus HTTP client, the .NET one
            client.DefaultRequestHeaders.Add( "Cookie", cookie );
            return client.GetByteArrayAsync( url );
        }
    }
}