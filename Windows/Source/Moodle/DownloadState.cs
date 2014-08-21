// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Moodle
{
    /// <summary>
    /// Display-friendly file download statuses.
    /// </summary>
    public enum DownloadState
    {
        /// <summary>
        /// No file download is in progress.
        /// </summary>
        None,

        /// <summary>
        /// A file is currently being downloaded.
        /// </summary>
        Downloading,

        /// <summary>
        /// An error occurred during a file download.
        /// </summary>
        Error
    }
}