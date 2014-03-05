// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using PocketCampus.Common;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Downloads Moodle files.
    /// </summary>
    public sealed class MoodleDownloader : IMoodleDownloader
    {
        private const string SessionHeaderName = "X-PC-AUTH-PCSESSID";

        private const string ActionKey = "action";
        private const string ActionValue = "download_file";
        private const string FilePathKey = "file_path";

        private readonly IServerSettings _serverSettings;


        /// <summary>
        /// Creates a new MoodleDownloader.
        /// </summary>
        public MoodleDownloader( IServerSettings serverSettings )
        {
            _serverSettings = serverSettings;
        }

        /// <summary>
        /// Asynchronously downloads a file from the specified URL, with the specified Moodle cookie.
        /// </summary>
        public async Task<byte[]> DownloadAsync( string url )
        {
            var client = new HttpClient(); // not the PocketCampus HTTP client, the .NET one
            client.DefaultRequestHeaders.Add( SessionHeaderName, _serverSettings.Session );

            var postParams = new Dictionary<string, string>
            {
                { ActionKey, ActionValue },
                { FilePathKey, url }
            };
            string downloadUrl = string.Format( "{0}://pocketcampus.epfl.ch:{1}/v3r1/raw-moodle",
                                                _serverSettings.Configuration.Protocol, _serverSettings.Configuration.Port );

            var resp = await client.PostAsync( downloadUrl, new FormUrlEncodedContent( postParams ) );
            return await resp.Content.ReadAsByteArrayAsync();
        }
    }
}