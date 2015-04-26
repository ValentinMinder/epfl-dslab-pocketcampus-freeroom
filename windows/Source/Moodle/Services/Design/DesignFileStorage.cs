// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IFileStorage

#if DEBUG
using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services.Design
{
    public sealed class DesignFileStorage : IFileStorage
    {
        public Task StoreFileAsync( MoodleFile file, byte[] content )
        {
            return Task.FromResult( 0 );
        }

        public Task<bool> IsStoredAsync( MoodleFile file )
        {
            return Task.FromResult( false );
        }

        public Task OpenFileAsync( MoodleFile file )
        {
            return Task.FromResult( 0 );
        }
    }
}
#endif