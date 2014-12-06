using System;
using System.IO;
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

        private static async Task<Stream> GetFileStreamAsync( Uri fileUri )
        {
            var file = await StorageFile.GetFileFromPathAsync( fileUri.ToString() );
            return ( await file.OpenReadAsync() ).AsStreamForRead();
        }

        private static Task<Stream> GetOnlineStreamAsync( Uri uri )
        {
            return new HttpClient().GetStreamAsync( uri );
        }
    }
}