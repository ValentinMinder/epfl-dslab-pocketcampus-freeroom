// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services
{
    public interface IFileStorage
    {
        Task StoreFileAsync( MoodleFile file, byte[] content );

        Task<bool> IsStoredAsync( MoodleFile file );

        Task OpenFileAsync( MoodleFile file );
    }
}