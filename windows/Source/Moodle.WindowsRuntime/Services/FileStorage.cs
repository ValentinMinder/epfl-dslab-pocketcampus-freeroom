// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Moodle.Models;
using Windows.Storage;
using Windows.Storage.Search;
using Windows.System;

namespace PocketCampus.Moodle.Services
{
    public sealed class FileStorage : IFileStorage
    {
        private const string NameExtensionSeparator = ".";
        private const string DefaultExtension = "txt"; // for files without one


        public async Task StoreFileAsync( MoodleFile moodleFile, byte[] content )
        {
            var file = await GetFileAsync( moodleFile, true );
            using ( var stream = await file.OpenStreamForWriteAsync() )
            {
                stream.Write( content, 0, content.Length );
            }
        }

        public async Task<bool> IsStoredAsync( MoodleFile moodleFile )
        {
            return await GetFileAsync( moodleFile, false ) != null;
        }

        public async Task OpenFileAsync( MoodleFile moodleFile )
        {
            var file = await GetFileAsync( moodleFile, false );
            await Launcher.LaunchFileAsync( file );
        }


        private static async Task<StorageFile> GetFileAsync( MoodleFile file, bool create )
        {
            // TODO: Find a way to export docs to the Documents library or something
            var folder = ApplicationData.Current.LocalCacheFolder;
            foreach ( string name in file.PathComponents )
            {
                folder = await folder.CreateFolderAsync( FixName( name, Path.GetInvalidPathChars() ), CreationCollisionOption.OpenIfExists );
            }
            string extension = string.IsNullOrWhiteSpace( file.Extension ) ? DefaultExtension : file.Extension;
            string fileName = FixName( file.Name + NameExtensionSeparator + extension, Path.GetInvalidFileNameChars() );

            // GetFileAsync throws an exception if the file doesn't exist
            // and there's no API to check for a file's existence
            // so we have to use GetFilesAsync...
            var storageFile = ( await folder.GetFilesAsync( CommonFileQuery.DefaultQuery ) )
                                            .FirstOrDefault( f => f.Name == fileName );
            if ( storageFile == null && create )
            {
                storageFile = await folder.CreateFileAsync( fileName, CreationCollisionOption.ReplaceExisting );
            }
            return storageFile;
        }

        private static string FixName( string name, char[] invalidChars )
        {
            string fixedName = invalidChars.Aggregate( name, ( s, c ) => s.Replace( c.ToString(), "" ) );
            return string.IsNullOrWhiteSpace( fixedName ) ? name.GetHashCode().ToString() : fixedName; // awful, but better than a crash
        }
    }
}