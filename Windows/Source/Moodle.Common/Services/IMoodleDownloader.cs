// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Downloads Moodle files.
    /// </summary>
    public interface IMoodleDownloader
    {
        /// <summary>
        /// Asynchronously downloads a file from the specified URL, with the specified Moodle cookie.
        /// </summary>
        Task<byte[]> DownloadAsync( string url, string cookie );
    }
}