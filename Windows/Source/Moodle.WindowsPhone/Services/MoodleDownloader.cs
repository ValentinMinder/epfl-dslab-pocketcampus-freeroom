// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Downloads Moodle files.
    /// </summary>
    public sealed class MoodleDownloader : IMoodleDownloader
    {
        private const string SessionHeaderName = "X-PC-AUTH-PCSESSID";

        private const string DownloadUrlFormat = "{0}://{1}:{2}/v3r1/raw-moodle";
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
        /// Asynchronously downloads the specified Moodle file.
        /// </summary>
        public async Task<byte[]> DownloadAsync( MoodleFile file )
        {
            var client = new HttpClient(); // not the PocketCampus HTTP client, the .NET one
            client.DefaultRequestHeaders.Add( SessionHeaderName, _serverSettings.Session );

            var postParams = new Dictionary<string, string>
            {
                { ActionKey, ActionValue },
                { FilePathKey, file.DownloadUrl }
            };
            string downloadUrl = string.Format( DownloadUrlFormat, _serverSettings.Configuration.Protocol, _serverSettings.Configuration.Address, _serverSettings.Configuration.Port );

            var response = await client.PostAsync( downloadUrl, new FormUrlEncodedContent( postParams ) );
            return await response.Content.ReadAsByteArrayAsync();
        }
    }
}