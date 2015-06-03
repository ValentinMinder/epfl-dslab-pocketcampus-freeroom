// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMoodleDownloader

#if DEBUG
using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services.Design
{
    public sealed class DesignMoodleDownloader : IMoodleDownloader
    {
        public Task<byte[]> DownloadAsync( MoodleFile file )
        {
            return Task.FromResult( new byte[0] );
        }
    }
}
#endif