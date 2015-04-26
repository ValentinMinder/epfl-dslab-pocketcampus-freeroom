// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using Windows.Web.Http;

namespace PocketCampus.Moodle.Services
{
    public sealed class MoodleDownloader : IMoodleDownloader
    {
        private const string PluginName = "raw-moodle";
        private const string ActionKey = "action";
        private const string ActionValue = "download_file";
        private const string FilePathKey = "file_path";

        private readonly IServerSettings _settings;
        private readonly IHttpHeaders _headers;


        public MoodleDownloader( IServerSettings settings, IHttpHeaders headers )
        {
            _settings = settings;
            _headers = headers;
        }


        public async Task<byte[]> DownloadAsync( MoodleFile file )
        {
            var client = new HttpClient();
            foreach ( var pair in _headers.Current )
            {
                client.DefaultRequestHeaders.Add( pair.Key, pair.Value );
            }

            var postParams = new Dictionary<string, string>
            {
                { ActionKey, ActionValue },
                { FilePathKey, file.DownloadUrl }
            };

            string downloadUrl = _settings.Configuration.ServerBaseUrl + PluginName;
            var uri = new Uri( downloadUrl, UriKind.Absolute );

            var response = await client.PostAsync( uri, new HttpFormUrlEncodedContent( postParams ) );
            var buffer = await response.Content.ReadAsBufferAsync();
            return buffer.ToArray();
        }
    }
}