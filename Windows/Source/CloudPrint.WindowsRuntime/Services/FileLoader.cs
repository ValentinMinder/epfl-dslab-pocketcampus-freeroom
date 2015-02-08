// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Windows.Storage;

namespace PocketCampus.CloudPrint.Services
{
    public sealed class FileLoader : IFileLoader
    {
        public Task<Stream> GetFileAsync( Uri uri )
        {
            if ( uri.IsFile )
            {
                return GetFileStreamAsync( uri );
            }

            return GetOnlineStreamAsync( uri );
        }

        public async Task DeleteFileAsync( Uri uri )
        {
            var file = await GetStorageFileAsync( uri );
            await file.DeleteAsync();
        }

        private static async Task<Stream> GetFileStreamAsync( Uri fileUri )
        {
            var file = await GetStorageFileAsync( fileUri );
            var stream = await file.OpenReadAsync();
            return stream.AsStreamForRead();
        }

        private static Task<Stream> GetOnlineStreamAsync( Uri uri )
        {
            return new HttpClient().GetStreamAsync( uri );
        }

        private static Task<StorageFile> GetStorageFileAsync( Uri fileUri )
        {
            // GetFileFromPathAsync requires backslashes...
            string path = WebUtility.UrlDecode( fileUri.AbsolutePath ).Replace( '/', '\\' );
            return StorageFile.GetFileFromPathAsync( path ).AsTask();
        }
    }
}