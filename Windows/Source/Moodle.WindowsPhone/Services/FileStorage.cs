// Copyright (c) PocketCampus.Org 2014
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
    /// <summary>
    /// Stores Moodle files in the Windows Phone application data.
    /// </summary>
    public sealed class FileStorage : IFileStorage
    {
        private const string NameExtensionSeparator = ".";
        private const string DefaultExtension = "txt"; // for files without one

        /// <summary>
        /// Asynchronously stores the specified Moodle file with the specified content.
        /// </summary>
        public async Task StoreFileAsync( MoodleFile moodleFile, byte[] content )
        {
            var file = await GetFileAsync( moodleFile, true );
            using ( var stream = await file.OpenStreamForWriteAsync() )
            {
                stream.Write( content, 0, content.Length );
            }
        }

        /// <summary>
        /// Asynchronously indicates whether the specified Moodle file is stored on the device.
        /// </summary>
        public async Task<bool> IsStoredAsync( MoodleFile moodleFile )
        {
            return await GetFileAsync( moodleFile, false ) != null;
        }

        /// <summary>
        /// Asynchronously opens the specified Moodle file.
        /// </summary>
        public async Task OpenFileAsync( MoodleFile moodleFile )
        {
            var file = await GetFileAsync( moodleFile, false );
            await Launcher.LaunchFileAsync( file );
        }

        /// <summary>
        /// Gets a file. Optionally creates it if it doesn't exist.
        /// </summary>
        private static async Task<StorageFile> GetFileAsync( MoodleFile file, bool create )
        {
            var folder = ApplicationData.Current.LocalFolder;
            foreach ( string name in file.PathComponents )
            {
                folder = await folder.CreateFolderAsync( FixName( name, Path.GetInvalidPathChars() ), CreationCollisionOption.OpenIfExists );
            }
            string extension = string.IsNullOrWhiteSpace( file.Extension ) ? DefaultExtension : file.Extension;
            string fileName = FixName( file.Name, Path.GetInvalidFileNameChars() ) + NameExtensionSeparator + extension;

            // GetFileAsync throws an exception if the file doesn't exist
            // and there's no API to check for a file's existence
            // so we have to use GetFilesAsync...
            var storageFile = ( await folder.GetFilesAsync( CommonFileQuery.DefaultQuery ) )
                                            .FirstOrDefault( f => f.Name == fileName );
            if ( storageFile == null && create )
            {
                storageFile = await folder.CreateFileAsync( fileName );
            }
            return storageFile;
        }

        /// <summary>
        /// Fixes a name by removing invalid characters.
        /// </summary>
        private static string FixName( string name, char[] invalidChars )
        {
            name = invalidChars.Aggregate( name, ( s, c ) => s.Replace( c.ToString(), "" ) );
            return string.IsNullOrWhiteSpace( name ) ? name.GetHashCode().ToString() : name; // awful, but better than a crash
        }
    }
}