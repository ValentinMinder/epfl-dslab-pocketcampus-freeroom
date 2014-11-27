// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Downloads Moodle files.
    /// </summary>
    public interface IMoodleDownloader
    {
        /// <summary>
        /// Asynchronously downloads the specified file.
        /// </summary>
        Task<byte[]> DownloadAsync( MoodleFile file );
    }
}