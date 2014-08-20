// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services
{
    /// <summary>
    /// Stores Moodle files.
    /// </summary>
    public interface IFileStorage
    {
        /// <summary>
        /// Asynchronously stores the specified Moodle file with the specified content.
        /// </summary>
        Task StoreFileAsync( CourseFile file, byte[] content );

        /// <summary>
        /// Asynchronously indicates whether the specified Moodle file is stored on the device.
        /// </summary>
        Task<bool> IsStoredAsync( CourseFile file );

        /// <summary>
        /// Asynchronously opens the specified Moodle file.
        /// </summary>
        Task OpenFileAsync( CourseFile file );
    }
}