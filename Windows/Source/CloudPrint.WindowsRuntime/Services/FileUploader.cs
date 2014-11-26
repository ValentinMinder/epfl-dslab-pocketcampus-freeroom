﻿using System;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using Windows.Web.Http;

namespace PocketCampus.CloudPrint.Services
{
    public sealed class FileUploader : IFileUploader
    {
        private const string PluginName = "raw-cloudprint";
        private const string ContentDispositionHeader = "Content-Disposition";
        private const string ContentDispositionHeaderFormat = "attachment; filename={0}";

        private readonly IServerSettings _settings;
        private readonly IHttpHeaders _headers;

        public FileUploader( IServerSettings settings, IHttpHeaders headers )
        {
            _settings = settings;
            _headers = headers;
        }

        public async Task<long> UploadFileAsync( string fileName, Stream fileContent )
        {
            var client = new HttpClient();
            foreach ( var pair in _headers.Current )
            {
                client.DefaultRequestHeaders.Add( pair.Key, pair.Value );
            }
            client.DefaultRequestHeaders.Add( ContentDispositionHeader, string.Format( ContentDispositionHeaderFormat, fileName ) );


            string downloadUrl = _settings.Configuration.ServerBaseUrl + PluginName;
            var uri = new Uri( downloadUrl, UriKind.Absolute );

            var response = await client.PostAsync( uri, new HttpStreamContent( fileContent.AsInputStream() ) );
            var responseStream = await response.Content.ReadAsInputStreamAsync();

            var serializer = new DataContractJsonSerializer( typeof( CloudPrintUploadResponse ) );
            var responseObj = (CloudPrintUploadResponse) serializer.ReadObject( responseStream.AsStreamForRead() );
            return responseObj.DocumentId;
        }


        [DataContract]
        private sealed class CloudPrintUploadResponse
        {
            [DataMember( Name = "file_id" )]
            public long DocumentId { get; set; }
        }
    }
}