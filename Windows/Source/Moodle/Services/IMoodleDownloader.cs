// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle.Services
{
    public interface IMoodleDownloader
    {
        Task<byte[]> DownloadAsync( MoodleFile file );
    }
}